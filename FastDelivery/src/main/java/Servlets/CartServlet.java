package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import DataAccessObject.ProductDataAccessObject;
import model.Cart;
import model.Product;
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

	// Add this method at the top of doPost():
	private boolean isValidInput(String productIdParam, String quantityParam) {
		try {
			// Check if productId is a number
			int productId = Integer.parseInt(productIdParam);
			if (productId <= 0)
				return false;

			// Check quantity if provided
			if (quantityParam != null) {
				int quantity = Integer.parseInt(quantityParam);
				if (quantity <= 0 || quantity > 100)
					return false; // Reasonable limit
			}
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Cart cart = getOrCreateCart(session);

		String action = request.getParameter("action");
		String productIdParam = request.getParameter(Constants.PARAM_PRODUCT_ID);
		String quantityParam = request.getParameter(Constants.PARAM_QUANTITY);

		try {
			if (productIdParam != null && !productIdParam.trim().isEmpty()) {
				int productId = Integer.parseInt(productIdParam.trim());
				Product product = productDAO.getProductById(productId);

				if (product != null) {
					if ("remove".equals(action)) {
						cart.removeItem(productId);
						session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
								product.getProduct_name() + " removed from cart!");

					} else if ("update".equals(action) && quantityParam != null) {
						int newQuantity = Integer.parseInt(quantityParam);
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
			session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid quantity format");
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