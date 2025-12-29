package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import Constants.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import DataAccessObject.ProductDataAccessObject;
import model.Product;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {
	private ProductDataAccessObject productDAO;

	@Override
	public void init() {
		productDAO = new ProductDataAccessObject();
		System.out.println("🎯 ProductServlet INIT called");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String category = request.getParameter(Constants.PARAM_CATEGORY);
			String sortBy = request.getParameter(Constants.PARAM_SORT);
			String search = request.getParameter(Constants.PARAM_SEARCH);

			List<Product> products;

			if (search != null && !search.trim().isEmpty()) {
				products = productDAO.searchProducts(search.trim());
				request.setAttribute(Constants.ATTR_TITLE, "Search Results for: " + search);

			} else if (category != null && !category.trim().isEmpty()) {
				products = productDAO.getProductsByCategory(category);
				Map<String, String> categoryNames = new HashMap<>();
				categoryNames.put("1", "Electronics");
				categoryNames.put("2", "Clothing");
				categoryNames.put("3", "Books");
				categoryNames.put("4", "Toys");
				categoryNames.put("5", "Utensils");
				categoryNames.put("6", "Utility");
				categoryNames.put("7", "Studying");
				categoryNames.put("8", "Decorating");
				categoryNames.put("9", "Gaming");

				String categoryName = categoryNames.get(category);
				if (categoryName != null) {
					request.setAttribute("title", categoryName + " Products");
				} else {
					request.setAttribute("title", "Category " + category + " Products");
				}

			} else {
				products = productDAO.getAllItems();
				if (Constants.SORT_PRICE.equals(sortBy)) {
					products.sort(Comparator.comparing(Product::getPrice));
				} else if (Constants.SORT_NAME.equals(sortBy)) {
					products.sort(Comparator.comparing(Product::getProduct_name));
				}
				request.setAttribute(Constants.ATTR_TITLE, "All Products");
			}

			Map<String, String> allCategories = new LinkedHashMap<>();
			allCategories.put("1", "Electronics");
			allCategories.put("2", "Clothing");
			allCategories.put("3", "Books");
			allCategories.put("4", "Toys");
			allCategories.put("5", "Utensils");
			allCategories.put("6", "Utility");
			allCategories.put("7", "Studying");
			allCategories.put("8", "Decorating");
			allCategories.put("9", "Gaming");
			request.setAttribute("allCategories", allCategories);
			if (products != null && !products.isEmpty()) {
				System.out.println("✅ Loaded " + products.size() + " products");
			} else {
				System.out.println("⚠️ No products found for the current filters");
				request.setAttribute(Constants.ATTR_MESSAGE, "No products found matching your criteria.");
			}

			request.setAttribute(Constants.ATTR_PRODUCTS, products);
			request.setAttribute(Constants.ATTR_CURRENT_CATEGORY, category);
			request.setAttribute(Constants.ATTR_CURRENT_SEARCH, search);
			request.setAttribute(Constants.ATTR_CURRENT_SORT, sortBy);

			RequestDispatcher dispatcher = request.getRequestDispatcher(Constants.JSP_PRODUCT_GRID);
			dispatcher.forward(request, response);

		} catch (Exception e) {
			System.out.println("❌ ERROR in ProductServlet:");
			e.printStackTrace();

			response.setContentType("text/html");
			java.io.PrintWriter out = response.getWriter();
			out.println("<h1>Servlet Error</h1>");
			out.println("<pre>");
			e.printStackTrace(out);
			out.println("</pre>");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String productId = request.getParameter(Constants.PARAM_VIEW_PRODUCT_ID);
		if (productId != null) {
			trackProductView(request, productId);
		}
		doGet(request, response);
	}

	private void trackProductView(HttpServletRequest request, String productId) {
		HttpSession session = request.getSession();

		List<String> recentlyViewed = (List<String>) session.getAttribute(Constants.SESSION_RECENTLY_VIEWED);

		if (recentlyViewed == null) {
			recentlyViewed = new ArrayList<>();
		}

		recentlyViewed.remove(productId);
		recentlyViewed.add(0, productId);

		if (recentlyViewed.size() > 5) {
			recentlyViewed = recentlyViewed.subList(0, 5);
		}

		session.setAttribute(Constants.SESSION_RECENTLY_VIEWED, recentlyViewed);
		System.out.println("📱 Tracked product view: " + productId);
	}
}