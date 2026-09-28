UPDATE shop_category SET category_type = CASE category_type
    WHEN 'FOOD' THEN '1' WHEN 'SERVICE' THEN '2' WHEN 'PRODUCT' THEN '3' ELSE category_type END;
UPDATE shop_category SET status = CASE status WHEN 'ENABLED' THEN '1' WHEN 'DISABLED' THEN '0' ELSE status END;
ALTER TABLE shop_category
    MODIFY COLUMN category_type TINYINT NOT NULL COMMENT '分类类型：1点餐、2专属服务、3商品',
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用、1启用';

UPDATE shop_item SET item_type = CASE item_type
    WHEN 'FOOD' THEN '1' WHEN 'SERVICE' THEN '2' WHEN 'PRODUCT' THEN '3' ELSE item_type END;
UPDATE shop_item SET status = CASE status
    WHEN 'DRAFT' THEN '0' WHEN 'ON_SHELF' THEN '1' WHEN 'OFF_SHELF' THEN '2' ELSE status END;
ALTER TABLE shop_item
    MODIFY COLUMN item_type TINYINT NOT NULL COMMENT '商品类型：1餐品、2服务、3商品',
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿、1上架、2下架';

UPDATE shop_banner SET status = CASE status WHEN 'ENABLED' THEN '1' WHEN 'DISABLED' THEN '0' ELSE status END;
ALTER TABLE shop_banner
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0停用、1启用';

UPDATE shop_order SET status = CASE status
    WHEN 'WAIT_CONFIRM' THEN '1' WHEN 'IN_PROGRESS' THEN '2' WHEN 'COMPLETED' THEN '3' ELSE status END;
ALTER TABLE shop_order
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态：1待确认、2进行中、3已完成';

UPDATE shop_order_item SET item_type = CASE item_type
    WHEN 'FOOD' THEN '1' WHEN 'SERVICE' THEN '2' WHEN 'PRODUCT' THEN '3' ELSE item_type END;
ALTER TABLE shop_order_item
    MODIFY COLUMN item_type TINYINT NOT NULL COMMENT '商品类型快照：1餐品、2服务、3商品';
