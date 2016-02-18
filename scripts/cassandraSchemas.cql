CREATE KEYSPACE IF NOT EXISTS persona WITH REPLICATION =
{
  'class': 'SimpleStrategy',
  'replication_factor': '1'
}
AND durable_writes = true;

USE persona;

CREATE TABLE IF NOT EXISTS bank(
  user_id int,
  creation_time timestamp,
  category text,
  subcategory text,
  data map<text, text>,
  PRIMARY KEY(user_id, creation_time))
WITH CLUSTERING ORDER BY(creation_time DESC);