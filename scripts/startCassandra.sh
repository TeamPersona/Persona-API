#!/bin/bash

pushd ~/Downloads

if [ ! -d "apache-cassandra-3.3" ]; then
    echo "Downloading Cassandra..."
	curl "http://apache.mirror.iweb.ca/cassandra/3.3/apache-cassandra-3.3-bin.tar.gz" -o "apache-cassandra-3.3-bin.tar.gz"
	tar -zxvf apache-cassandra-3.3-bin.tar.gz
	rm apache-cassandra-3.3-bin.tar.gz
else
	echo "Cassandra already downloaded"
fi

echo "Running cassandra"
apache-cassandra-3.3/bin/cassandra -f

popd
