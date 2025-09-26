CREATE OR REPLACE VIEW app_logs_v AS
SELECT
regexp_extract("$path", '.*/([^/]+)$', 1) AS file_name,
  t.*
FROM app_logs t;