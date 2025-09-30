DROP DATABASE IF EXISTS SWP301;
CREATE DATABASE SWP301 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE SWP301;
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            created_by BIGINT DEFAULT NULL,
                            updated_by BIGINT DEFAULT NULL,
                            deleted_by BIGINT DEFAULT NULL,
                            created_at DATETIME(6) DEFAULT NULL,
                            updated_at DATETIME(6) DEFAULT NULL,
                            deleted_at DATETIME(6) DEFAULT NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            image_url VARCHAR(255) DEFAULT NULL
);

-- Bảng tables
CREATE TABLE tables (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        created_by BIGINT DEFAULT NULL,
                        updated_by BIGINT DEFAULT NULL,
                        deleted_by BIGINT DEFAULT NULL,
                        created_at DATETIME(6) DEFAULT NULL,
                        updated_at DATETIME(6) DEFAULT NULL,
                        deleted_at DATETIME(6) DEFAULT NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,
                        table_number VARCHAR(10) NOT NULL UNIQUE,
                        capacity INT NOT NULL,
                        location VARCHAR(255) DEFAULT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'Available'
);

-- Bảng permissions
CREATE TABLE permissions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             created_by BIGINT DEFAULT NULL,
                             updated_by BIGINT DEFAULT NULL,
                             deleted_by BIGINT DEFAULT NULL,
                             created_at DATETIME(6) DEFAULT NULL,
                             updated_at DATETIME(6) DEFAULT NULL,
                             deleted_at DATETIME(6) DEFAULT NULL,
                             is_deleted TINYINT(1) DEFAULT 0,
                             is_activated TINYINT(1) DEFAULT 1,
                             name VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT
);

-- Bảng roles
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       created_by BIGINT DEFAULT NULL,
                       updated_by BIGINT DEFAULT NULL,
                       deleted_by BIGINT DEFAULT NULL,
                       created_at DATETIME(6) DEFAULT NULL,
                       updated_at DATETIME(6) DEFAULT NULL,
                       deleted_at DATETIME(6) DEFAULT NULL,
                       is_deleted TINYINT(1) DEFAULT 0,
                       is_activated TINYINT(1) DEFAULT 1,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT
);

-- Bảng users
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       created_by BIGINT DEFAULT NULL,
                       updated_by BIGINT DEFAULT NULL,
                       deleted_by BIGINT DEFAULT NULL,
                       created_at DATETIME(6) DEFAULT NULL,
                       updated_at DATETIME(6) DEFAULT NULL,
                       deleted_at DATETIME(6) DEFAULT NULL,
                       is_deleted TINYINT(1) DEFAULT 0,
                       is_activated TINYINT(1) DEFAULT 1,
                       full_name VARCHAR(100) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       hashed_password VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(15) UNIQUE
);

-- Bảng promotions
CREATE TABLE promotions (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            created_by BIGINT DEFAULT NULL,
                            updated_by BIGINT DEFAULT NULL,
                            deleted_by BIGINT DEFAULT NULL,
                            created_at DATETIME(6) DEFAULT NULL,
                            updated_at DATETIME(6) DEFAULT NULL,
                            deleted_at DATETIME(6) DEFAULT NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1,
                            name VARCHAR(100) NOT NULL,
                            code VARCHAR(50) UNIQUE,
                            description TEXT,
                            promotion_type VARCHAR(25) NOT NULL,
                            value DECIMAL(10,2) NOT NULL,
                            min_spend DECIMAL(12,2),
                            start_date DATETIME NOT NULL,
                            end_date DATETIME NOT NULL,
                            usage_limit INT
);

-- Bảng menu_items
CREATE TABLE menu_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            created_by BIGINT DEFAULT NULL,
                            updated_by BIGINT DEFAULT NULL,
                            deleted_by BIGINT DEFAULT NULL,
                            created_at DATETIME(6) DEFAULT NULL,
                            updated_at DATETIME(6) DEFAULT NULL,
                            deleted_at DATETIME(6) DEFAULT NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            image_url VARCHAR(255),
                            price DECIMAL(10,2) NOT NULL,
                            status VARCHAR(20) NOT NULL DEFAULT 'Available',
                            category_id BIGINT NOT NULL,
                            CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Bảng bookings
CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          created_by BIGINT DEFAULT NULL,
                          updated_by BIGINT DEFAULT NULL,
                          deleted_by BIGINT DEFAULT NULL,
                          created_at DATETIME(6) DEFAULT NULL,
                          updated_at DATETIME(6) DEFAULT NULL,
                          deleted_at DATETIME(6) DEFAULT NULL,
                          is_deleted TINYINT(1) DEFAULT 0,
                          is_activated TINYINT(1) DEFAULT 1,
                          customer_name VARCHAR(100) NOT NULL,
                          customer_phone VARCHAR(15) NOT NULL,
                          booking_time DATETIME NOT NULL,
                          num_guests INT NOT NULL,
                          notes TEXT,
                          status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                          table_id BIGINT,
                          staff_id BIGINT,
                          CONSTRAINT fk_booking_table FOREIGN KEY (table_id) REFERENCES tables(id),
                          CONSTRAINT fk_booking_staff FOREIGN KEY (staff_id) REFERENCES users(id)
);

-- Bảng orders
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        created_by BIGINT DEFAULT NULL,
                        updated_by BIGINT DEFAULT NULL,
                        deleted_by BIGINT DEFAULT NULL,
                        created_at DATETIME(6) DEFAULT NULL,
                        updated_at DATETIME(6) DEFAULT NULL,
                        deleted_at DATETIME(6) DEFAULT NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,
                        table_id BIGINT NOT NULL,
                        staff_id BIGINT,
                        total_amount DECIMAL(12,2) NOT NULL DEFAULT '0.00',
                        status VARCHAR(20) NOT NULL DEFAULT 'New',
                        notes TEXT,
                        completed_at DATETIME(6),
                        promotion_id BIGINT,
                        CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES tables(id),
                        CONSTRAINT fk_order_staff FOREIGN KEY (staff_id) REFERENCES users(id),
                        CONSTRAINT fk_order_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

-- Bảng order_details
CREATE TABLE order_details (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               created_by BIGINT DEFAULT NULL,
                               updated_by BIGINT DEFAULT NULL,
                               deleted_by BIGINT DEFAULT NULL,
                               created_at DATETIME(6) DEFAULT NULL,
                               updated_at DATETIME(6) DEFAULT NULL,
                               deleted_at DATETIME(6) DEFAULT NULL,
                               is_deleted TINYINT(1) DEFAULT 0,
                               is_activated TINYINT(1) DEFAULT 1,
                               order_id BIGINT NOT NULL,
                               menu_item_id BIGINT NOT NULL,
                               quantity INT NOT NULL DEFAULT '1',
                               price_at_order DECIMAL(10,2) NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                               notes TEXT,
                               CONSTRAINT fk_orderdetail_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                               CONSTRAINT fk_orderdetail_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Bảng transactions
CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              created_by BIGINT DEFAULT NULL,
                              updated_by BIGINT DEFAULT NULL,
                              deleted_by BIGINT DEFAULT NULL,
                              created_at DATETIME(6) DEFAULT NULL,
                              updated_at DATETIME(6) DEFAULT NULL,
                              deleted_at DATETIME(6) DEFAULT NULL,
                              is_deleted TINYINT(1) DEFAULT 0,
                              is_activated TINYINT(1) DEFAULT 1,
                              order_id BIGINT NOT NULL,
                              amount_paid DECIMAL(12,2) NOT NULL,
                              payment_method VARCHAR(20) NOT NULL,
                              transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              transaction_code VARCHAR(255),
                              cashier_id BIGINT,
                              CONSTRAINT fk_transaction_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                              CONSTRAINT fk_transaction_cashier FOREIGN KEY (cashier_id) REFERENCES users(id)
);

-- Bảng reviews
CREATE TABLE reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         created_by BIGINT DEFAULT NULL,
                         updated_by BIGINT DEFAULT NULL,
                         deleted_by BIGINT DEFAULT NULL,
                         created_at DATETIME(6) DEFAULT NULL,
                         updated_at DATETIME(6) DEFAULT NULL,
                         deleted_at DATETIME(6) DEFAULT NULL,
                         is_deleted TINYINT(1) DEFAULT 0,
                         is_activated TINYINT(1) DEFAULT 1,
                         order_id BIGINT NOT NULL UNIQUE,
                         rating_score TINYINT NOT NULL,
                         comment TEXT,
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Bảng support_requests
CREATE TABLE support_requests (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  created_by BIGINT DEFAULT NULL,
                                  updated_by BIGINT DEFAULT NULL,
                                  deleted_by BIGINT DEFAULT NULL,
                                  created_at DATETIME(6) DEFAULT NULL,
                                  updated_at DATETIME(6) DEFAULT NULL,
                                  deleted_at DATETIME(6) DEFAULT NULL,
                                  is_deleted TINYINT(1) DEFAULT 0,
                                  is_activated TINYINT(1) DEFAULT 1,
                                  table_id BIGINT NOT NULL,
                                  request_type VARCHAR(20) NOT NULL,
                                  status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                                  details TEXT,
                                  staff_id BIGINT,
                                  resolved_at DATETIME(6),
                                  CONSTRAINT fk_support_table FOREIGN KEY (table_id) REFERENCES tables(id),
                                  CONSTRAINT fk_support_staff FOREIGN KEY (staff_id) REFERENCES users(id)
);

-- Bảng user_roles
CREATE TABLE user_roles (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            created_by BIGINT DEFAULT NULL,
                            updated_by BIGINT DEFAULT NULL,
                            deleted_by BIGINT DEFAULT NULL,
                            created_at DATETIME(6) DEFAULT NULL,
                            updated_at DATETIME(6) DEFAULT NULL,
                            deleted_at DATETIME(6) DEFAULT NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1,
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            UNIQUE KEY user_roles_unique (user_id, role_id),
                            CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Bảng role_permissions
CREATE TABLE role_permissions (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  created_by BIGINT DEFAULT NULL,
                                  updated_by BIGINT DEFAULT NULL,
                                  deleted_by BIGINT DEFAULT NULL,
                                  created_at DATETIME(6) DEFAULT NULL,
                                  updated_at DATETIME(6) DEFAULT NULL,
                                  deleted_at DATETIME(6) DEFAULT NULL,
                                  is_deleted TINYINT(1) DEFAULT 0,
                                  is_activated TINYINT(1) DEFAULT 1,
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  UNIQUE KEY role_permissions_unique (role_id, permission_id),
                                  CONSTRAINT fk_rolepermission_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_rolepermission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE stores (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL UNIQUE
);

