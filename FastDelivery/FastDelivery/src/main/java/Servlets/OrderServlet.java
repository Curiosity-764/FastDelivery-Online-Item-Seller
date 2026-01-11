package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.User;
import model.OrderItem;
import DataAccessObject.OrderDAO;
import utils.CSRFUtil;
import Constants.Constants;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO;
    
    @Override
    public void init() {
        orderDAO = new OrderDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); 
        
        if (session == null || session.getAttribute("user") == null) {
            if (session != null) {
                session.setAttribute("redirectAfterLogin", request.getRequestURI());
            }
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        String csrfToken = CSRFUtil.generateToken();
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        
   
        Object userObj = session.getAttribute("user");
        User user = null;
        
        if (userObj instanceof User) {
            user = (User) userObj;
        } else {
            session.removeAttribute("user"); 
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null) {
            List<Order> orders = orderDAO.getOrdersByUser(user.getUserId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/jsp/orders.jsp").forward(request, response);
            return;
        }
        
        switch (path) {
            case "/view":
                viewOrder(request, response, user);
                break;
            case "/track":
                trackOrder(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        Object userObj = session.getAttribute("user");
        User user = null;
        
        if (userObj instanceof User) {
            user = (User) userObj;
        } else {
            session.removeAttribute("user"); 
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        String path = request.getPathInfo();
        
        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }
        
        switch (path) {
            case "/cancel":
                cancelOrder(request, response, user);
                break;
            case "/track":
                trackOrder(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    private void viewOrder(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
       
        String csrfToken = CSRFUtil.generateToken();
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        
        String orderIdParam = request.getParameter("id");
        String orderNumber = request.getParameter("number");
        
        if (orderIdParam == null && orderNumber == null) {
            // Redirect to appropriate orders page
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            } else {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            return;
        }
        
        Order order = null;
        
        if (orderIdParam != null) {
            try {
                int orderId = Integer.parseInt(orderIdParam);
                order = orderDAO.getOrderById(orderId);
            } catch (NumberFormatException e) {
         
            }
        } else if (orderNumber != null) {
            order = orderDAO.getOrderByNumber(orderNumber);
        }
        
        if (order == null) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Order not found");
   
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            } else {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            return;
        }
        
        boolean isOwner = order.getUserId() == user.getUserId();
        boolean isAdmin = user.isAdmin();
        
        if (!isOwner && !isAdmin) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Access denied");
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }
        
        request.setAttribute("order", order);
        request.setAttribute("isOwner", isOwner);
        request.setAttribute("isAdmin", isAdmin);
        

        if (isAdmin) {
            request.setAttribute("backUrl", request.getContextPath() + "/admin/orders");
            request.getRequestDispatcher("/admin_jsp/order-details-admin.jsp").forward(request, response);
        } else {
            request.setAttribute("backUrl", request.getContextPath() + "/orders");
            request.getRequestDispatcher("/jsp/order-details.jsp").forward(request, response);
        }
    }
    
    private void cancelOrder(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        

        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        
        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }
        
  
        session.removeAttribute("csrfToken");
        
        String orderIdParam = request.getParameter("orderId");
        
        if (orderIdParam == null) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdParam);
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Order not found");
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
            
            boolean isOwner = order.getUserId() == user.getUserId();
            boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN");
            
            if (!isOwner && !isAdmin) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, "Access denied");
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
            
            if (!order.canBeCancelled()) {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                    "Order cannot be cancelled at this stage");
                response.sendRedirect(request.getContextPath() + "/orders/view?id=" + orderId);
                return;
            }
            
            boolean cancelled = orderDAO.cancelOrder(orderId);
            
            if (cancelled) {
                session.setAttribute(Constants.SESSION_FLASH_MESSAGE, 
                    "Order #" + order.getOrderNumber() + " has been cancelled");
            } else {
                session.setAttribute(Constants.SESSION_FLASH_ERROR, 
                    "Failed to cancel order");
            }
            
            response.sendRedirect(request.getContextPath() + "/orders/view?id=" + orderId);
            
        } catch (NumberFormatException e) {
            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid order ID");
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }
    
    private void trackOrder(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String orderNumber = request.getParameter("orderNumber");
        
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            request.getRequestDispatcher("/jsp/track-order.jsp").forward(request, response);
            return;
        }
        
        Order order = orderDAO.getOrderByNumber(orderNumber.trim());
        
        if (order == null) {
            request.setAttribute("error", "Order not found. Please check your order number.");
            request.getRequestDispatcher("/jsp/track-order.jsp").forward(request, response);
            return;
        }
        
        request.setAttribute("order", order);
        request.getRequestDispatcher("/jsp/order-tracking.jsp").forward(request, response);
    }
}