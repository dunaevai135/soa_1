CREATE TABLE products
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT NOT NULL,
    coordinate_id   INTEGER ,
    creationDate    DATE ,
    price           NUMBER ,
    manufactureCost NUMBER ,
    unitOfMeasure   TEXT,
    owner           INTEGER
);
CREATE TABLE coordinates
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    x               REAL NOT NULL,
    y               REAL NOT NULL
);

CREATE TABLE people
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT,
    passportID      TEXT,
    eyeColor        TEXT,
    hairColor       TEXT,
    nationality     TEXT,
    location_id     INTEGER
);
CREATE TABLE locations
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    y               INTEGER NOT NULL,
    x               REAL NOT NULL,
    name            TEXT NOT NULL
);
