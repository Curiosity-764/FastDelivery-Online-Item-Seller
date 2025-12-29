package DataAccessObject;

import java.sql.*;
import java.util.*;
import model.Product;
import model.DBConnection;
import Constants.Constants; 

public class ProductDataAccessObject {

	public List<Product> getAllItems() {
		List<Product> products = new ArrayList<>();

		String sql = Constants.SQL_SELECT_ALL_PRODUCTS;

		try (Connection conn = DBConnection.getConnection()) {

			try (PreparedStatement ps = conn.prepareStatement(Constants.SQL_SELECT_ALL_PRODUCTS);
					ResultSet rs = ps.executeQuery()) {

				int count = 0;
				while (rs.next()) {
					count++;
					Product p = new Product();

					p.setId(rs.getInt(Constants.COL_PRODUCT_ID));
					p.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
					p.setDescription(rs.getString(Constants.COL_DESCRIPTION));
					p.setPrice(rs.getDouble(Constants.COL_PRICE));
					p.setStock(rs.getInt(Constants.COL_STOCK));
					p.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
					p.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));

					products.add(p);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return products;
	}

	public Product getProductById(int id) {
		Product p = null;

		String sql = Constants.SQL_SELECT_PRODUCT_BY_ID;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p = new Product();

				p.setId(rs.getInt(Constants.COL_PRODUCT_ID));
				p.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
				p.setDescription(rs.getString(Constants.COL_DESCRIPTION));
				p.setPrice(rs.getDouble(Constants.COL_PRICE));
				p.setStock(rs.getInt(Constants.COL_STOCK));
				p.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
				p.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return p;
	}

	public List<Product> searchProducts(String keyword) {
		List<Product> products = new ArrayList<>();

		String sql = Constants.SQL_SEARCH_PRODUCTS;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			String searchTerm = "%" + keyword + "%";
			stmt.setString(1, searchTerm);
			stmt.setString(2, searchTerm);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = mapResultSetToProduct(rs);
				products.add(product);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	public List<Product> getProductsByCategory(String category) {
		List<Product> products = new ArrayList<>();

		String sql = Constants.SQL_PRODUCTS_BY_CATEGORY;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			int categoryId = Integer.parseInt(category);
			stmt.setInt(1, categoryId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = mapResultSetToProduct(rs);
				products.add(product);
			}

		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		return products;
	}

	private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
		Product product = new Product();

		product.setId(rs.getInt(Constants.COL_PRODUCT_ID));
		product.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
		product.setDescription(rs.getString(Constants.COL_DESCRIPTION));
		product.setPrice(rs.getDouble(Constants.COL_PRICE));
		product.setStock(rs.getInt(Constants.COL_STOCK));
		product.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
		product.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));

		return product;
	}
}