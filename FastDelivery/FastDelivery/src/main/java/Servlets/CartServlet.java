package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import DataAccessObject.ProductDataAccessObject;
import model.Cart;
import model.Product;
import utils.CSRFUtil;
import Constants.Constants;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
	private ProductDataAccessObject productDAO;

	@Override
	public void init() {
		productDAO = new ProductDataAccessObject();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Cart cart = getOrCreateCart(session);

		// Generate CSRF token for cart page
		String csrfToken = CSRFUtil.generateToken();
		session.setAttribute("csrfToken", csrfToken);
		request.setAttribute("csrfToken", csrfToken);

		// Remove flash messages after displaying
		String flashMessage = (String) session.getAttribute(Constants.SESSION_FLASH_MESSAGE);
		String flashError = (String) session.getAttribute(Constants.SESSION_FLASH_ERROR);

		if (flashMessage != null) {
			request.setAttribute("flashMessage", flashMessage);
			session.removeAttribute(Constants.SESSION_FLASH_MESSAGE);
		}

		if (flashError != null) {
			request.setAttribute("flashError", flashError);
			session.removeAttribute(Constants.SESSION_FLASH_ERROR);
		}

		request.setAttribute(Constants.SESSION_CART, cart);
		request.getRequestDispatcher(Constants.JSP_CART_VIEW).forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Cart cart = getOrCreateCart(session);

		String action = request.getParameter("action");
		String productIdParam = request.getParameter(Constants.PARAM_PRODUCT_ID);
		String quantityParam = request.getParameter(Constants.PARAM_QUANTITY);

		// CSRF Validation for state-changing operations
		if (action != null && !action.isEmpty()) {
			String csrfToken = request.getParameter("csrfToken");
			String sessionToken = (String) session.getAttribute("csrfToken");
			
			if (csrfToken == null || !csrfToken.equals(sessionToken)) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid request. Please try again.");
				response.sendRedirect("cart");
				return;
			}
			
			// Regenerate CSRF token for next request (DO NOT remove it)
			String newToken = CSRFUtil.generateToken();
			session.setAttribute("csrfToken", newToken);
		}

		try {
			if (productIdParam != null && !productIdParam.trim().isEmpty()) {
				int productId = Integer.parseInt(productIdParam.trim());
				
				// Validate productId
				if (productId <= 0) {
					session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid product ID");
					response.sendRedirect("cart");
					return;
				}
				
				Product product = productDAO.getProductById(productId);

				if (product != null) {
					if ("remove".equals(action)) {
						cart.removeItem(productId);
						session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
								product.getProduct_name() + " removed from cart!");

					} else if ("update".equals(action) && quantityParam != null) {
						int newQuantity = Integer.parseInt(quantityParam);
						
						// Validate quantity
						if (newQuantity < 0 || newQuantity > 100) {
							session.setAttribute(Constants.SESSION_FLASH_ERROR, 
								"Quantity must be between 0 and 100");
							response.sendRedirect("cart");
							return;
						}
						
						if (newQuantity > 0) {
							if (newQuantity <= product.getStock()) {
								cart.updateQuantity(productId, newQuantity);
								session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
										"Quantity updated for " + product.getProduct_name());
							} else {
								session.setAttribute(Constants.SESSION_FLASH_ERROR, "Cannot set quantity to "
										+ newQuantity + ". Only " + product.getStock() + " available in stock.");
							}
						} else {
							cart.removeItem(productId);
							session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
									product.getProduct_name() + " removed from cart!");
						}

					} else {
						int quantity = 1;
						if (quantityParam != null && !quantityParam.trim().isEmpty()) {
							quantity = Integer.parseInt(quantityParam.trim());
							
							// Validate quantity
							if (quantity <= 0 || quantity > 100) {
								session.setAttribute(Constants.SESSION_FLASH_ERROR, 
									"Quantity must be between 1 and 100");
								response.sendRedirect(request.getHeader("referer"));
								return;
							}
						}

						if (quantity > 0) {
							if (quantity <= product.getStock()) {
								cart.addItem(product, quantity);
								session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
										product.getProduct_name() + " added to cart!");
							} else {
								session.setAttribute(Constants.SESSION_FLASH_ERROR,
										"Only " + product.getStock() + " items available in stock");
							}
						}
					}
				} else {
					session.setAttribute(Constants.SESSION_FLASH_ERROR, "Product not found");
				}
			} else {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Product ID is required");
			}

		} catch (NumberFormatException e) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid number format");
		} catch (Exception e) {
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Error processing cart operation: " + e.getMessage());
			e.printStackTrace();
		}

		response.sendRedirect("cart");
	}

	private Cart getOrCreateCart(HttpSession session) {
		Cart cart = (Cart) session.getAttribute(Constants.SESSION_CART);
		if (cart == null) {
			cart = new Cart();
			session.setAttribute(Constants.SESSION_CART, cart);
		}
		return cart;
	}
}