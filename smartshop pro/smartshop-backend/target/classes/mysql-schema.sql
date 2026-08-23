-- SmartShop Pro Database Schema for MySQL 8.0+

CREATE DATABASE IF NOT EXISTS smartshop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartshop_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL,
    last_login_at DATETIME
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL
);

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(50) UNIQUE,
    barcode VARCHAR(50) UNIQUE,
    category_id BIGINT,
    brand VARCHAR(100),
    model_number VARCHAR(100),
    unit VARCHAR(30) DEFAULT 'pcs',
    cost_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    selling_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    current_stock INT NOT NULL DEFAULT 0,
    min_stock_alert INT NOT NULL DEFAULT 5,
    shelf_location VARCHAR(100),
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_product_barcode (barcode),
    INDEX idx_product_sku (sku),
    INDEX idx_product_name (name)
);

-- 4. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    company_name VARCHAR(150),
    contact_person VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(120),
    address VARCHAR(255),
    tax_number VARCHAR(50),
    opening_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 5. Supplier Purchases Table
CREATE TABLE IF NOT EXISTS supplier_purchases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(60) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    purchase_date DATETIME NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    due_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    payment_status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    notes VARCHAR(500),
    created_by_user_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_purchase_invoice (invoice_number)
);

-- 6. Purchase Items Table
CREATE TABLE IF NOT EXISTS purchase_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(12,2) NOT NULL,
    total_cost DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (purchase_id) REFERENCES supplier_purchases(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 7. Supplier Payments Table
CREATE TABLE IF NOT EXISTS supplier_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    purchase_id BIGINT,
    amount DECIMAL(12,2) NOT NULL,
    payment_date DATETIME NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    reference_number VARCHAR(60),
    notes VARCHAR(500),
    balance_before_payment DECIMAL(12,2) NOT NULL,
    balance_after_payment DECIMAL(12,2) NOT NULL,
    created_by_user_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (purchase_id) REFERENCES supplier_purchases(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 8. Supplier Ledger Entries Table (Running Ledger)
CREATE TABLE IF NOT EXISTS supplier_ledger_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    transaction_date DATETIME NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    reference_number VARCHAR(60),
    description VARCHAR(255),
    debit DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    credit DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    balance_after DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_by_user_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ledger_supplier_date (supplier_id, transaction_date)
);

-- 9. Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(120),
    address VARCHAR(255),
    city VARCHAR(100),
    total_purchases INT NOT NULL DEFAULT 0,
    total_spent DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL,
    INDEX idx_customer_phone (phone),
    INDEX idx_customer_name (name)
);

-- 10. Sales Orders Table
CREATE TABLE IF NOT EXISTS sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(60) NOT NULL UNIQUE,
    customer_id BIGINT,
    customer_name VARCHAR(120) DEFAULT 'Walk-in Customer',
    customer_phone VARCHAR(30),
    cashier_id BIGINT NOT NULL,
    sale_date DATETIME NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax_rate DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    grand_total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    change_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CASH',
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    total_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_profit DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    notes VARCHAR(500),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    FOREIGN KEY (cashier_id) REFERENCES users(id),
    INDEX idx_sale_invoice (invoice_number),
    INDEX idx_sale_date (sale_date)
);

-- 11. Sale Items Table
CREATE TABLE IF NOT EXISTS sale_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name VARCHAR(150) NOT NULL,
    sku VARCHAR(50),
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    cost_price DECIMAL(12,2) NOT NULL,
    discount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(12,2) NOT NULL,
    item_profit DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
);

-- 12. Expense Categories Table
CREATE TABLE IF NOT EXISTS expense_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL
);

-- 13. Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    expense_date DATETIME NOT NULL,
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CASH',
    reference_number VARCHAR(60),
    notes VARCHAR(500),
    created_by_user_id BIGINT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_expense_date (expense_date)
);

-- 14. Shop Settings Table
CREATE TABLE IF NOT EXISTS shop_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_name VARCHAR(150) NOT NULL,
    tagline VARCHAR(200),
    address VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(120),
    website VARCHAR(100),
    tax_number VARCHAR(60),
    currency_symbol VARCHAR(10) DEFAULT '$',
    currency_code VARCHAR(10) DEFAULT 'USD',
    default_tax_rate DECIMAL(6,2) DEFAULT 5.00,
    enable_tax BOOLEAN NOT NULL DEFAULT TRUE,
    default_low_stock_alert INT NOT NULL DEFAULT 5,
    receipt_header VARCHAR(300),
    receipt_footer VARCHAR(300),
    updated_at DATETIME
);

-- 15. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(40) NOT NULL,
    details VARCHAR(1000),
    performed_by_username VARCHAR(60),
    performed_by_full_name VARCHAR(100),
    ip_address VARCHAR(50),
    timestamp DATETIME NOT NULL,
    INDEX idx_audit_time (timestamp),
    INDEX idx_audit_action (action)
);
