package DataAccessObject;

import java.sql.*;
import java.util.*;
import model.Product;
import model.DBConnection;
import Constants.Constants;

public class ProductDataAccessObject {

    public boolean addProduct(Product product) {
   
        if (product == null) {
            return false;
        }
        
    
        if (product.getProduct_name() == null || product.getProduct_name().trim().isEmpty()) {
            return false;
        }
        
        String productName = product.getProduct_name().trim();
        if (productName.length() < 2 || productName.length() > 255) {
            return false;
        }
        
    
        if (product.getDescription() != null && product.getDescription().length() > 2000) {
            return false;
        }
        
    
        if (product.getPrice() <= 0 || product.getPrice() > 1000000) {
            return false;
        }

        if (product.getStock() < 0 || product.getStock() > 10000) {
            return false;
        }
        
  
        String imageUrl = product.getImage_url();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            String trimmedUrl = imageUrl.trim();
            if (!isValidImageUrl(trimmedUrl)) {
                return false;
            }
            product.setImage_url(trimmedUrl);
        } else {
            product.setImage_url("https://via.placeholder.com/300x200?text=No+Image");
        }

        if (!isValidCategory(product.getCategory_id())) {
            return false;
        }
        
   
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_INSERT_PRODUCT)) {
            
            ps.setString(1, productName);
            ps.setString(2, product.getDescription() != null ? product.getDescription().trim() : null);
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getImage_url());
            ps.setInt(6, product.getCategory_id());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean updateProduct(Product product) {

        if (product == null) {
            return false;
        }
        
   
        if (product.getId() <= 0) {
            return false;
        }
        
  
        if (product.getProduct_name() == null || product.getProduct_name().trim().isEmpty()) {
            return false;
        }
        
        String productName = product.getProduct_name().trim();
        if (productName.length() < 2 || productName.length() > 255) {
            return false;
        }

        if (product.getDescription() != null && product.getDescription().length() > 2000) {
            return false;
        }

        if (product.getPrice() <= 0 || product.getPrice() > 1000000) {
            return false;
        }
        

        if (product.getStock() < 0 || product.getStock() > 10000) {
            return false;
        }
        

        String imageUrl = product.getImage_url();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            String trimmedUrl = imageUrl.trim();
            if (!isValidImageUrl(trimmedUrl)) {
                return false;
            }
            product.setImage_url(trimmedUrl);
        } else {
            product.setImage_url("https://via.placeholder.com/300x200?text=No+Image");
        }
        

        if (!isValidCategory(product.getCategory_id())) {
            return false;
        }

        if (!productExists(product.getId())) {
            return false;
        }
  
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_UPDATE_PRODUCT)) {
            
            ps.setString(1, productName);
            ps.setString(2, product.getDescription() != null ? product.getDescription().trim() : null);
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getImage_url());
            ps.setInt(6, product.getCategory_id());
            ps.setInt(7, product.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    private boolean isValidCategory(int categoryId) {

        if (!Constants.CATEGORY_NAMES.containsKey(String.valueOf(categoryId))) {
            return false;
        }
        

        String sql = "SELECT COUNT(*) FROM categories WHERE category_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            // If database check fails, at least the constant check passed
            return true;
        }
        
        return false;
    }
    
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true;
        }
        
        String urlLower = url.toLowerCase().trim();
        

        try {
            new java.net.URL(urlLower).toURI();
        } catch (Exception e) {
            return false;
        }
        
        // Check image extensions or placeholder URLs
        return urlLower.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|svg)$") || 
               urlLower.startsWith("https://via.placeholder.com/") ||
               urlLower.startsWith("http://via.placeholder.com/");
    }
    
    private boolean productExists(int productId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_SELECT_PRODUCT_BY_ID)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // If there's a result, product exists
            
        } catch (SQLException e) {
            return false;
        }
    }


    public List<Product> getAllItems() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(Constants.SQL_SELECT_ALL_PRODUCTS);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
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
                // Log error if needed
            }

        } catch (SQLException e) {
            // Log error if needed
        }

        return products;
    }

    public Product getProductById(int id) {
        Product p = null;

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_SELECT_PRODUCT_BY_ID)) {

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
            // Log error if needed
        }

        return p;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(Constants.SQL_SEARCH_PRODUCTS)) {
            
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                
                product.setId(rs.getInt(Constants.COL_PRODUCT_ID));
                product.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
                product.setDescription(rs.getString(Constants.COL_DESCRIPTION));
                product.setPrice(rs.getDouble(Constants.COL_PRICE));
                product.setStock(rs.getInt(Constants.COL_STOCK));
                product.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
                product.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));
                
                products.add(product);
            }
            
        } catch (SQLException e) {
            // Log error if needed
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(Constants.SQL_PRODUCTS_BY_CATEGORY)) {

            int categoryId = Integer.parseInt(category);
            stmt.setInt(1, categoryId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();

                product.setId(rs.getInt(Constants.COL_PRODUCT_ID));
                product.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
                product.setDescription(rs.getString(Constants.COL_DESCRIPTION));
                product.setPrice(rs.getDouble(Constants.COL_PRICE));
                product.setStock(rs.getInt(Constants.COL_STOCK));
                product.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
                product.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));
                
                products.add(product);
            }

        } catch (SQLException | NumberFormatException e) {
            // Log error if needed
        }
        return products;
    }

    public boolean deleteProduct(int productId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_DELETE_PRODUCT)) {
            
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean softDeleteProduct(int productId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SQL_SOFT_DELETE_PRODUCT)) {
            
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        }
    }


    public int getTotalProductsCount(String category, String search) {
        int count = 0;
        String sql;
        ResultSet rs = null;
        
        try (Connection conn = DBConnection.getConnection()) {
            if (search != null && !search.trim().isEmpty()) {
                sql = "SELECT COUNT(*) FROM products WHERE (product_name LIKE ? OR description LIKE ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    String searchTerm = "%" + search + "%";
                    ps.setString(1, searchTerm);
                    ps.setString(2, searchTerm);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            } else if (category != null && !category.trim().isEmpty()) {
                sql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int categoryId = Integer.parseInt(category);
                    ps.setInt(1, categoryId);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            } else {
                sql = "SELECT COUNT(*) FROM products";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error getting total products count: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) {}
            }
        }
        
        return count;
    }

    public List<Product> getAllProductsWithPagination(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllProductsWithPagination: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getProductsByCategoryWithPagination(String category, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ? ORDER BY product_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int categoryId = Integer.parseInt(category);
            ps.setInt(1, categoryId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt(Constants.COL_PRODUCT_ID));
                    product.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
                    product.setDescription(rs.getString(Constants.COL_DESCRIPTION));
                    product.setPrice(rs.getDouble(Constants.COL_PRICE));
                    product.setStock(rs.getInt(Constants.COL_STOCK));
                    product.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
                    product.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));
                    products.add(product);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error in getProductsByCategoryWithPagination: " + e.getMessage());
        }
        return products;
    }

    public List<Product> searchProductsWithPagination(String keyword, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE (product_name LIKE ? OR description LIKE ?) ORDER BY product_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt(Constants.COL_PRODUCT_ID));
                    product.setProduct_name(rs.getString(Constants.COL_PRODUCT_NAME));
                    product.setDescription(rs.getString(Constants.COL_DESCRIPTION));
                    product.setPrice(rs.getDouble(Constants.COL_PRICE));
                    product.setStock(rs.getInt(Constants.COL_STOCK));
                    product.setImage_url(rs.getString(Constants.COL_IMAGE_URL));
                    product.setCategory_id(rs.getInt(Constants.COL_CATEGORY_ID));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in searchProductsWithPagination: " + e.getMessage());
        }
        return products;
    }
}