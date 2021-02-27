CREATE TABLE products
(
    id              serial PRIMARY KEY,
    name            TEXT NOT NULL,
    coordinate_id   INTEGER ,
    creationDate    DATE ,
    price           numeric ,
    manufactureCost numeric ,
    unitOfMeasure   TEXT,
    owner           INTEGER
);
CREATE TABLE coordinates
(
    id              serial PRIMARY KEY,
    x               REAL NOT NULL,
    y               REAL NOT NULL
);

CREATE TABLE people
(
    id serial primary key,
    owner_name TEXT,
    eyeColor TEXT,
    hairColor TEXT,
    nationality TEXT,
    location_x INTEGER,
    location_y NUMERIC,
    location_name TEXT
);