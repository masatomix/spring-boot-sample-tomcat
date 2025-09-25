CREATE EXTERNAL TABLE app_logs (
  `date` string,
  `@timestamp` string,
  `@version` string,
  `message` string,
  `logger_name` string,
  `thread_name` string,
  `level` string,
  `level_value` bigint,
  `requestId` string,
  `container_name` string,
  `source` string,
  `container_id` string,
  `ecs_cluster` string,
  `ecs_task_arn` string,
  `ecs_task_definition` string
)
ROW FORMAT SERDE 
  'org.openx.data.jsonserde.JsonSerDe'
WITH SERDEPROPERTIES ( 
  'ignore.malformed.json'='true'
)
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat'
LOCATION 
  's3://masatomix-fluent-bit/fluent-bit-logs/app'
TBLPROPERTIES (
  'has_encrypted_data'='false',
  'transient_lastDdlTime'='1758787330'
);