CREATE EXTERNAL TABLE `request_response_logs`(
  `date` string COMMENT 'from deserializer', 
  `@timestamp` string COMMENT 'from deserializer', 
  `@version` string COMMENT 'from deserializer', 
  `message` string COMMENT 'from deserializer', 
  `logger_name` string COMMENT 'from deserializer', 
  `thread_name` string COMMENT 'from deserializer', 
  `level` string COMMENT 'from deserializer', 
  `level_value` bigint COMMENT 'from deserializer', 
  `requestid` string COMMENT 'from deserializer', 
  `request` struct<uri:string,parameters:map<string,array<string>>,headers:map<string,string>,body:string,method:string> COMMENT 'from deserializer', 
  `response` struct<headers:map<string,array<string>>,status:string,body:string,duration:string> COMMENT 'from deserializer', 
  `apptype` string COMMENT 'from deserializer', 
  `logtype` string COMMENT 'from deserializer', 
  `container_id` string COMMENT 'from deserializer', 
  `container_name` string COMMENT 'from deserializer', 
  `source` string COMMENT 'from deserializer', 
  `ecs_cluster` string COMMENT 'from deserializer', 
  `ecs_task_arn` string COMMENT 'from deserializer', 
  `ecs_task_definition` string COMMENT 'from deserializer')
ROW FORMAT SERDE 
  'org.openx.data.jsonserde.JsonSerDe' 
WITH SERDEPROPERTIES ( 
  'ignore.malformed.json'='true') 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat'
LOCATION
  's3://masatomix-fluent-bit-req-res/fluent-bit-logs'
TBLPROPERTIES (
  'has_encrypted_data'='false', 
  'transient_lastDdlTime'='1758780974')