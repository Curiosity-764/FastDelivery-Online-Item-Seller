package DataAccessObject;

import model.*;
import java.sql.*;
import java.util.*;

public class OrderDAO {

	public String createOrder(Order order, List<OrderItem> items) throws SQLException {
		Connection conn = null;
		PreparedStatement orderStmt = null;
		PreparedStatement itemStmt = null;
		ResultSet generatedKeys = null;
		String orderNumber = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			String orderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, payment_method, status, payment_status) "
					+ "VALUES (?, ?, ?, ?, ?, ?)"; // them order

			orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
			orderStmt.setInt(1, order.getUserId());
			orderStmt.setDouble(2, order.getTotalAmount());
			orderStmt.setString(3, order.getShippingAddress());
			orderStmt.setString(4, order.getPaymentMethod());
			orderStmt.setString(5, order.getStatus());
			orderStmt.setString(6, order.getPaymentStatus());

			int affectedRows = orderStmt.executeUpdate();

			if (affectedRows > 0) {
				generatedKeys = orderStmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					int orderId = generatedKeys.getInt(1);

					String numberSql = "SELECT order_number FROM orders WHERE order_id = ?";
					try (PreparedStatement ps = conn.prepareStatement(numberSql)) {
						ps.setInt(1, orderId);
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							orderNumber = rs.getString("order_number");
						}
					}

					String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price) "
							+ "VALUES (?, ?, ?, ?, ?)";
					itemStmt = conn.prepareStatement(itemSql);

					for (OrderItem item : items) {
						itemStmt.setInt(1, orderId);
						itemStmt.setInt(2, item.getProductId());
						itemStmt.setString(3, item.getProductName());
						itemStmt.setInt(4, item.getQuantity());
						itemStmt.setDouble(5, item.getUnitPrice());
						itemStmt.addBatch();
					}

					itemStmt.executeBatch();

					updateProductStock(conn, items);

					conn.commit();

					return orderNumber;
				}
			}

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
				}
			}
			throw e;
		} finally {
			// Close resources
			if (generatedKeys != null)
				try {
					generatedKeys.close();
				} catch (Exception e) {
				}
			if (itemStmt != null)
				try {
					itemStmt.close();
				} catch (Exception e) {
				}
			if (orderStmt != null)
				try {
					orderStmt.close();
				} catch (Exception e) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}

		return null;
	}

	private void updateProductStock(Connection conn, List<OrderItem> items) throws SQLException {
		String sql = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (OrderItem item : items) {
				ps.setInt(1, item.getQuantity());
				ps.setInt(2, item.getProductId());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	public Order getOrderById(int orderId) {
		Order order = null;
		String sql = "SELECT o.*, u.username, u.email FROM orders o " + "LEFT JOIN users u ON o.user_id = u.user_id "
				+ "WHERE o.order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				order = mapResultSetToOrder(rs);
				order.setItems(getOrderItems(conn, orderId));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return order;
	}

	public Order getOrderByNumber(String orderNumber) {
		Order order = null;
		String sql = "SELECT o.*, u.username, u.email FROM orders o " + "LEFT JOIN users u ON o.user_id = u.user_id "
				+ "WHERE o.order_number = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, orderNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				order = mapResultSetToOrder(rs);
				order.setItems(getOrderItems(conn, order.getOrderId()));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return order;
	}

	// Get all orders for a user
	public List<Order> getOrdersByUser(int userId) {
		List<Order> orders = new ArrayList<>();
		String sql = "SELECT o.*, u.username, u.email FROM orders o " + "LEFT JOIN users u ON o.user_id = u.user_id "
				+ "WHERE o.user_id = ? ORDER BY o.order_date DESC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Order order = mapResultSetToOrder(rs);
				orders.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orders;
	}

	// Admin - only
	public List<Order> getAllOrders() {
		List<Order> orders = new ArrayList<>();
		String sql = "SELECT o.*, u.username, u.email FROM orders o " + "LEFT JOIN users u ON o.user_id = u.user_id "
				+ "ORDER BY o.order_date DESC";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				orders.add(mapResultSetToOrder(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orders;
	}

	// upate thanh toan
	public boolean updateOrderStatus(int orderId, String newStatus) {
		String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, newStatus);
			ps.setInt(2, orderId);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// thanh status cho thanh toan
	public boolean updatePaymentStatus(int orderId, String paymentStatus) {
		String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, paymentStatus);
			ps.setInt(2, orderId);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// huy order
	public boolean cancelOrder(int orderId) {
		Order order = getOrderById(orderId);
		if (order == null || !order.canBeCancelled()) {
			return false;
		}

		return updateOrderStatus(orderId, "CANCELLED");
	}

	private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {
		List<OrderItem> items = new ArrayList<>();
		String sql = "SELECT oi.*, p.image_url FROM order_items oi "
				+ "LEFT JOIN products p ON oi.product_id = p.product_id " + "WHERE oi.order_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, orderId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				OrderItem item = new OrderItem();
				item.setOrderItemId(rs.getInt("order_item_id"));
				item.setOrderId(rs.getInt("order_id"));
				item.setProductId(rs.getInt("product_id"));
				item.setProductName(rs.getString("product_name"));
				item.setQuantity(rs.getInt("quantity"));
				item.setUnitPrice(rs.getDouble("unit_price"));

				Product product = new Product();
				product.setId(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setImage_url(rs.getString("image_url"));
				item.setProduct(product);

				items.add(item);
			}
		}

		return items;
	}

	private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setOrderId(rs.getInt("order_id"));
		order.setOrderNumber(rs.getString("order_number"));
		order.setUserId(rs.getInt("user_id"));
		order.setOrderDate(rs.getTimestamp("order_date"));
		order.setTotalAmount(rs.getDouble("total_amount"));
		order.setStatus(rs.getString("status"));
		order.setShippingAddress(rs.getString("shipping_address"));
		order.setPaymentMethod(rs.getString("payment_method"));
		order.setPaymentStatus(rs.getString("payment_status"));

		// thong tin nguoi dung
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setUsername(rs.getString("username"));
		user.setEmail(rs.getString("email"));
		order.setUser(user);

		return order;
	}
}