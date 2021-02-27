CREATE TABLE products
(
    id              serial PRIMARY KEY,
    name            TEXT NOT NULL,
    coordinate_id   INTEGER ,
    creationDate    DATE ,
    price           TEXT ,
    manufactureCost TEXT ,
    unitOfMeasure   TEXT,
    owner           INTEGER
);
CREATE TABLE coordinates
(
    id              serial PRIMARY KEY,
    x               TEXT NOT NULL,
    y               TEXT NOT NULL
);

CREATE TABLE people
(
    id serial primary key,
    owner_name TEXT,
    eyeColor TEXT,
    hairColor TEXT,
    nationality TEXT,
    location_x TEXT,
    location_y TEXT,
    location_name TEXT
);