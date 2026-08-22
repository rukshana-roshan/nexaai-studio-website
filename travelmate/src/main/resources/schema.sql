-- TravelMate Database Schema (SRS Section 6.1 Database Requirements)
-- Table: TOURIST_ATTRACTION

CREATE TABLE IF NOT EXISTS tourist_attraction (
    attraction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    image VARCHAR(1000) NOT NULL,
    distance DOUBLE NOT NULL,
    visiting_duration DOUBLE NOT NULL,
    location VARCHAR(150) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    map_link VARCHAR(1000),
    best_time VARCHAR(100),
    entry_fee VARCHAR(100)
);
