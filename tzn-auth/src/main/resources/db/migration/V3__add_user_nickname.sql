ALTER TABLE sys_user ADD COLUMN nickname VARCHAR(32) NULL AFTER username;

UPDATE sys_user SET nickname = username WHERE nickname IS NULL OR nickname = '';

ALTER TABLE sys_user MODIFY COLUMN nickname VARCHAR(32) NOT NULL;
