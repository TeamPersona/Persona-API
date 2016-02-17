CREATE KEYSPACE IF NOT EXISTS keyspace1 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE IF NOT EXISTS keyspace1.users (
firstname varchar, 
lastname varchar, 
age int, 
PRIMARY KEY (firstname, lastname));
