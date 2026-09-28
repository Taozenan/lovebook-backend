CREATE TABLE IF NOT EXISTS shop_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_type TINYINT NOT NULL COMMENT '分类类型：1点餐、2专属服务、3商品',
    name VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon_url VARCHAR(500) NULL COMMENT '分类图标地址',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用、1启用',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shop_category_type_name (category_type, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城分类';

CREATE TABLE IF NOT EXISTS shop_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_id BIGINT NULL COMMENT '分类ID',
    item_type TINYINT NOT NULL COMMENT '商品类型：1餐品、2服务、3商品',
    title VARCHAR(128) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(255) NULL COMMENT '商品副标题',
    description TEXT NULL COMMENT '商品详细说明',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    cover_url VARCHAR(500) NULL COMMENT '封面图片地址',
    seller_user_id BIGINT NOT NULL COMMENT '上架用户ID',
    service_duration_min INT NULL COMMENT '服务时长，单位分钟',
    is_recommended TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐：0否、1是',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿、1上架、2下架',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_shop_item_type_status (item_type, status, del_flag),
    KEY idx_shop_item_seller (seller_user_id, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城商品';

CREATE TABLE IF NOT EXISTS shop_item_image (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    item_id BIGINT NOT NULL COMMENT '商品ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片地址',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '图片排序号',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_shop_item_image_item (item_id, sort_no, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片';

CREATE TABLE IF NOT EXISTS shop_banner (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(128) NOT NULL COMMENT '轮播标题',
    image_url VARCHAR(500) NOT NULL COMMENT '轮播图片地址',
    link_item_id BIGINT NULL COMMENT '跳转商品ID',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0停用、1启用',
    start_time DATETIME NULL COMMENT '生效开始时间',
    end_time DATETIME NULL COMMENT '生效结束时间',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_shop_banner_status (status, sort_no, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城首页轮播';

CREATE TABLE IF NOT EXISTS shop_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(40) NOT NULL COMMENT '订单编号',
    buyer_user_id BIGINT NOT NULL COMMENT '购买用户ID',
    seller_user_id BIGINT NOT NULL COMMENT '上架用户ID',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态：1待确认、2进行中、3已完成',
    remark VARCHAR(500) NULL COMMENT '订单备注',
    appointment_time DATETIME NULL COMMENT '预约服务时间',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shop_order_no (order_no),
    KEY idx_shop_order_buyer (buyer_user_id, status, del_flag),
    KEY idx_shop_order_seller (seller_user_id, status, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单';

CREATE TABLE IF NOT EXISTS shop_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    item_id BIGINT NOT NULL COMMENT '商品ID',
    item_title VARCHAR(128) NOT NULL COMMENT '下单时商品名称快照',
    item_type TINYINT NOT NULL COMMENT '商品类型快照：1餐品、2服务、3商品',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '商品单价快照',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '明细小计',
    cover_url VARCHAR(500) NULL COMMENT '商品封面快照',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_shop_order_item_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单明细';

INSERT IGNORE INTO shop_category (category_type, name, sort_no) VALUES (1, '点餐', 1);
INSERT IGNORE INTO shop_category (category_type, name, sort_no) VALUES (2, '专属服务', 2);
INSERT IGNORE INTO shop_category (category_type, name, sort_no) VALUES (3, '商品', 3);
