#!/bin/bash
flume-ng agent -C target/info-services-0.0.2-SNAPSHOT.jar -f conf/flume-twitter-to-hdfs.conf -n a1
