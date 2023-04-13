package com.xde.repository;

import com.xde.model.OrganizationBoxCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationBoxCountRepository extends CrudRepository<OrganizationBoxCount, Integer> {
}
