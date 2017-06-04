#!/bin/bash
flume-ng agent -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n  -C target/info-services-0.0.1-SNAPSHOT.jar -f conf/flume-twitter-avro-to-hdfs.conf -n a1
