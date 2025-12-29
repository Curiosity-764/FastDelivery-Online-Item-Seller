package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Cart;
import model.CartItems;
import model.User;
import model.Order;
import model.OrderItem;
import model.DBConnection;
import DataAccessObject.OrderDAO;
import Constants.Constants;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");

		if (user == null) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Please login to checkout");
			response.sendRedirect(request.getContextPath() + "/auth/login");
			return;
		}

		Cart cart = (Cart) session.getAttribute(Constants.SESSION_CART);
		if (cart == null || cart.isEmpty()) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Your cart is empty");
			response.sendRedirect(request.getContextPath() + "/cart");
			return;
		}

		request.setAttribute("cart", cart);
		request.setAttribute("user", user);
		request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");

		if (user == null) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Please login to checkout");
			response.sendRedirect(request.getContextPath() + "/auth/login");
			return;
		}

		Cart cart = (Cart) session.getAttribute(Constants.SESSION_CART);
		if (cart == null || cart.isEmpty()) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Your cart is empty");
			response.sendRedirect(request.getContextPath() + "/cart");
			return;
		}

		// Get shipping address from form
		String shippingAddress = request.getParameter("shippingAddress");
		String paymentMethod = request.getParameter("paymentMethod");

		if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Please enter shipping address");
			response.sendRedirect(request.getContextPath() + "/checkout");
			return;
		}

		try {
			// Create order using OrderDAO
			OrderDAO orderDAO = new OrderDAO();

			// Calculate total amount (with shipping and tax)
			double subtotal = cart.getTotalPrice();
			double shipping = (subtotal >= 50) ? 0 : 5.00;
			double tax = subtotal * 0.1;
			double totalAmount = subtotal + shipping + tax;

			// Create Order object
			Order order = new Order();
			order.setUserId(user.getUserId());
			order.setTotalAmount(totalAmount);
			order.setShippingAddress(shippingAddress.trim());
			order.setPaymentMethod(paymentMethod != null ? paymentMethod : "COD");
			order.setStatus("PENDING");
			order.setPaymentStatus("PENDING");

			// Create OrderItems from Cart
			List<OrderItem> orderItems = new ArrayList<>();
			for (CartItems cartItem : cart.getItems()) {
				OrderItem orderItem = new OrderItem();
				orderItem.setProductId(cartItem.getProduct().getId());
				orderItem.setProductName(cartItem.getProduct().getProduct_name());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setUnitPrice(cartItem.getProduct().getPrice());
				orderItems.add(orderItem);
			}

			// Save order to database
			String orderNumber = orderDAO.createOrder(order, orderItems);

			if (orderNumber != null && !orderNumber.isEmpty()) {
				// Clear cart
				cart.clear();
				session.setAttribute(Constants.SESSION_CART, cart);

				// Set success message
				session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
						"Order #" + orderNumber + " placed successfully! Thank you for your purchase.");

				// Redirect to order details page
				response.sendRedirect(request.getContextPath() + "/orders/view?number=" + orderNumber);

			} else {
				// Order creation failed
				session.setAttribute(Constants.SESSION_FLASH_ERROR,
						"Failed to place order. Please try again or contact support.");
				response.sendRedirect(request.getContextPath() + "/checkout");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Database error: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/checkout");

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Error processing order: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/checkout");
		}
	}

	// Old method - keeping for reference but not used anymore
	private int createOrderOld(int userId, Cart cart, String shippingAddress, String paymentMethod) {
		Connection conn = null;
		PreparedStatement orderStmt = null;
		PreparedStatement itemStmt = null;
		PreparedStatement updateStockStmt = null;
		ResultSet generatedKeys = null;
		int orderId = -1;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false); // Start transaction

			// 1. Insert order
			String orderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, payment_method, status, payment_status) "
					+ "VALUES (?, ?, ?, ?, 'PENDING', 'PENDING')";

			// Calculate total
			double subtotal = cart.getTotalPrice();
			double shipping = (subtotal >= 50) ? 0 : 5.00;
			double tax = subtotal * 0.1;
			double totalAmount = subtotal + shipping + tax;

			orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
			orderStmt.setInt(1, userId);
			orderStmt.setDouble(2, totalAmount);
			orderStmt.setString(3, shippingAddress);
			orderStmt.setString(4, paymentMethod != null ? paymentMethod : "COD");

			int affectedRows = orderStmt.executeUpdate();

			if (affectedRows > 0) {
				generatedKeys = orderStmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					orderId = generatedKeys.getInt(1);

					// Get the generated order number
					String numberSql = "SELECT order_number FROM orders WHERE order_id = ?";
					try (PreparedStatement ps = conn.prepareStatement(numberSql)) {
						ps.setInt(1, orderId);
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							String orderNumber = rs.getString("order_number");
							System.out.println("Generated order number: " + orderNumber);
						}
					}

					// 2. Insert order items
					String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price) "
							+ "VALUES (?, ?, ?, ?, ?)";
					itemStmt = conn.prepareStatement(itemSql);

					// 3. Update stock
					String updateStockSql = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
					updateStockStmt = conn.prepareStatement(updateStockSql);

					for (CartItems item : cart.getItems()) {
						// Insert order item
						itemStmt.setInt(1, orderId);
						itemStmt.setInt(2, item.getProduct().getId());
						itemStmt.setString(3, item.getProduct().getProduct_name());
						itemStmt.setInt(4, item.getQuantity());
						itemStmt.setDouble(5, item.getProduct().getPrice());
						itemStmt.addBatch();

						// Update product stock
						updateStockStmt.setInt(1, item.getQuantity());
						updateStockStmt.setInt(2, item.getProduct().getId());
						updateStockStmt.addBatch();
					}

					// Execute batch inserts
					itemStmt.executeBatch();
					updateStockStmt.executeBatch();

					// Commit transaction
					conn.commit();

				}
			}

		} catch (SQLException e) {
			try {
				if (conn != null)
					conn.rollback(); // Rollback on error
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			throw new RuntimeException("Database error creating order", e);
		} finally {
			// Close resources
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (Exception e) {
			}
			try {
				if (itemStmt != null)
					itemStmt.close();
			} catch (Exception e) {
			}
			try {
				if (updateStockStmt != null)
					updateStockStmt.close();
			} catch (Exception e) {
			}
			try {
				if (orderStmt != null)
					orderStmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return orderId;
	}
}