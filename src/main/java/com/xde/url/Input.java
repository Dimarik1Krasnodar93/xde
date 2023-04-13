package com.xde.url;

import com.xde.model.OrganizationBoxCount;
import com.xde.repository.OrganizationBoxCountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/input")
@AllArgsConstructor
public class Input {
    OrganizationBoxCountRepository organizationBoxCountRepository;

    @GetMapping("/getOrgInfo")
    public Iterable<OrganizationBoxCount> getOrgInfo() {
        return organizationBoxCountRepository.findAll();
    }

}
