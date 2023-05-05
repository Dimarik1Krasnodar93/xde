CREATE TABLE contractor_box (
    id SERIAL PRIMARY KEY,
    name TEXT,
    name_contractor TEXT,
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
                                fns_id TEXT,
                                thumbprint TEXT,
                                thumbprint_server TEXT,
                                certificate TEXT,
                                thumbprint_1c TEXT,
                                certificate_password_1c TEXT
);

CREATE TABLE organization_box_count (
    id SERIAL PRIMARY KEY,
    box_id INT REFERENCES organization_box(id),
    count INT
)