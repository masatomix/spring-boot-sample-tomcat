CREATE OR REPLACE VIEW fw_logs_v AS
SELECT
regexp_extract("$path", '.*/([^/]+)$', 1) AS file_name,
  t.*
FROM fw_logs t;