#!/bin/bash
flume-ng agent -C target/info-services-0.0.1-SNAPSHOT.jar -f conf/flume-bom.conf -n a1
