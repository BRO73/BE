DROP DATABASE IF EXISTS SWP301;
CREATE DATABASE SWP301 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE SWP301;

-- ====================================================================================
-- BẢNG LÕI
-- ====================================================================================

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NULL COMMENT 'Chỉ cho staff',
                       hashed_password VARCHAR(255) NULL COMMENT 'Chỉ cho staff',
                       is_activated TINYINT(1) DEFAULT 1,
                       is_deleted TINYINT(1) DEFAULT 0,
                       created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) NULL,
                       created_by BIGINT NULL,
                       updated_by BIGINT NULL,
                       deleted_by BIGINT NULL
);

CREATE TABLE staff (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NULL UNIQUE,
                       store_id BIGINT NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       position VARCHAR(50),
                       phone_number VARCHAR(15) NULL UNIQUE,
                       email VARCHAR(100) NULL UNIQUE,
                       created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) NULL,
                       created_by BIGINT NULL,
                       updated_by BIGINT NULL,
                       deleted_by BIGINT NULL,
                       is_deleted TINYINT(1) DEFAULT 0,
                       is_activated TINYINT(1) DEFAULT 1,
                       CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE customers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           full_name VARCHAR(100),
                           user_id BIGINT NULL UNIQUE,
                           phone_number VARCHAR(15) NULL UNIQUE,
                           email VARCHAR(100) NULL UNIQUE,
                           created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                           deleted_at DATETIME(6) NULL,
                           created_by BIGINT NULL,
                           updated_by BIGINT NULL,
                           deleted_by BIGINT NULL,
                           is_deleted TINYINT(1) DEFAULT 0,
                           is_activated TINYINT(1) DEFAULT 1,
                           CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT,
                       created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE permissions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT,
                             created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE user_roles (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE role_permissions (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  CONSTRAINT fk_rolepermission_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_rolepermission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ====================================================================================
-- BẢNG NGHIỆP VỤ
-- ====================================================================================

CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            image_url VARCHAR(255),
                            created_by BIGINT,
                            updated_by BIGINT,
                            deleted_by BIGINT,
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1
);

CREATE TABLE menu_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            category_id BIGINT NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            image_url VARCHAR(255),
                            price DECIMAL(10,2) NOT NULL,
                            status VARCHAR(20) NOT NULL DEFAULT 'Available',
                            created_by BIGINT,
                            updated_by BIGINT,
                            deleted_by BIGINT,
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1,
                            CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE location (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL UNIQUE,
                          description VARCHAR(255),
                          created_by BIGINT,
                          updated_by BIGINT,
                          deleted_by BIGINT,
                          created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                          deleted_at DATETIME(6) NULL,
                          is_deleted TINYINT(1) DEFAULT 0,
                          is_activated TINYINT(1) DEFAULT 1
);

CREATE TABLE tables (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        table_number VARCHAR(10) NOT NULL UNIQUE,
                        capacity INT NOT NULL,
                        location_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'Available',
                        created_by BIGINT,
                        updated_by BIGINT,
                        deleted_by BIGINT,
                        created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at DATETIME(6) NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,
                        CONSTRAINT fk_table_location FOREIGN KEY (location_id) REFERENCES location(id)
);

CREATE TABLE promotions (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            code VARCHAR(50) UNIQUE,
                            description TEXT,
                            promotion_type VARCHAR(25) NOT NULL,
                            value DECIMAL(10,2) NOT NULL,
                            min_spend DECIMAL(12,2),
                            start_date DATETIME NOT NULL,
                            end_date DATETIME NOT NULL,
                            usage_limit INT,
                            created_by BIGINT,
                            updated_by BIGINT,
                            deleted_by BIGINT,
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            deleted_at DATETIME(6) NULL,
                            is_deleted TINYINT(1) DEFAULT 0,
                            is_activated TINYINT(1) DEFAULT 1
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        table_id BIGINT NOT NULL,
                        staff_user_id BIGINT NULL COMMENT 'ID của user là nhân viên tạo order',
                        customer_user_id BIGINT NULL COMMENT 'ID của user là khách hàng của order này',
                        promotion_id BIGINT,
                        total_amount DECIMAL(12,2) NOT NULL DEFAULT '0.00',
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        notes TEXT,
                        completed_at DATETIME(6),
                        created_by BIGINT,
                        updated_by BIGINT,
                        deleted_by BIGINT,
                        created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at DATETIME(6) NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,
                        CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES tables(id),
                        CONSTRAINT fk_order_staff_user FOREIGN KEY (staff_user_id) REFERENCES users(id),
                        CONSTRAINT fk_order_customer_user FOREIGN KEY (customer_user_id) REFERENCES users(id),
                        CONSTRAINT fk_order_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

CREATE TABLE order_details (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               order_id BIGINT NOT NULL,
                               menu_item_id BIGINT NOT NULL,
                               quantity INT NOT NULL DEFAULT '1',
                               price_at_order DECIMAL(10,2) NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                               notes TEXT,
                               created_by BIGINT,
                               updated_by BIGINT,
                               deleted_by BIGINT,
                               created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                               deleted_at DATETIME(6) NULL,
                               is_deleted TINYINT(1) DEFAULT 0,
                               is_activated TINYINT(1) DEFAULT 1,
                               CONSTRAINT fk_orderdetail_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                               CONSTRAINT fk_orderdetail_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              order_id BIGINT NOT NULL,
                              cashier_user_id BIGINT COMMENT 'ID của user là thu ngân',
                              amount_paid DECIMAL(12,2) NOT NULL,
                              payment_method VARCHAR(20) NOT NULL,
                              transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              transaction_code VARCHAR(255),
                              created_by BIGINT,
                              updated_by BIGINT,
                              deleted_by BIGINT,
                              created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                              updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                              deleted_at DATETIME(6) NULL,
                              is_deleted TINYINT(1) DEFAULT 0,
                              is_activated TINYINT(1) DEFAULT 1,
                              CONSTRAINT fk_transaction_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                              CONSTRAINT fk_transaction_cashier_user FOREIGN KEY (cashier_user_id) REFERENCES users(id)
);

CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          customer_user_id BIGINT NULL COMMENT 'User khách hàng nếu họ đăng nhập',
                          customer_name VARCHAR(100) NOT NULL,
                          customer_phone VARCHAR(15) NOT NULL,
                          booking_time DATETIME NOT NULL,
                          num_guests INT NOT NULL,
                          notes TEXT,
                          status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                          table_id BIGINT,
                          staff_user_id BIGINT,
                          created_by BIGINT,
                          updated_by BIGINT,
                          deleted_by BIGINT,
                          created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                          deleted_at DATETIME(6) NULL,
                          is_deleted TINYINT(1) DEFAULT 0,
                          is_activated TINYINT(1) DEFAULT 1,
                          CONSTRAINT fk_booking_customer_user FOREIGN KEY (customer_user_id) REFERENCES users(id),
                          CONSTRAINT fk_booking_table FOREIGN KEY (table_id) REFERENCES tables(id),
                          CONSTRAINT fk_booking_staff_user FOREIGN KEY (staff_user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL UNIQUE,
                         customer_user_id BIGINT NOT NULL,
                         rating_score TINYINT NOT NULL,
                         comment TEXT,
                         created_by BIGINT,
                         updated_by BIGINT,
                         deleted_by BIGINT,
                         created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                         deleted_at DATETIME(6) NULL,
                         is_deleted TINYINT(1) DEFAULT 0,
                         is_activated TINYINT(1) DEFAULT 1,
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                         CONSTRAINT fk_review_customer_user FOREIGN KEY (customer_user_id) REFERENCES users(id)
);

CREATE TABLE support_requests (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  table_id BIGINT NOT NULL,
                                  customer_user_id BIGINT NOT NULL,
                                  staff_user_id BIGINT NULL,
                                  request_type VARCHAR(20) NOT NULL,
                                  status VARCHAR(20) NOT NULL DEFAULT 'Pending',
                                  details TEXT,
                                  resolved_at DATETIME(6),
                                  created_by BIGINT,
                                  updated_by BIGINT,
                                  deleted_by BIGINT,
                                  created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                  updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                  deleted_at DATETIME(6) NULL,
                                  is_deleted TINYINT(1) DEFAULT 0,
                                  is_activated TINYINT(1) DEFAULT 1,
                                  CONSTRAINT fk_support_table FOREIGN KEY (table_id) REFERENCES tables(id),
                                  CONSTRAINT fk_support_customer_user FOREIGN KEY (customer_user_id) REFERENCES users(id),
                                  CONSTRAINT fk_support_staff_user FOREIGN KEY (staff_user_id) REFERENCES users(id)
);

-- Thêm data mẫu
INSERT INTO categories (name, description) VALUES
                                               ('Appetizer', 'Starter dishes to begin a meal'),
                                               ('Main Course', 'Main dishes for a full meal'),
                                               ('Dessert', 'Sweet dishes to finish the meal'),
                                               ('Beverage', 'Drinks including soft drinks and juices');

INSERT INTO location (name, description)
VALUES
    ('Đà Nẵng', 'Thành phố đáng sống'),
    ('Hải Phòng', 'Thành phố cảng'),
    ('Cần Thơ', 'Thành phố miền Tây');

INSERT INTO tables (table_number, capacity, location_id, status)
VALUES
    ('T03', 2, '1', 'Available'),
    ('T04', 8, '2', 'Occupied'),
    ('T05', 4, '3', 'Available');


INSERT INTO menu_items (category_id, name, description, price, status) VALUES
-- Appetizers (Khai vị)
(1, 'Gỏi cuốn tôm thịt', 'Gỏi cuốn tươi với tôm, thịt, bún và rau thơm, dùng kèm nước chấm đậu phộng.', 65000.00, 'Available'),
(1, 'Chả giò hải sản', 'Chả giò giòn rụm với nhân hải sản phong phú.', 75000.00, 'Available'),

-- Main Courses (Món chính)
(2, 'Phở bò đặc biệt', 'Tô phở đầy đủ nạm, gân, sách, tái và bò viên.', 95000.00, 'Available'),
(2, 'Cơm tấm sườn bì chả', 'Cơm tấm nóng hổi ăn kèm sườn nướng, bì, chả trứng và nước mắm chua ngọt.', 85000.00, 'Available'),
(2, 'Bún chả Hà Nội', 'Bún, chả nướng than hoa, nem rán và nước mắm chua ngọt đặc trưng.', 80000.00, 'Available'),

-- Desserts (Tráng miệng)
(3, 'Chè khúc bạch', 'Chè khúc bạch hạnh nhân, nhãn và hạt é.', 45000.00, 'Available'),
(3, 'Tàu hũ trân châu đường đen', 'Tàu hũ mềm mịn kết hợp với trân châu đường đen dai ngon.', 40000.00, 'Available'),

-- Beverages (Đồ uống)
(4, 'Nước chanh sả', 'Nước chanh tươi mát lạnh kết hợp với hương thơm của sả.', 40000.00, 'Available'),
(4, 'Cà phê sữa đá', 'Cà phê phin đậm đà pha cùng sữa đặc và đá.', 35000.00, 'Available'),
(4, 'Trà sen vàng', 'Trà sen thơm dịu, giúp thư giãn tinh thần.', 50000.00, 'Unavailable');

INSERT INTO roles (name, description) VALUES
                                          ('ADMIN', 'Quản trị hệ thống, có toàn quyền'),
                                          ('WAITSTAFF', 'Nhân viên phục vụ bàn, nhận order từ khách'),
                                          ('KITCHEN', 'Nhân viên bếp, chế biến món ăn'),
                                          ('CASHIER', 'Thu ngân, xử lý thanh toán'),
                                          ('CUSTOMER', 'Khách hàng');

# SELECT * FROM customers;
# SELECT * FROM stores WHERE name = 'Nhà hàng A - Quận 1';
# SELECT * FROM users
# SELECT * FROM staff
# SELECT * FROM roles
# DELETE FROM users where id = 2
# DELETE FROM customer where id = 4
# SELECT * FROM tables;
# SELECT * FROM orders
# SELECT * FROM transactions
# SELECT * FROM order_details
# SELECT * FROM roles;
# SELECT * FROM user_roles;
# SELECT * FROM menu_items;
# SELECT * FROM categories;
# SELECT * FROM promotions;

# SHOW CREATE TABLE users;
# ALTER TABLE users MODIFY full_name VARCHAR(100) NULL;
# SELECT * FROM roles;

# DROP TABLE IF EXISTS floor_elements;

CREATE TABLE floor_elements (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                x DOUBLE NOT NULL,
                                y DOUBLE NOT NULL,
                                width DOUBLE NOT NULL,
                                height DOUBLE NOT NULL,
                                rotation DOUBLE DEFAULT 0,
                                color VARCHAR(20),
                                type VARCHAR(20),
                                label VARCHAR(100),


    -- Audit fields
                                created_by BIGINT,
                                updated_by BIGINT,
                                deleted_by BIGINT,
                                created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                deleted_at DATETIME(6) NULL,
                                is_deleted TINYINT(1) DEFAULT 0,
                                is_activated TINYINT(1) DEFAULT 1
);

# SELECT * FROM tables;
# ALTER TABLE bookings
#     ADD customer_email VARCHAR(100) NOT NULL DEFAULT '';
#
# DESC customers;

#
# ALTER TABLE customers MODIFY user_id BIGINT NULL;


INSERT INTO promotions
(name, code, description, promotion_type, value, min_spend, start_date, end_date, usage_limit, created_by, updated_by, is_deleted, is_activated)
VALUES
-- ACTIVE: nằm trong khoảng ngày hiện tại
('Weekend Special', 'WEEKEND20', '20% off all main courses during weekends', 'percentage', 20.00, NULL,
 '2025-10-01 00:00:00', '2025-12-31 23:59:59', 100, 1, 1, 0, 1),

-- ACTIVE (tháng 11)
('Happy Hour', 'HAPPY5', '$5 off all beverages from 5-7 PM', 'fixed', 5.00, NULL,
 '2025-11-01 00:00:00', '2025-11-30 23:59:59', NULL, 1, 1, 0, 1),

-- ACTIVE (trải dài cả năm 2025)
('First Timer', 'FIRSTTIME15', '15% discount for new customers', 'percentage', 15.00, NULL,
 '2025-01-01 00:00:00', '2025-12-31 23:59:59', NULL, 1, 1, 0, 1),

-- EXPIRED (đã hết hạn từ 14/02/2025)
('Valentine''s Special', 'VALENTINE', 'Buy one get one free dessert', 'percentage', 50.00, NULL,
 '2025-02-10 00:00:00', '2025-02-14 23:59:59', 50, 1, 1, 0, 1),

-- SCHEDULED (chưa đến ngày bắt đầu)
('Holiday Season', 'HOLIDAY25', '25% off holiday season menu items', 'percentage', 25.00, NULL,
 '2025-12-15 00:00:00', '2026-01-05 23:59:59', 200, 1, 1, 0, 1),

-- INACTIVE (đang trong khoảng ngày nhưng bị tắt kích hoạt)
('Student Discount', 'STUDENT10', '$10 off orders above $50 for students', 'fixed', 10.00, 50.00,
 '2025-01-01 00:00:00', '2025-12-31 23:59:59', NULL, 1, 1, 0, 0);

INSERT INTO orders (
    table_id, staff_user_id, customer_user_id, promotion_id,
    total_amount, status, notes, completed_at,
    created_by, updated_by, is_deleted, is_activated
)
VALUES
-- Đơn hàng mới (chưa hoàn thành)
(1, 2, 1, NULL, 350.00, 'NEW', 'Khách vừa gọi 2 món chính và 1 nước', NULL, 2, 2, 0, 1),

-- Đơn hàng đang xử lý
(2, 7, 10, 4, 420.00, 'PROCESSING', 'Đang chế biến', NULL, 3, 3, 0, 1),

-- Đơn hàng đã hoàn tất
(3, 8, 11, 5, 580.00, 'COMPLETED', 'Khách đã thanh toán bằng tiền mặt', NOW(), 4, 4, 0, 1),

-- Đơn hàng áp dụng khuyến mãi
(5, 9, 12, 6, 320.00, 'COMPLETED', 'Giảm giá 20% cho món chính', NOW(), 3, 3, 0, 1);

INSERT INTO customers (full_name, user_id, phone_number, email, is_deleted, is_activated)
VALUES
    ('Nguyễn Văn A', 9, '0905123456', 'vana@example.com', 0, 1),
    ('Trần Thị B', 10, '0906234567', 'thib@example.com', 0, 1),
    ('Lê Văn C', 11, '0907345678', 'vanc@example.com', 0, 1),
    ('Phạm Thị D', 12, '0908456789', 'thid@example.com', 0, 1);

INSERT INTO staff (
    user_id,
    full_name,
    position,
    phone_number,
    email,
    created_by,
    updated_by,
    is_deleted,
    is_activated
)
VALUES
    (7, 'Nguyễn Văn Minh',  'Waitstaff',  '0905123456', 'minh.waiter@example.com', 1, 1, 0, 1),
    (8, 'Trần Thị Hoa',     'Cashier',    '0906234567', 'hoa.cashier@example.com', 1, 1, 0, 1),
    (9, 'Lê Văn Nam',       'Kitchen',    '0907345678', 'nam.kitchen@example.com', 1, 1, 0, 1),
    (10, 'Phạm Thị Linh',    'Waitstaff',  '0908456789', 'linh.waiter@example.com', 1, 1, 0, 1),
    (11, 'Ngô Đức Phúc',     'Manager',    '0909567890', 'phuc.manager@example.com', 1, 1, 0, 1);


INSERT INTO order_details (order_id, menu_item_id, quantity, price_at_order, status, notes)
VALUES
-- Order #1 (Khách gọi 2 món chính và 1 nước)
(1, 3, 1, 95000.00, 'Completed', 'Phở bò đặc biệt cho khách A'),
(1, 4, 1, 85000.00, 'Completed', 'Cơm tấm sườn bì chả cho khách A'),
(1, 9, 1, 40000.00, 'Completed', 'Nước chanh sả kèm món chính'),

-- Order #2 (Đang xử lý)
(2, 5, 2, 80000.00, 'Processing', 'Bún chả Hà Nội cho bàn 2'),
(2, 10, 1, 35000.00, 'Processing', 'Cà phê sữa đá cho bàn 2'),

-- Order #3 (Hoàn tất)
(3, 2, 1, 75000.00, 'Completed', 'Chả giò hải sản'),
(3, 3, 1, 95000.00, 'Completed', 'Phở bò đặc biệt'),
(3, 6, 1, 45000.00, 'Completed', 'Chè khúc bạch tráng miệng'),

-- Order #4 (Hoàn tất, có khuyến mãi)
(4, 4, 2, 85000.00, 'Completed', 'Cơm tấm sườn bì chả giảm giá 20%'),
(4, 9, 1, 40000.00, 'Completed', 'Nước chanh sả kèm món chính');


INSERT INTO orders (table_id, staff_user_id, customer_user_id, total_amount, status, completed_at, created_at, is_deleted, is_activated)
VALUES
-- Ngày 11/1/2025
(1, 7, 9, 280000.00, 'COMPLETED', '2025-11-01 14:30:00', '2025-11-01 12:00:00', 0, 1),
(2, 8, 10, 420000.00, 'COMPLETED', '2025-11-01 19:45:00', '2025-11-01 18:30:00', 0, 1),
(3, 9, 11, 350000.00, 'COMPLETED', '2025-11-01 20:15:00', '2025-11-01 19:00:00', 0, 1),

-- Ngày 11/2/2025
(1, 7, 9, 520000.00, 'COMPLETED', '2025-11-02 13:20:00', '2025-11-02 12:15:00', 0, 1),
(2, 8, 10, 380000.00, 'COMPLETED', '2025-11-02 18:45:00', '2025-11-02 17:30:00', 0, 1),
(3, 9, 11, 610000.00, 'COMPLETED', '2025-11-02 21:00:00', '2025-11-02 19:45:00', 0, 1),

-- Ngày 11/3/2025
(1, 7, 9, 290000.00, 'COMPLETED', '2025-11-03 14:10:00', '2025-11-03 12:45:00', 0, 1),
(2, 8, 10, 480000.00, 'COMPLETED', '2025-11-03 19:30:00', '2025-11-03 18:15:00', 0, 1),
(3, 9, 11, 390000.00, 'COMPLETED', '2025-11-03 20:45:00', '2025-11-03 19:30:00', 0, 1),

-- Ngày 11/4/2025 (hôm nay - cao điểm)
(1, 7, 9, 680000.00, 'COMPLETED', '2025-11-04 15:30:00', '2025-11-04 13:15:00', 0, 1),
(2, 8, 10, 720000.00, 'COMPLETED', '2025-11-04 19:15:00', '2025-11-04 17:45:00', 0, 1),
(3, 9, 11, 890000.00, 'COMPLETED', '2025-11-04 21:30:00', '2025-11-04 20:00:00', 0, 1),
(5, 10, 12, 450000.00, 'COMPLETED', '2025-11-04 20:00:00', '2025-11-04 18:30:00', 0, 1),

-- Ngày 11/5/2025
(1, 7, 9, 320000.00, 'COMPLETED', '2025-11-05 14:45:00', '2025-11-05 13:00:00', 0, 1),
(2, 8, 10, 550000.00, 'COMPLETED', '2025-11-05 19:00:00', '2025-11-05 17:30:00', 0, 1),

-- Ngày 11/6/2025
(1, 7, 9, 410000.00, 'COMPLETED', '2025-11-06 15:15:00', '2025-11-06 13:45:00', 0, 1),
(2, 8, 10, 670000.00, 'COMPLETED', '2025-11-06 20:30:00', '2025-11-06 19:00:00', 0, 1),
(3, 9, 11, 380000.00, 'COMPLETED', '2025-11-06 21:15:00', '2025-11-06 19:45:00', 0, 1),

-- Ngày 11/7/2025
(1, 7, 9, 290000.00, 'COMPLETED', '2025-11-07 14:20:00', '2025-11-07 12:30:00', 0, 1),
(2, 8, 10, 510000.00, 'COMPLETED', '2025-11-07 18:45:00', '2025-11-07 17:15:00', 0, 1);

INSERT INTO order_details (order_id, menu_item_id, quantity, price_at_order, status)
VALUES
-- Order cho ngày 11/4 (cao điểm)
(5, 3, 2, 95000.00, 'Completed'),  -- Phở bò
(5, 4, 1, 85000.00, 'Completed'),  -- Cơm tấm
(5, 9, 3, 40000.00, 'Completed'),  -- Nước chanh sả
(6, 2, 2, 75000.00, 'Completed'),  -- Chả giò
(6, 5, 3, 80000.00, 'Completed'),  -- Bún chả
(6, 10, 2, 35000.00, 'Completed'), -- Cà phê
(7, 1, 1, 65000.00, 'Completed'),  -- Gỏi cuốn
(7, 3, 4, 95000.00, 'Completed'),  -- Phở bò
(7, 6, 2, 45000.00, 'Completed'),  -- Chè khúc bạch
(8, 4, 3, 85000.00, 'Completed'),  -- Cơm tấm
(8, 9, 2, 40000.00, 'Completed'),  -- Nước chanh sả

-- Thêm order_details cho các order khác...
(9, 3, 1, 95000.00, 'Completed'),
(9, 4, 1, 85000.00, 'Completed'),
(9, 9, 1, 40000.00, 'Completed'),
(10, 2, 2, 75000.00, 'Completed'),
(10, 5, 2, 80000.00, 'Completed'),
(10, 10, 1, 35000.00, 'Completed');

SELECT mi.name,
       SUM(od.quantity) AS total_qty,
       SUM(od.price_at_order * od.quantity) AS revenue
FROM order_details od
         JOIN menu_items mi ON mi.id = od.menu_item_id
         JOIN orders o ON o.id = od.order_id
WHERE DATE(o.created_at) BETWEEN DATE(NOW() - INTERVAL 7 DAY) AND DATE(NOW())
GROUP BY mi.name
ORDER BY revenue DESC
LIMIT 5;


INSERT INTO orders (table_id, staff_user_id, customer_user_id, total_amount, status, created_at, completed_at)
VALUES
    (1, 7, 9, 500000, 'COMPLETED', NOW(), NOW()),
    (2, 8, 10, 350000, 'COMPLETED', NOW(), NOW());

INSERT INTO order_details (order_id, menu_item_id, quantity, price_at_order, status)
VALUES
    (LAST_INSERT_ID(), 3, 2, 95000, 'Completed'),
    (LAST_INSERT_ID(), 4, 1, 85000, 'Completed');