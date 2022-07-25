PRAGMA foreign_keys = OFF;
drop table if exists LGA;
drop table if exists HomlessGroup;
PRAGMA foreign_keys = ON;

CREATE TABLE LGA (
    lga_code          INTEGER NOT NULL,
    lga_name          TEXT NOT NULL,
    lga_type          CHAR (2),
    area_sqkm         DOUBLE,
    latitude          DOUBLE,
    longitude         DOUBLE,
    PRIMARY KEY (lga_code)
);

CREATE TABLE HomlessGroup (
    lga_code          INTEGER NOT NULL,
    year              INTEGER NOT NULL,
    status            CHAR (10) NOT NULL,
    sex               CHAR (2),
    age_group         CHAR (10),
    count             INTEGER,
    PRIMARY KEY (lga_code, status, year, sex, age_group)
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code)
);
