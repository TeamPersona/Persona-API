CREATE KEYSPACE IF NOT EXISTS persona WITH REPLICATION =
{
  'class': 'SimpleStrategy',
  'replication_factor': '1'
}
AND durable_writes = true;

USE persona;

CREATE TABLE IF NOT EXISTS data
(
  user_id INT,
  creation_time TIMESTAMP,
  category TEXT,
  subcategory TEXT,
  data MAP<TEXT, TEXT>,
  PRIMARY KEY(user_id, creation_time)
)
WITH CLUSTERING ORDER BY(creation_time DESC);

CREATE TABLE IF NOT EXISTS counts
(
  user_id INT,
  data_type TEXT,
  count COUNTER,
  PRIMARY KEY(user_id, data_type)
);
