CREATE TABLE IF NOT EXISTS couple_binding (
    id BIGINT NOT NULL AUTO_INCREMENT,
    requester_user_id BIGINT NOT NULL,
    target_user_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_couple_binding_requester (requester_user_id, status),
    KEY idx_couple_binding_target (target_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情侣绑定邀请';
