CREATE KEYSPACE IF NOT EXISTS my_keyspace
    WITH REPLICATION = {
    'class':'SimpleStrategy',
    'replication_factor':1
    };
USE my_keyspace;
DROP TABLE IF EXISTS conversion;
CREATE TABLE conversion (
     id uuid,
     symbol text,
     rate decimal,
     rate_on_previous_close decimal,
     made_at timestamp,
     PRIMARY KEY (symbol, made_at)
);

-- INSERT INTO conversion(symbol, made_at) VALUES
--     ('USD_RUB', '2014-03-21 02:52:00');
