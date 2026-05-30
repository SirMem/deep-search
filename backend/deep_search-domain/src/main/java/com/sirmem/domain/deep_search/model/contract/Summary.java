package com.sirmem.domain.deep_search.model.contract;

import com.sirmem.domain.deep_search.model.value.ResearchNote;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Summary {
    @NonNull
    ResearchNote compressedResearch;
}
