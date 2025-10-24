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
                        locationId BIGINT NOT NULL, -- THAY ĐỔI: Khớp với @JoinColumn(name = "locationId")
                        status VARCHAR(20) NOT NULL DEFAULT 'Available',
                        current_order_session_id BIGINT NULL UNIQUE, -- THAY ĐỔI: Thêm cột mới
                        created_by BIGINT,
                        updated_by BIGINT,
                        deleted_by BIGINT,
                        created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at DATETIME(6) NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,

                        CONSTRAINT fk_table_location FOREIGN KEY (locationId) REFERENCES location(id) -- THAY ĐỔI: Cập nhật tên cột
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

-- THAY ĐỔI: Thêm bảng ORDER_SESSIONS mới
CREATE TABLE order_sessions (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                table_id BIGINT NOT NULL,
                                staff_user_id BIGINT NULL COMMENT 'ID của user là nhân viên tạo session',
                                customer_user_id BIGINT NULL COMMENT 'ID của user là khách hàng của session này',
                                promotion_id BIGINT NULL,
                                total_amount DECIMAL(12,2) NOT NULL DEFAULT '0.00',
                                status VARCHAR(20) NOT NULL DEFAULT 'Active' COMMENT 'e.g., Active, Completed, Cancelled',
                                notes TEXT,
                                session_start_time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                session_end_time DATETIME(6) NULL,
                                created_by BIGINT,
                                updated_by BIGINT,
                                deleted_by BIGINT,
                                created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                deleted_at DATETIME(6) NULL,
                                is_deleted TINYINT(1) DEFAULT 0,
                                is_activated TINYINT(1) DEFAULT 1,
                                CONSTRAINT fk_session_table FOREIGN KEY (table_id) REFERENCES tables(id),
                                CONSTRAINT fk_session_staff_user FOREIGN KEY (staff_user_id) REFERENCES users(id),
                                CONSTRAINT fk_session_customer_user FOREIGN KEY (customer_user_id) REFERENCES users(id),
                                CONSTRAINT fk_session_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

-- THAY ĐỔI: Thêm khóa ngoại từ `tables` -> `order_sessions`
ALTER TABLE tables
    ADD CONSTRAINT fk_table_current_session
        FOREIGN KEY (current_order_session_id) REFERENCES order_sessions(id) ON DELETE SET NULL;


-- THAY ĐỔI: Cấu trúc bảng `orders`
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_session_id BIGINT NOT NULL, -- THAY ĐỔI: Thêm cột này
                        status VARCHAR(20) NOT NULL DEFAULT 'New', -- Giữ lại từ entity
                        notes TEXT, -- Giữ lại từ entity

    -- CÁC CỘT ĐÃ BỊ XÓA (đã chuyển lên order_sessions)
    -- table_id, staff_user_id, customer_user_id, promotion_id, total_amount, completed_at

                        created_by BIGINT,
                        updated_by BIGINT,
                        deleted_by BIGINT,
                        created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at DATETIME(6) NULL,
                        is_deleted TINYINT(1) DEFAULT 0,
                        is_activated TINYINT(1) DEFAULT 1,

    -- THAY ĐỔI: Cập nhật/thêm khóa ngoại
                        CONSTRAINT fk_order_session FOREIGN KEY (order_session_id) REFERENCES order_sessions(id) ON DELETE CASCADE

    -- CÁC KHÓA NGOẠI ĐÃ BỊ XÓA
    -- CONSTRAINT fk_order_table, fk_order_staff_user, fk_order_customer_user, fk_order_promotion
);

-- KHÔNG THAY ĐỔI: Bảng order_details đã khớp với entity
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
                              order_id BIGINT NOT NULL, -- Chú ý: Có thể bạn sẽ muốn đổi cái này thành order_session_id
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
                              CONSTRAINT fk_transaction_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, -- Cân nhắc đổi
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
                         order_id BIGINT NOT NULL UNIQUE, -- Chú ý: Có thể bạn sẽ muốn đổi cái này thành order_session_id
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
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, -- Cân nhắc đổi
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

-- THAY ĐỔI: Cập nhật tên cột `location_id` -> `locationId`
INSERT INTO tables (table_number, capacity, locationId, status)
VALUES
    ('T03', 2, 1, 'Available'),
    ('T04', 8, 2, 'Occupied'),
    ('T05', 4, 3, 'Available');


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
                                          ('KITCHEN_STAFF', 'Nhân viên bếp, chế biến món ăn'),
                                          ('CASHIER', 'Thu ngân, xử lý thanh toán'),
                                          ('CUSTOMER', 'Khách hàng');