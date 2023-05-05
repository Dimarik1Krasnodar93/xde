package com.xde.repository;

import com.xde.model.DocInput;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocInputRepository extends JpaRepository<DocInput, Integer> {
    DocInput findByIdDoc(String idDoc);
}
