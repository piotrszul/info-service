#!/bin/bash
flume-ng agent -Xmx1G -C ../kite-all/target/kite-all-0.1-SNAPSHOT-all.jar:target/info-services-0.0.2-SNAPSHOT.jar:libs/stanford-corenlp-3.7.0-models-english.jar  -f conf/flume-json-twitter.conf -n a1
