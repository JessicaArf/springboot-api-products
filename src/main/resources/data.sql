INSERT INTO tb_roles (role_id, name)
SELECT 1, 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_roles WHERE role_id = 1
);

INSERT INTO tb_roles (role_id, name)
SELECT 2, 'BASIC'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_roles WHERE role_id = 2
);