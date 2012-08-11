-- sqlite
DROP TABLE IF EXISTS gaps;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS activities;

CREATE TABLE gaps (
    id VARCHAR(255) PRIMARY KEY,
    version VARCHAR(10) NOT NULL,
    description VARCHAR(200)
);
CREATE TABLE users (
    user VARCHAR(4) PRIMARY KEY,
    firstname VARCHAR(20),
    lastname VARCHAR(20)
);
CREATE TABLE activities (
    id VARCHAR(255)  PRIMARY KEY,
    day DATE NOT NULL,
    time FLOAT NOT NULL CHECK (time>=0 AND time<=1),
    gap_id VARCHAR(255) NOT NULL REFERENCES gaps(id),
    user VARCHAR(4) NOT NULL REFERENCES users(user)
);

DELETE FROM gaps;
INSERT INTO gaps (id, version, description) VALUES ('000ABCDE', '1.7.1', 'description');
INSERT INTO gaps (id, version, description) VALUES ('000FGHIJ', '1.7.1', 'description');
INSERT INTO gaps (id, version, description) VALUES ('000KLMNO', '1.7.2', 'description');
INSERT INTO gaps (id, version, description) VALUES ('000PQRST', '1.7.2', 'description');
INSERT INTO gaps (id, version, description) VALUES ('000UVWXY', '1.7.3', 'description');

DELETE FROM users;
INSERT INTO users (user) VALUES ('CHTS');
INSERT INTO users (user) VALUES ('BHRY');
INSERT INTO users (user) VALUES ('BRUG');
INSERT INTO users (user) VALUES ('RCTC');

DELETE FROM activities;
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100001, date('now', '-1 day'), 0.1, '000ABCDE', 'CHTS');
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100002, date('now', '-2 day'), 0.2, '000ABCDE', 'BHRY');
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100003, date('now', '-3 day'), 0.3, '000KLMNO', 'CHTS');
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100004, date('now', '-4 day'), 0.4, '000KLMNO', 'BHRY');
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100005, date('now', '-5 day'), 0.5, '000KLMNO', 'BRUG');

INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100006, date('now', '-6 day'), 0.6, '000FGHIJ', 'CHTS');
INSERT INTO activities (id, day, time, gap_id, user)
    VALUES (100007, date('now', '-7 day'), 0.7, '000FGHIJ', 'CHTS');





