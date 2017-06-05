DROP TABLE IF EXISTS tweet_avro;
CREATE EXTERNAL TABLE tweet_avro(
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
 STORED AS AVRO
 LOCATION '/user/cloudera/tweet_avro';
