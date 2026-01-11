package Constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Constants {
	// Các cột trong database - ADDING MISSING ONES
	public static final String COL_PRODUCT_ID = "product_id";
	public static final String COL_PRODUCT_NAME = "product_name";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_PRICE = "price";
	public static final String COL_STOCK = "stock";
	public static final String COL_IMAGE_URL = "image_url";
	public static final String COL_CATEGORY_ID = "category_id";
	public static final String COL_ORDER_ID = "order_id";
	public static final String COL_ORDER_NUMBER = "order_number";
	public static final String COL_USER_ID = "user_id";
	public static final String COL_TOTAL_AMOUNT = "total_amount";
	public static final String COL_SHIPPING_ADDRESS = "shipping_address";
	public static final String COL_PAYMENT_METHOD = "payment_method";
	public static final String COL_STATUS = "status";
	public static final String COL_PAYMENT_STATUS = "payment_status";
	public static final String COL_ORDER_DATE = "order_date";
	
	// Additional column names for users table
	public static final String COL_USERNAME = "username";
	public static final String COL_EMAIL = "email";
	public static final String COL_PASSWORD_HASH = "password_hash";
	public static final String COL_FIRST_NAME = "first_name";
	public static final String COL_LAST_NAME = "last_name";
	public static final String COL_PHONE = "phone";
	public static final String COL_ADDRESS = "address";
	public static final String COL_ROLE = "role";
	public static final String COL_CREATED_AT = "created_at";
	public static final String COL_ACTIVE = "active";

	// lệnh SQL cho sản phẩm
	public static final String SQL_SELECT_ALL_PRODUCTS = "SELECT * FROM products";
	public static final String SQL_SELECT_PRODUCT_BY_ID = "SELECT * FROM products WHERE product_id = ?";
	public static final String SQL_SEARCH_PRODUCTS = "SELECT * FROM products WHERE product_name LIKE ? OR description LIKE ?";
	public static final String SQL_PRODUCTS_BY_CATEGORY = "SELECT * FROM products WHERE category_id = ?";
	public static final String SQL_INSERT_PRODUCT = "INSERT INTO products (product_name, description, price, stock, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?)";
	public static final String SQL_UPDATE_PRODUCT = "UPDATE products SET product_name = ?, description = ?, price = ?, stock = ?, image_url = ?, category_id = ? WHERE product_id = ?";
	public static final String SQL_DELETE_PRODUCT = "DELETE FROM products WHERE product_id = ?";
	public static final String SQL_SOFT_DELETE_PRODUCT = "UPDATE products SET active = 0 WHERE product_id = ?";
	public static final String SQL_UPDATE_PRODUCT_STOCK = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
	
	// lệnh SQL cho user
	public static final String SQL_INSERT_USER = "INSERT INTO users (username, email, password_hash, first_name, last_name, phone, address, role, created_at, active) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ? AND active = 1";
	public static final String SQL_SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ? AND active = 1";
	public static final String SQL_SELECT_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ? AND active = 1";
	public static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users WHERE active = 1 ORDER BY created_at DESC";
	public static final String SQL_SELECT_ALL_USERS_ADMIN = "SELECT * FROM users ORDER BY created_at DESC";
	public static final String SQL_UPDATE_USER = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
	public static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users SET password_hash = ? WHERE user_id = ?";
	public static final String SQL_DELETE_USER = "UPDATE users SET active = 0 WHERE user_id = ?";
	public static final String SQL_ACTIVATE_USER = "UPDATE users SET active = 1 WHERE user_id = ?";
	public static final String SQL_CHANGE_USER_ROLE = "UPDATE users SET role = ? WHERE user_id = ?";
	public static final String SQL_COUNT_USERS = "SELECT COUNT(*) FROM users WHERE active = 1";

	// lệnh SQL cho order
	public static final String SQL_INSERT_ORDER = "INSERT INTO orders (user_id, total_amount, shipping_address, payment_method, status, payment_status) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	public static final String SQL_SELECT_ORDER_NUMBER = "SELECT order_number FROM orders WHERE order_id = ?";
	public static final String SQL_INSERT_ORDER_ITEM = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price) "
			+ "VALUES (?, ?, ?, ?, ?)";
	public static final String SQL_SELECT_ORDER_BY_ID_WITH_USER = "SELECT o.*, u.username, u.email FROM orders o "
			+ "LEFT JOIN users u ON o.user_id = u.user_id " + "WHERE o.order_id = ?";
	public static final String SQL_SELECT_ORDER_BY_NUMBER_WITH_USER = "SELECT o.*, u.username, u.email FROM orders o "
			+ "LEFT JOIN users u ON o.user_id = u.user_id " + "WHERE o.order_number = ?";
	public static final String SQL_SELECT_ORDERS_BY_USER = "SELECT o.*, u.username, u.email FROM orders o "
			+ "LEFT JOIN users u ON o.user_id = u.user_id " + "WHERE o.user_id = ? ORDER BY o.order_date DESC";
	public static final String SQL_SELECT_ALL_ORDERS = "SELECT o.*, u.username, u.email FROM orders o "
			+ "LEFT JOIN users u ON o.user_id = u.user_id " + "ORDER BY o.order_date DESC";
	public static final String SQL_UPDATE_ORDER_STATUS = "UPDATE orders SET status = ? WHERE order_id = ?";
	public static final String SQL_UPDATE_ORDER_PAYMENT_STATUS = "UPDATE orders SET payment_status = ? WHERE order_id = ?";
	public static final String SQL_SELECT_ORDER_ITEMS = "SELECT oi.*, p.image_url FROM order_items oi "
			+ "LEFT JOIN products p ON oi.product_id = p.product_id " + "WHERE oi.order_id = ?";

	// biến request
	public static final String PARAM_SEARCH = "search";
	public static final String PARAM_CATEGORY = "category";
	public static final String PARAM_SORT = "sort";
	public static final String PARAM_PRODUCT_ID = "productId";
	public static final String PARAM_QUANTITY = "quantity";
	public static final String PARAM_VIEW_PRODUCT_ID = "viewProductId";
	public static final String PARAM_ORDER_NUMBER = "orderNumber";
	public static final String PARAM_ORDER_ID = "orderId";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_SHIPPING_ADDRESS = "shippingAddress";
	public static final String PARAM_PAYMENT_METHOD = "paymentMethod";

	// thuộc tính request
	public static final String ATTR_PRODUCTS = "products";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_MESSAGE = "message";
	public static final String ATTR_CURRENT_CATEGORY = "currentCategory";
	public static final String ATTR_CURRENT_SEARCH = "currentSearch";
	public static final String ATTR_CURRENT_SORT = "currentSort";
	public static final String ATTR_ORDER = "order";
	public static final String ATTR_ORDERS = "orders";
	public static final String ATTR_USER = "user";
	public static final String ATTR_CART = "cart";
	public static final String ATTR_ERROR = "error";
	public static final String ATTR_FLASH_MESSAGE = "flashMessage";
	public static final String ATTR_FLASH_ERROR = "flashError";
	public static final String ATTR_ALL_CATEGORIES = "allCategories";
	public static final String ATTR_CSRF_TOKEN = "csrfToken";
	public static final String ATTR_IS_OWNER = "isOwner";
	public static final String ATTR_IS_ADMIN = "isAdmin";

	// thuộc tính session
	public static final String SESSION_CART = "cart";
	public static final String SESSION_USER = "user";
	public static final String SESSION_FLASH_MESSAGE = "flashMessage";
	public static final String SESSION_FLASH_ERROR = "flashError";
	public static final String SESSION_VIEWED_CATEGORIES = "viewedCategories";
	public static final String SESSION_SEARCH_HISTORY = "searchHistory";
	public static final String SESSION_RECENTLY_VIEWED = "recentlyViewed";
	public static final String SESSION_CSRF_TOKEN = "csrfToken";
	public static final String SESSION_REDIRECT_URL = "redirectAfterLogin";

	// Đường dẫn file
	public static final String JSP_PRODUCT_GRID = "jsp/product-grid.jsp";
	public static final String JSP_CART_VIEW = "jsp/cart.jsp";
	public static final String JSP_CHECKOUT = "jsp/checkout.jsp";
	public static final String JSP_ORDER_DETAILS = "jsp/order-details.jsp";
	public static final String JSP_ORDERS_LIST = "jsp/orders.jsp";
	public static final String JSP_ORDER_TRACKING = "jsp/order-tracking.jsp";
	public static final String JSP_TRACK_ORDER = "jsp/track-order.jsp";
	public static final String JSP_LOGIN = "jsp/login.jsp";
	public static final String JSP_REGISTER = "jsp/register.jsp";
	public static final String JSP_PROFILE = "jsp/profile.jsp";
	public static final String JSP_ERROR = "jsp/error.jsp";

	// sắp xếp
	public static final String SORT_NAME = "name";
	public static final String SORT_PRICE = "price";
	public static final String SORT_DATE = "date";

	// Category IDs with Display Names
	public static final String CATEGORY_ELECTRONICS = "1";
	public static final String CATEGORY_CLOTHING = "2";
	public static final String CATEGORY_BOOKS = "3";
	public static final String CATEGORY_TOYS = "4";
	public static final String CATEGORY_UTENSILS = "5";
	public static final String CATEGORY_UTILITY = "6";
	public static final String CATEGORY_STUDYING = "7";
	public static final String CATEGORY_DECORATING = "8";
	public static final String CATEGORY_GAMING = "9";

	// lưu trữ categories 
	public static final Map<String, String> CATEGORY_NAMES = createCategoryMap();

	private static Map<String, String> createCategoryMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(CATEGORY_ELECTRONICS, "Electronics");
		map.put(CATEGORY_CLOTHING, "Clothing");
		map.put(CATEGORY_BOOKS, "Books");
		map.put(CATEGORY_TOYS, "Toys");
		map.put(CATEGORY_UTENSILS, "Utensils");
		map.put(CATEGORY_UTILITY, "Utility");
		map.put(CATEGORY_STUDYING, "Studying");
		map.put(CATEGORY_DECORATING, "Decorating");
		map.put(CATEGORY_GAMING, "Gaming");
		return Collections.unmodifiableMap(map);
	}

	// Trạng thái order
	public static final String ORDER_PENDING = "PENDING";
	public static final String ORDER_CONFIRMED = "CONFIRMED";
	public static final String ORDER_SHIPPED = "SHIPPED";
	public static final String ORDER_DELIVERED = "DELIVERED";
	public static final String ORDER_CANCELLED = "CANCELLED";

	// phương thức thanh toán ( fix cứng ) 
	public static final String PAYMENT_PENDING = "PENDING";
	public static final String PAYMENT_PAID = "PAID";
	public static final String PAYMENT_FAILED = "FAILED";
	public static final String PAYMENT_COD = "COD";
	public static final String PAYMENT_CARD = "CARD";

	// Quyền lợi Admin hay user
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_CUSTOMER = "CUSTOMER";

	// Thao tác cho giỏ hàng
	public static final String ACTION_ADD = "add";
	public static final String ACTION_REMOVE = "remove";
	public static final String ACTION_UPDATE = "update";

	// fix cứng các giá trị hàng hóa
	public static final double SHIPPING_FEE = 5.00;
	public static final double FREE_SHIPPING_THRESHOLD = 50.00;
	public static final double TAX_RATE = 0.10; // 10%
	public static final int MAX_QUANTITY = 100;
	public static final int MIN_QUANTITY = 1;
}