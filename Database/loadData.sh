#!/bin/bash
bin/cqlsh -f createSchemas.cql
cp ../snapshotFiles/1436222095833/* data/data/keyspace1/user*/
bin/sstableloader -d localhost data/data/keyspace1/users*/
