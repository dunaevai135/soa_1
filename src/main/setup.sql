CREATE TABLE product
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR2 (100 BYTE) NOT NULL,
    coordinates     INTEGER NOT NULL,
    creationDate    DATE NOT NULL,
    price           NUMBER NOT NULL,
    manufactureCost NUMBER NOT NULL,
    unitOfMeasure   NUMBER,
    owner           NUMBER NOT NULL
)
CREATE TABLE coordinates
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    x               INTEGER NOT NULL,
    y               REAL NOT NULL,
)
