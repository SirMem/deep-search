package com.sirmem.domain.deep_search.model.contract;

import com.sirmem.domain.deep_search.model.value.ResearchBrief;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ResearchBriefOutput {
    @NonNull
    ResearchBrief researchBrief;
}
