#!/bin/bash

if [ ! -d "apache-cassandra-2.1.8" ]; then
	wget http://apache.mirror.iweb.ca/cassandra/2.1.8/apache-cassandra-2.1.8-bin.tar.gz
	tar -zxvf apache-cassandra-2.1.8-bin.tar.gz
	rm apache-cassandra-2.1.8-bin.tar.gz
else
	echo "Cassandra already downloaded"
fi
