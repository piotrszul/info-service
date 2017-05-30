ADD JAR /usr/lib/hive-hcatalog/share/hcatalog/hive-hcatalog-core-1.1.0-cdh5.8.0.jar;
INSERT OVERWRITE TABLE observation SELECT * FROM observation_ext;
