ADD JAR /usr/lib/hive-hcatalog/share/hcatalog/hive-hcatalog-core-1.1.0-cdh5.8.0.jar;
DROP TABLE IF EXISTS tweet_ext;
CREATE EXTERNAL TABLE tweet_ext (
   id BIGINT,
   created_at STRING,
   source STRING,
   favorited BOOLEAN,
   retweeted_status STRUCT<
     text:STRING,
     user:STRUCT<screen_name:STRING,name:STRING>,
     retweet_count:INT>,
   entities STRUCT<
     urls:ARRAY<STRUCT<expanded_url:STRING>>,
     user_mentions:ARRAY<STRUCT<screen_name:STRING,name:STRING>>,
     hashtags:ARRAY<STRUCT<text:STRING>>>,
   text STRING,
   user STRUCT<
     screen_name:STRING,
     name:STRING,
     friends_count:INT,
     followers_count:INT,
     statuses_count:INT,
     verified:BOOLEAN,
     utc_offset:INT,
     time_zone:STRING>,
   in_reply_to_screen_name STRING
 ) 
 ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'
 LOCATION '/user/cloudera/tweet_ext';
