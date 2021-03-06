DROP TABLE IF EXISTS  forecast;
CREATE TABLE forecast (
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
 STORED AS PARQUET;
