package com.sirmem.domain.deep_search.model.contract;

import com.sirmem.domain.deep_search.model.value.ResearchTopic;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ConductResearch {
    @NonNull
    ResearchTopic researchTopic;
}
