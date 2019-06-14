INSERT IGNORE INTO roles(id, name)
VALUES (1, 'ROLE_USER');
INSERT IGNORE INTO roles(id, name)
VALUES (2, 'ROLE_ADMIN');
INSERT IGNORE INTO roles(id, name)
VALUES (3, 'ROLE_PM');
INSERT IGNORE INTO roles(id, name)
VALUES (4, 'ROLE_RM');
INSERT IGNORE INTO roles(id, name)
VALUES (5, 'ROLE_PM_LEADER');

INSERT IGNORE INTO users(id, name, email, password, username, code) value (1, 'admin', 'admin@gmail.com', '$2a$10$wNmWUTU4hvpMCyU5CqNrn.Mc40b4GwY46FsuK6rRm7bUKnCQy/Ooe', 'admin', 'ADMIN');
INSERT IGNORE INTO user_roles(user_id, role_id) VALUE (1, 2);
INSERT IGNORE INTO users(id, name, email, password, username, code) value (3, 'maianh', 'tranmaianh@saltlux.com', '$2a$10$wNmWUTU4hvpMCyU5CqNrn.Mc40b4GwY46FsuK6rRm7bUKnCQy/Ooe', 'maianh', 'MAI');
INSERT IGNORE INTO user_roles(user_id, role_id) VALUE (3, 1);
INSERT IGNORE INTO user_roles(user_id, role_id) VALUE (3, 3);
INSERT IGNORE INTO user_roles(user_id, role_id) VALUE (3, 4);

ALTER TABLE purchase_order MODIFY invoice_id BIGINT(20) NULL ;
ALTER TABLE project_assignment MODIFY candidate_id BIGINT(20) NULL;
ALTER TABLE invoices MODIFY candidate_id BIGINT(20) NULL;
# PROJECT ASSIGNMENT
ALTER TABLE project_assignment MODIFY rep100 DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY rep84_75 DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY rep94_85 DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY rep99_95 DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY repno_match DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY reprep DECIMAL(9,2) NOT NULL;
ALTER TABLE project_assignment MODIFY total_rep DECIMAL(10,2) NOT NULL;

ALTER TABLE project_assignment MODIFY w100 DECIMAL(5,2) NOT NULL;
ALTER TABLE project_assignment MODIFY w84_75 DECIMAL(5,2) NOT NULL;
ALTER TABLE project_assignment MODIFY w94_85 DECIMAL(5,2) NOT NULL;
ALTER TABLE project_assignment MODIFY w99_95 DECIMAL(5,2) NOT NULL;
ALTER TABLE project_assignment MODIFY wno_match DECIMAL(5,2) NOT NULL;
ALTER TABLE project_assignment MODIFY wrep DECIMAL(5,2) NOT NULL;

ALTER TABLE project_assignment MODIFY total DECIMAL(22,2) NOT NULL;
ALTER TABLE project_assignment MODIFY unit_price DECIMAL(9,4) NOT NULL;
ALTER TABLE project_assignment MODIFY net_or_hour DECIMAL(22,2) NOT NULL;