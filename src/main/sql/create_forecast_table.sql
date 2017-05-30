CREATE EXTERNAL TABLE forecast (
    issue_time_utc TIMESTAMP,
    area STRUCT<
        aac:STRING,
        parent_aac:STRING,
        type: STRING,
        description: STRING>,
    periods ARRAY<STRUCT<
        start_time_utc TIMESTAMP,
        end_time_utc TIMESTAMP,
        elements STRUCT<
            air_temperature_maximum STRING,
            air_temperature_minimum STRING,
            forecast_icon_code STRING,
            precipitation_range STRING,
            precis STRING,
            probability_of_precipitation STRING>>>
 ) 
 ROW FORMAT SERDE 'com.cloudera.hive.serde.JSONSerDe'
 LOCATION '/user/flume/tweets';