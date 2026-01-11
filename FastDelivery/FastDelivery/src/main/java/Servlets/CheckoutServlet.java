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
import utils.CSRFUtil;
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

	        // Generate CSRF token
	        String csrfToken = CSRFUtil.generateToken();
	        session.setAttribute("csrfToken", csrfToken);
	        request.setAttribute("csrfToken", csrfToken);

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

	        // CSRF Validation
	        String csrfToken = request.getParameter("csrfToken");
	        String sessionToken = (String) session.getAttribute("csrfToken");
	        
	        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
	            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid request. Please try again.");
	            response.sendRedirect(request.getContextPath() + "/checkout");
	            return;
	        }
	        
	        session.removeAttribute("csrfToken");

	        Cart cart = (Cart) session.getAttribute(Constants.SESSION_CART);
	        if (cart == null || cart.isEmpty()) {
	            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Your cart is empty");
	            response.sendRedirect(request.getContextPath() + "/cart");
	            return;
	        }

	        String shippingAddress = request.getParameter("shippingAddress");
	        String paymentMethod = request.getParameter("paymentMethod");

	        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
	            session.setAttribute(Constants.SESSION_FLASH_ERROR, "Please enter shipping address");
	            response.sendRedirect(request.getContextPath() + "/checkout");
	            return;
	        }

	        try {
	            OrderDAO orderDAO = new OrderDAO();

	            double subtotal = cart.getTotalPrice();
	            double shipping = (subtotal >= 50) ? 0 : 5.00;
	            double tax = subtotal * 0.1;
	            double totalAmount = subtotal + shipping + tax;

	            Order order = new Order();
	            order.setUserId(user.getUserId());
	            order.setTotalAmount(totalAmount);
	            order.setShippingAddress(shippingAddress.trim());
	            order.setPaymentMethod(paymentMethod != null ? paymentMethod : "COD");
	            order.setStatus("PENDING");
	            order.setPaymentStatus("PENDING");

	            List<OrderItem> orderItems = new ArrayList<>();
	            for (CartItems cartItem : cart.getItems()) {
	                OrderItem orderItem = new OrderItem();
	                orderItem.setProductId(cartItem.getProduct().getId());
	                orderItem.setProductName(cartItem.getProduct().getProduct_name());
	                orderItem.setQuantity(cartItem.getQuantity());
	                orderItem.setUnitPrice(cartItem.getProduct().getPrice());
	                orderItems.add(orderItem);
	            }

	            String orderNumber = orderDAO.createOrder(order, orderItems);

	            if (orderNumber != null && !orderNumber.isEmpty()) {
	                cart.clear();
	                session.setAttribute(Constants.SESSION_CART, cart);

	                session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
	                        "Order #" + orderNumber + " placed successfully! Thank you for your purchase.");

	                response.sendRedirect(request.getContextPath() + "/orders/view?number=" + orderNumber);

	            } else {
	                session.setAttribute(Constants.SESSION_FLASH_ERROR,
	                        "Failed to place order. Please try again or contact support.");
	                response.sendRedirect(request.getContextPath() + "/checkout");
	            }

	        } catch (SQLException e) {
	            // ========== PHẦN QUAN TRỌNG ĐÃ SỬA ==========
	            
	            // Log lỗi để debug
	            System.err.println("❌ Checkout SQL Error: " + e.getMessage());
	            System.err.println("   SQL State: " + e.getSQLState());
	            System.err.println("   Error Code: " + e.getErrorCode());
	            
	            // Message thân thiện cho user, KHÔNG hiển thị chi tiết SQL
	            String errorMessage;
	            
	            // Có thể check một số error code phổ biến
	            if (e.getMessage().contains("check constraint") || 
	                e.getMessage().contains("CHECK") ||
	                e.getSQLState().equals("23000")) { // Integrity constraint violation
	                
	                // Có thể là lỗi out of stock (do constraint)
	                errorMessage = "Sorry, we couldn't process your order. " +
	                              "Some items in your cart may be out of stock. " +
	                              "Please check your cart and try again.";
	            } else {
	                // Lỗi database khác
	                errorMessage = "A system error occurred while processing your order. " +
	                              "Please try again in a few moments.";
	            }
	            
	            session.setAttribute(Constants.SESSION_FLASH_ERROR, errorMessage);
	            response.sendRedirect(request.getContextPath() + "/cart");
	            // ============================================

	        } catch (Exception e) {
	            // Các lỗi khác
	            System.err.println("❌ Unexpected error during checkout: " + e.getMessage());
	            e.printStackTrace();
	            
	            session.setAttribute(Constants.SESSION_FLASH_ERROR, 
	                "An unexpected error occurred. Please try again.");
	            response.sendRedirect(request.getContextPath() + "/checkout");
	        }
	    }
	}