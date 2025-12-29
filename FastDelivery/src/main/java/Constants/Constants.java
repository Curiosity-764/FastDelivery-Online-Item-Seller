package Constants;

public class Constants {
    // fields
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_PRODUCT_NAME = "product_name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_PRICE = "price";
    public static final String COL_STOCK = "stock";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_CATEGORY_ID = "category_id";
    // sql
    public static final String SQL_SELECT_ALL_PRODUCTS = "SELECT * FROM products";
    public static final String SQL_SELECT_PRODUCT_BY_ID = "SELECT * FROM products WHERE product_id = ?";
    public static final String SQL_SEARCH_PRODUCTS = "SELECT * FROM products WHERE product_name LIKE ? OR description LIKE ?";
    public static final String SQL_PRODUCTS_BY_CATEGORY = "SELECT * FROM products WHERE category_id = ?";
    private static final String INSERT_USER = 
        "INSERT INTO users (username, email, password_hash, first_name, last_name, phone, address, role, created_at, active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = 
        "SELECT * FROM users WHERE user_id = ? AND active = 1";
    private static final String SELECT_USER_BY_USERNAME = 
        "SELECT * FROM users WHERE username = ? AND active = 1";
    private static final String SELECT_USER_BY_EMAIL = 
        "SELECT * FROM users WHERE email = ? AND active = 1";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT * FROM users WHERE active = 1 ORDER BY created_at DESC";
    
    private static final String SELECT_ALL_USERS_ADMIN = 
        "SELECT * FROM users ORDER BY created_at DESC";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
    
    private static final String UPDATE_USER_PASSWORD = 
        "UPDATE users SET password_hash = ? WHERE user_id = ?";
    
    private static final String DELETE_USER = 
        "UPDATE users SET active = 0 WHERE user_id = ?";
    
    private static final String ACTIVATE_USER = 
        "UPDATE users SET active = 1 WHERE user_id = ?";
    
    private static final String CHANGE_USER_ROLE = 
        "UPDATE users SET role = ? WHERE user_id = ?";
    
    private static final String COUNT_USERS = 
        "SELECT COUNT(*) FROM users WHERE active = 1";
    
    
    
    
    
    
    // search
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_PRODUCT_ID = "productId";
    public static final String PARAM_QUANTITY = "quantity";
    public static final String PARAM_VIEW_PRODUCT_ID = "viewProductId";
    
    // request
    public static final String ATTR_PRODUCTS = "products";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_CURRENT_CATEGORY = "currentCategory";
    public static final String ATTR_CURRENT_SEARCH = "currentSearch";
    public static final String ATTR_CURRENT_SORT = "currentSort";
    
    // session
    public static final String SESSION_CART = "cart";
    public static final String SESSION_USER = "user"; 
    public static final String SESSION_FLASH_MESSAGE = "flashMessage";
    public static final String SESSION_FLASH_ERROR = "flashError";
    public static final String SESSION_VIEWED_CATEGORIES = "viewedCategories";
    public static final String SESSION_SEARCH_HISTORY = "searchHistory";
    public static final String SESSION_RECENTLY_VIEWED = "recentlyViewed";
    
    // Duong dan file
    public static final String JSP_PRODUCT_GRID = "jsp/product-grid.jsp";
    public static final String JSP_CART_VIEW = "jsp/cart.jsp";
    public static final String JSP_ERROR = "jsp/error.jsp";
    
    // sort
    public static final String SORT_NAME = "name";
    public static final String SORT_PRICE = "price";
    
    // category
    public static final String CATEGORY_ELECTRONICS = "1";
    public static final String CATEGORY_CLOTHING = "2";
    public static final String CATEGORY_BOOKS = "3";
    public static final String CATEGORY_TOYS = "4";
    public static final String CATEGORY_UTENSILS = "5";
    public static final String CATEGORY_UTILITY= "6";
    public static final String CATEGORY_STUDYING = "7";
    public static final String CATEGORY_DECORATING = "8";
    public static final String CATEGORY_GAMING = "9";
}