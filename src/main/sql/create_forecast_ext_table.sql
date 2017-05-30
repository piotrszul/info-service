ADD JAR file:///home/cloudera/dev/cdh-twitter-example/hive-serdes/target/hive-serdes-1.0-SNAPSHOT.jar;
DROP TABLE IF EXISTS  forecast_ext;
CREATE EXTERNAL TABLE forecast_ext (
    issue_time_utc STRING,
    area STRUCT<
        aac:STRING,
        parent_aac:STRING,
        type:STRING,
        description: STRING>,
    periods ARRAY<STRUCT<
        start_time_utc:STRING,
        end_time_utc:STRING,
        elements:STRUCT<
            air_temperature_maximum:STRING,
            air_temperature_minimum:STRING,
            forecast_icon_code:STRING,
            precipitation_range:STRING,
            precis:STRING,
            probability_of_precipitation:STRING>>>
 ) 
 ROW FORMAT SERDE 'com.cloudera.hive.serde.JSONSerDe'
 LOCATION '/user/cloudera/forecast';
