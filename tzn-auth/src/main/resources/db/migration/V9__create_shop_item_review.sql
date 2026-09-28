CREATE TABLE IF NOT EXISTS shop_item_review (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    item_id BIGINT NOT NULL COMMENT '商品ID',
    reviewer_user_id BIGINT NOT NULL COMMENT '评价用户ID',
    seller_user_id BIGINT NOT NULL COMMENT '卖家用户ID',
    rating TINYINT NOT NULL COMMENT '评分：1至5星',
    content VARCHAR(1000) NULL COMMENT '评价内容',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shop_item_review_order (order_id),
    KEY idx_shop_item_review_item (item_id, del_flag, create_time),
    KEY idx_shop_item_review_seller (seller_user_id, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城商品评价';