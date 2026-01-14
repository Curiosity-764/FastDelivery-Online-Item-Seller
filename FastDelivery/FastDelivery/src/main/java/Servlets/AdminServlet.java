package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import DataAccessObject.*;
import model.*;
import Constants.Constants;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;
    private ProductDataAccessObject productDAO;  
    private OrderDAO orderDAO;
    private Map<String, String> categoryMap;

    @Override
    public void init() {
        userDAO = new UserDAO();
        productDAO = new ProductDataAccessObject();  
        orderDAO = new OrderDAO();
        
        // Create category map
        categoryMap = new HashMap<>();
        categoryMap.put("1", "Electronics");
        categoryMap.put("2", "Clothing");
        categoryMap.put("3", "Books");
        categoryMap.put("4", "Toys");
        categoryMap.put("5", "Utensils");
        categoryMap.put("6", "Utility");
        categoryMap.put("7", "Studying");
        categoryMap.put("8", "Decorating");
        categoryMap.put("9", "Gaming");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        
        // Check admin access
        if (currentUser == null || !currentUser.isAdmin()) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                "Access denied. Administrator privileges required.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/") || path.equals("")) {
            showDashboard(request, response);
        } else if (path.equals("/users")) {
            showUsers(request, response);
        } else if (path.equals("/products")) {
            showProducts(request, response);
        } else if (path.equals("/orders")) {
            showOrders(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String path = request.getPathInfo();
        String action = request.getParameter("action");
        
        if (path.equals("/products")) {
            handleProductAction(request, response, action);
        } else if (path.equals("/users")) {
            handleUserAction(request, response, action);
        } else if (path.equals("/orders")) {
            handleOrderAction(request, response, action);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int totalUsers = userDAO.getTotalUsers();
        List<Product> products = productDAO.getAllItems();
        List<Order> allOrders = orderDAO.getAllOrders();
        List<Order> recentOrders = allOrders;
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalProducts", products.size());
        request.setAttribute("totalOrders", allOrders.size());
        request.setAttribute("recentOrders", recentOrders);
        request.setAttribute("activeMenu", "dashboard");
        request.setAttribute("allCategories", categoryMap);
        
        request.getRequestDispatcher("/admin_jsp/dashboard.jsp").forward(request, response);
    }
    
    private void showUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
      
        if ("add".equals(action)) {
            request.setAttribute("activeMenu", "users");
            request.setAttribute("allCategories", categoryMap);
            
            request.getRequestDispatcher("/admin_jsp/user-form.jsp").forward(request, response);
            return;
        }
        
        // Otherwise show user list
        boolean showInactive = "true".equals(request.getParameter("showInactive"));
        List<User> users = userDAO.getAllUsers(showInactive);
        
        request.setAttribute("users", users);
        request.setAttribute("showInactive", showInactive);
        request.setAttribute("activeMenu", "users");
        request.setAttribute("allCategories", categoryMap);
        
        request.getRequestDispatcher("/admin_jsp/users.jsp").forward(request, response);
    }
    private void showProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String productId = request.getParameter("id");
        
    
        if ("add".equals(action) || ("edit".equals(action) && productId != null)) {
            Product product = null;
            if ("edit".equals(action)) {
                product = productDAO.getProductById(Integer.parseInt(productId));
            }
            
            request.setAttribute("product", product);
            request.setAttribute("activeMenu", "products");
            request.setAttribute("allCategories", categoryMap);
            
            request.getRequestDispatcher("/admin_jsp/product-form.jsp").forward(request, response);
            return;
        }
        
   
        List<Product> products = productDAO.getAllItems();
        
        request.setAttribute("products", products);
        request.setAttribute("activeMenu", "products");
        request.setAttribute("allCategories", categoryMap);
        
        request.getRequestDispatcher("/admin_jsp/products.jsp").forward(request, response);
    }
    private void showOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Order> orders = orderDAO.getAllOrders();
        
        request.setAttribute("orders", orders);
        request.setAttribute("activeMenu", "orders");
        request.setAttribute("allCategories", categoryMap);
        
        request.getRequestDispatcher("/admin_jsp/orders.jsp").forward(request, response);
    }
    
    private void handleProductAction(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        
        if ("add".equals(action) || "edit".equals(action)) {
          
            int productId = 0;
            if ("edit".equals(action)) {
                try {
                    productId = Integer.parseInt(request.getParameter("productId"));
                } catch (NumberFormatException e) {
                    session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid product ID");
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                    return;
                }
            }
            
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String stockStr = request.getParameter("stock");
            String imageUrl = request.getParameter("imageUrl");
            String categoryIdStr = request.getParameter("categoryId");
            
            // Basic parameter validation
            if (productName == null || productName.trim().isEmpty() ||
                priceStr == null || priceStr.trim().isEmpty() ||
                stockStr == null || stockStr.trim().isEmpty() ||
                categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                
                session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                    "Required fields: Product Name, Price, Stock, and Category");
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;
            }
            
            double price;
            int stock;
            int categoryId;
            
            try {
                price = Double.parseDouble(priceStr);
                stock = Integer.parseInt(stockStr);
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                    "Invalid number format for Price, Stock, or Category");
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;
            }
            
            Product product = new Product();
            product.setId(productId);
            product.setProduct_name(productName.trim());
            product.setDescription(description != null ? description.trim() : null);
            product.setPrice(price);
            product.setStock(stock);
            product.setImage_url(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : null);
            product.setCategory_id(categoryId);
            
            boolean success;
            if ("add".equals(action)) {
                success = productDAO.addProduct(product);  
                if (success) {
                    session.setAttribute(Constants.SESSION_FLASH_MESSAGE, 
                        "Product '" + productName + "' added successfully.");
                } else {
                    session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                        "Failed to add product. Please check all field values.");
                }
            } else {
                success = productDAO.updateProduct(product);  
                if (success) {
                    session.setAttribute(Constants.SESSION_FLASH_MESSAGE, 
                        "Product '" + productName + "' updated successfully.");
                } else {
                    session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                        "Failed to update product. Product may not exist or data is invalid.");
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/products");
            
        } else if ("delete".equals(action)) {
            int productId;
            try {
                productId = Integer.parseInt(request.getParameter("productId"));
            } catch (NumberFormatException e) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid product ID");
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;
            }
            
            boolean success = productDAO.deleteProduct(productId);
            
            if (success) {
                session.setAttribute(Constants.SESSION_FLASH_MESSAGE, "Product deleted successfully.");
            } else {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Failed to delete product.");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }
    private void handleUserAction(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        
        HttpSession session = request.getSession();
        int userId = Integer.parseInt(request.getParameter("userId"));
        User user = userDAO.findById(userId);
        
        if (user == null) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "User not found.");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        boolean success = false;
        String message = "";
        
        switch (action) {
            case "makeAdmin":
                success = userDAO.changeUserRole(userId, Constants.ROLE_ADMIN);
                message = "User '" + user.getUsername() + "' is now an Administrator.";
                break;
            case "makeCustomer":
                success = userDAO.changeUserRole(userId, Constants.ROLE_CUSTOMER);
                message = "User '" + user.getUsername() + "' is now a Customer.";
                break;
            case "activate":
                success = userDAO.activateUser(userId);
                message = "User '" + user.getUsername() + "' has been activated.";
                break;
            case "deactivate":
                success = userDAO.deleteUser(userId); 
                message = "User '" + user.getUsername() + "' has been deactivated.";
                break;
        }
        
        if (success) {
            session.setAttribute(Constants.SESSION_FLASH_MESSAGE, message);
        } else {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Failed to update user.");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void handleOrderAction(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        int orderId;
        String orderIdParam = request.getParameter("orderId");
        
        if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }
        
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid order ID format");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }
        
        boolean success = false;
        String message = "";
        
        if ("updateStatus".equals(action)) {
            String newStatus = request.getParameter("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Status is required");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            success = orderDAO.updateOrderStatus(orderId, newStatus);
            message = "Order status updated to " + newStatus + ".";
        } else if ("updatePaymentStatus".equals(action)) {
            String paymentStatus = request.getParameter("paymentStatus");
            if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Payment status is required");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            success = orderDAO.updatePaymentStatus(orderId, paymentStatus);
            message = "Payment status updated to " + paymentStatus + ".";
        } else if ("delete".equals(action)) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM orders WHERE order_id = ?")) {
                
                ps.setInt(1, orderId);
                int rowsAffected = ps.executeUpdate();
                success = rowsAffected > 0;
                
                if (success) {
                    // Also delete order items
                    try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM order_items WHERE order_id = ?")) {
                        ps2.setInt(1, orderId);
                        ps2.executeUpdate();
                    }
                    message = "Order deleted successfully.";
                } else {
                    message = "Failed to delete order. Order may not exist.";
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                message = "Database error: " + e.getMessage();
            }
        }
        
   
        if (success) {
            session.setAttribute(Constants.SESSION_FLASH_MESSAGE, message);
        } else {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Failed to update order. " + message);
        }
        
    
        String redirectTo = request.getParameter("redirectTo");
        if ("orderView".equals(redirectTo)) {
                   response.sendRedirect(request.getContextPath() + "/orders/view?id=" + orderId);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }
}
