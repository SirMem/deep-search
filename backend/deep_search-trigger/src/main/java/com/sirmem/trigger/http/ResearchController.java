package com.sirmem.trigger.http;

import com.sirmem.types.dto.request.ResearchRequest;
import com.sirmem.types.dto.response.ResearchResponse;
import com.sirmem.domain.deep_search.service.ResearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResearchController {

    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/research")
    public ResearchResponse research(@RequestBody ResearchRequest request) {
        ResearchResponse response = researchService.research(new ResearchRequest(request.question()));
        return response;
    }
}
