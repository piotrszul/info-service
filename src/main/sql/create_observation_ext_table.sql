ADD JAR file:///home/cloudera/dev/cdh-twitter-example/hive-serdes/target/hive-serdes-1.0-SNAPSHOT.jar;
DROP TABLE IF EXISTS  observation_ext;
CREATE EXTERNAL TABLE observatoin_ext (
    issue_time_utc STRING,
    issue_time_local STRING,
    station STRUCT<
        bom_id:STRING,
        description:STRING,
        forecast_district_id:STRING,
        lat:DOUBLE,
        lon:DOUBLE,
        stn_name:STRING,
        tz:STRING,
        wmo_id:STRING>,
    period STRUCT<
        time_utc:STRING,
        time_local:STRING>,
    elements:STRUCT<
        air_temperature:STRING,
        apparent_temp:STRING,
        cloud:STRING,
        cloud_base_m:STRING,
        cloud_oktas:STRING,
        cloud_type_id:STRING,
        delta_t:STRING,
        dew_point:STRING,
        gust_kmh:STRING,
        maximum_air_temperature:STRING,
        maximum_gust_dir:STRING,
        maximum_gust_kmh:STRING,
        maximum_gust_spd:STRING,
        minimum_air_temperature:STRING,
        msl_pres:STRING,
        pres:STRING,
        qnh_pres:STRING,
        rainfall:STRING,
        rainfall_24hr:STRING,
        rel_humidity:STRING,
        sea_height:STRING,
        swell_dir:STRING,
        swell_height:STRING,
        swell_period:STRING,
        vis_km:STRING,
        weather:STRING,
        wind_dir:STRING,
        wind_dir_deg:STRING,
        wind_gust_spd:STRING,
        wind_spd:STRING,
        wind_spd_kmh:STRING>
 ) 
 ROW FORMAT SERDE 'com.cloudera.hive.serde.JSONSerDe'
 LOCATION '/user/cloudera/observation_ext';
