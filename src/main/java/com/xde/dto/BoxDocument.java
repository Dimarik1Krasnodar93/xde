package com.xde.dto;


import java.util.Objects;

public record BoxDocument(String boxId, String docId) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoxDocument that = (BoxDocument) o;
        return Objects.equals(boxId, that.boxId) && Objects.equals(docId, that.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxId, docId);
    }
}
