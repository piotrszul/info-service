#!/bin/bash
flume-ng agent -C target/info-services-0.0.2-SNAPSHOT-all.jar -f conf/flume-bom.conf -n a1
