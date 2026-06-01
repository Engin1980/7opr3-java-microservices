-- Create databases for each microservice
CREATE DATABASE IF NOT EXISTS applogdb;
CREATE DATABASE IF NOT EXISTS appuserdb;
CREATE DATABASE IF NOT EXISTS authdb;

-- Grant privileges
GRANT ALL PRIVILEGES ON applogdb.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON appuserdb.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON authdb.* TO 'root'@'%';

FLUSH PRIVILEGES;

