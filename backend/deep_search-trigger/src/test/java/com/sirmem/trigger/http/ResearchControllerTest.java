package com.sirmem.trigger.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirmem.domain.deep_search.service.ResearchService;
import com.sirmem.types.dto.response.ResearchResponse;
import com.sirmem.types.enumeration.ResearchStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResearchControllerTest {

    @Test
    void mapsResearchQuestionToPublicResearchResponse() throws Exception {
        ResearchService researchService = mock(ResearchService.class);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ResearchController(researchService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .build();

        when(researchService.research(argThat(question -> "What is Deep Search?".equals(question.question()))))
                .thenReturn(new ResearchResponse(ResearchStatus.COMPLETED, "# Research Report"));

        mockMvc.perform(post("/research")
                        .contentType("application/json")
                        .content("{\"question\":\"What is Deep Search?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.finalReport").value("# Research Report"))
                .andExpect(jsonPath("$.notes").doesNotExist())
                .andExpect(jsonPath("$.rawNotes").doesNotExist())
                .andExpect(jsonPath("$.topics").doesNotExist())
                .andExpect(jsonPath("$.prompt").doesNotExist())
                .andExpect(jsonPath("$.model").doesNotExist())
                .andExpect(jsonPath("$.tools").doesNotExist());
    }
}
