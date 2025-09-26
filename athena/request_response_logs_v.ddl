CREATE OR REPLACE VIEW request_response_logs_v AS
SELECT
regexp_extract("$path", '.*/([^/]+)$', 1) AS file_name,
  t.*
FROM request_response_logs t;