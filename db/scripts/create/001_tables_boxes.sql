CREATE TABLE contractor_box (
    id SERIAL PRIMARY KEY,
    name TEXT,
    name_org TEXT,
    inn TEXT,
    operator_id TEXT,
    fns_id TEXT
);
CREATE TABLE organization_box (
                                id SERIAL PRIMARY KEY,
                                name TEXT,
                                name_org TEXT,
                                inn TEXT,
                                operator_id TEXT,
                                fns_id TEXT
);

CREATE TABLE organization_box_count (
    id SERIAL PRIMARY KEY,
    box INT REFERENCES organization_box(id),
    count INT
)