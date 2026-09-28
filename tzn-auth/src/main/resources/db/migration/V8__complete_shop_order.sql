ALTER TABLE shop_order
    MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态：1待接单、2已接待、3已完成',
    ADD COLUMN payment_status TINYINT NOT NULL DEFAULT 1 COMMENT '支付状态：1已支付、2支付失败',
    ADD COLUMN payment_method TINYINT NOT NULL DEFAULT 1 COMMENT '支付方式：1余额',
    ADD COLUMN paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实际支付金额',
    ADD COLUMN paid_time DATETIME NULL COMMENT '支付时间',
    ADD COLUMN seller_confirm_time DATETIME NULL COMMENT '卖家接待时间',
    ADD COLUMN buyer_confirm_time DATETIME NULL COMMENT '买家确认完成时间',
    ADD COLUMN completed_time DATETIME NULL COMMENT '订单完成时间',
    ADD COLUMN version INT NOT NULL DEFAULT 0 COMMENT '版本号';

ALTER TABLE shop_order_item
    ADD COLUMN service_duration_min INT NULL COMMENT '服务时长，单位分钟',
    ADD COLUMN service_rule VARCHAR(500) NULL COMMENT '服务规则快照';

CREATE TABLE IF NOT EXISTS shop_user_account (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    frozen_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0正常、1删除',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id), UNIQUE KEY uk_shop_user_account_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城用户账户';

CREATE TABLE IF NOT EXISTS shop_account_flow (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT NULL COMMENT '订单ID',
    order_no VARCHAR(40) NULL COMMENT '订单编号',
    flow_type TINYINT NOT NULL COMMENT '流水类型：1消费、2收入、3充值、4退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '变动金额',
    balance_before DECIMAL(10,2) NOT NULL COMMENT '变动前余额',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    remark VARCHAR(255) NULL COMMENT '流水说明',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id), UNIQUE KEY uk_shop_account_flow_order_type (order_id, flow_type), KEY idx_shop_account_flow_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城账户流水';

CREATE TABLE IF NOT EXISTS shop_order_track (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    status TINYINT NOT NULL COMMENT '订单状态：1待接单、2已接待、3已完成',
    title VARCHAR(64) NOT NULL COMMENT '节点标题',
    description VARCHAR(255) NULL COMMENT '节点描述',
    operate_user_id BIGINT NULL COMMENT '操作人ID',
    create_user BIGINT NULL COMMENT '创建人ID',
    update_user BIGINT NULL COMMENT '修改人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id), KEY idx_shop_order_track_order (order_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单跟踪';