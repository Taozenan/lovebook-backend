ALTER TABLE shop_item
    ADD COLUMN publish_time DATETIME NULL COMMENT '上架时间' AFTER status,
    ADD COLUMN off_shelf_time DATETIME NULL COMMENT '下架时间' AFTER publish_time,
    ADD COLUMN off_shelf_reason VARCHAR(255) NULL COMMENT '下架原因' AFTER off_shelf_time,
    ADD COLUMN service_rule VARCHAR(500) NULL COMMENT '服务规则或预约说明' AFTER service_duration_min,
    ADD COLUMN version INT NOT NULL DEFAULT 0 COMMENT '数据版本号，用于乐观锁' AFTER update_time;
