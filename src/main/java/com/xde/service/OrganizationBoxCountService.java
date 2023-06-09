package com.xde.service;

import com.xde.model.OrganizationBoxCount;
import com.xde.repository.OrganizationBoxCountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис ящиков организаций со счетчиками
 * */
@Service
@AllArgsConstructor
public class OrganizationBoxCountService {
    OrganizationBoxCountRepository organizationBoxCountRepository;

    public OrganizationBoxCount findById(int id) {
        return organizationBoxCountRepository.findById(id).orElseThrow();
    }

    public void save(OrganizationBoxCount organizationBoxCount) {
        organizationBoxCountRepository.save(organizationBoxCount);
    }
}
