

CREATE DATABASE fast_delivery_db;
GO

USE shopping_system_db;
GO

-- Categories table
CREATE TABLE categories (
    category_id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE()
);

INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Mobile phones, laptops, tablets, accessories'),
('Clothing', 'Men, women, and kids clothing'),
('Books', 'Fiction, non-fiction, textbooks'),
('Toys', 'Children toys and games'),
('Utensils', 'Kitchen utensils and tools'),
('Utility', 'Home utility items'),
('Studying', 'Study materials, stationery'),
('Decorating', 'Home decoration items'),
('Gaming', 'Video games, consoles, accessories');
GO

-- Products table
CREATE TABLE products (
    product_id INT PRIMARY KEY IDENTITY(1,1),
    product_name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url NVARCHAR(500),
    category_id INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_name ON products(product_name);

INSERT INTO products (product_name, description, price, stock, image_url, category_id) VALUES
('iPhone 15 Pro', 'Latest Apple smartphone with A17 Pro chip', 999.99, 50, 'images/iphone15.jpg', 1),
('MacBook Pro 16"', 'Apple laptop with M3 Max chip', 2499.99, 25, 'images/macbook.jpg', 1),
('Nike Air Max', 'Comfortable running shoes', 129.99, 100, 'images/nike.jpg', 2),
('Casual T-Shirt', '100% cotton, multiple colors', 24.99, 200, 'images/tshirt.jpg', 2),
('The Great Gatsby', 'Classic novel by F. Scott Fitzgerald', 12.99, 75, 'images/gatsby.jpg', 3),
('Java Programming', 'Complete guide to Java programming', 49.99, 40, 'images/java.jpg', 3),
('LEGO Star Wars', 'Millennium Falcon building set', 159.99, 30, 'images/lego.jpg', 4),
('Kitchen Knife Set', '5-piece stainless steel knife set', 79.99, 60, 'images/knives.jpg', 5);
GO

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    first_name NVARCHAR(50),
    last_name NVARCHAR(50),
    phone NVARCHAR(20),
    address NVARCHAR(500),
    role NVARCHAR(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'ADMIN')),
    created_at DATETIME DEFAULT GETDATE(),
    last_login DATETIME,
    is_active BIT DEFAULT 1,
    CONSTRAINT CHK_username_length CHECK (LEN(username) >= 3),
    CONSTRAINT CHK_email_format CHECK (email LIKE '%_@__%.__%')
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

INSERT INTO users (username, email, password_hash, first_name, last_name, role) 
VALUES ('admin', 'admin@store.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System', 'Administrator', 'ADMIN');

INSERT INTO users (username, email, password_hash, first_name, last_name, phone, address) VALUES
('john_doe', 'john@example.com', '00e765aa5e61172f6659b5b6c2e6a3d8f8e6c7e5d4c3b2a1f0e9d8c7b6a5f4e3', 'John', 'Doe', '123-456-7890', '123 Main St, New York'),
('jane_smith', 'jane@example.com', '00e765aa5e61172f6659b5b6c2e6a3d8f8e6c7e5d4c3b2a1f0e9d8c7b6a5f4e3', 'Jane', 'Smith', '987-654-3210', '456 Oak Ave, Boston');
GO

-- Carts table
CREATE TABLE carts (
    cart_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    session_id NVARCHAR(100),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    cart_item_id INT PRIMARY KEY IDENTITY(1,1),
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    added_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE (cart_id, product_id)
);
GO

-- Orders table
CREATE TABLE orders (
    order_id INT PRIMARY KEY IDENTITY(1,1),
    order_number AS 'ORD' + RIGHT('00000' + CAST(order_id AS VARCHAR(10)), 6) PERSISTED,
    user_id INT NOT NULL,
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10,2) NOT NULL,
    status NVARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    shipping_address NVARCHAR(500) NOT NULL,
    payment_method NVARCHAR(50),
    payment_status NVARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED')),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name NVARCHAR(200) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    total_price AS (quantity * unit_price),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
GO

-- Reviews table
CREATE TABLE reviews (
    review_id INT PRIMARY KEY IDENTITY(1,1),
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(1000),
    created_at DATETIME DEFAULT GETDATE(),
    is_approved BIT DEFAULT 1,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE (product_id, user_id)
);
GO

-- Trigger for stock update
CREATE TRIGGER trg_update_product_stock
ON order_items
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p
    SET p.stock = p.stock - i.quantity,
        p.updated_at = GETDATE()
    FROM products p
    INNER JOIN inserted i ON p.product_id = i.product_id;
END;
GO

PRINT 'Database shopping_system_db created successfully!';
GO