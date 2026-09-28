ALTER TABLE shop_order
    ADD COLUMN is_reviewed TINYINT NOT NULL DEFAULT 0 COMMENT '是否已评价：0否、1是' AFTER status;

UPDATE shop_order o
JOIN shop_item_review r ON r.order_id = o.id AND r.del_flag = 0
SET o.is_reviewed = 1
WHERE o.is_reviewed = 0;