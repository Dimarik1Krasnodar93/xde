CREATE TABLE doc_input (
                           id SERIAL PRIMARY KEY,
                           id_doc VARCHAR,
                           id_pac VARCHAR,
                           id_box_contractor VARCHAR,
                           id_box VARCHAR,
                           status_ed VARCHAR,
                           status_edo VARCHAR,
                           kind_doc VARCHAR,
                           date_doc TIMESTAMP,
                           date_sign TIMESTAMP,
                           date_get TIMESTAMP,
                           date_agreement TIMESTAMP,
                           fns_contractor VARCHAR,
                           fns_organization VARCHAR,
                           contractor_guid VARCHAR,
                           contractor_inn VARCHAR,
                           contractor_kpp VARCHAR,
                           number_doc VARCHAR,
                           sum REAL,
                           type_doc INT,
                           need_alert_get BOOL,
                           need_sign BOOL,
                           flag_error BOOL,
                           organization_guid VARCHAR
);