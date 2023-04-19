package com.xde.dto;


import com.xde.model.OrganizationBox;

import java.util.Objects;

public record BoxDocument(OrganizationBox organizationBox, String docId) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoxDocument that = (BoxDocument) o;
        return Objects.equals(organizationBox, that.organizationBox) && Objects.equals(docId, that.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationBox, docId);
    }
}
