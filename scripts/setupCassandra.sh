#!/bin/bash

currentDir=$(pwd)
echo "The current working directory $currentDir."

pushd ~/Downloads/apache-cassandra-3.3

echo "Creating schemas"
bin/cqlsh -f $currentDir/createSchemas.cql

popd
