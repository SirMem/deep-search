package com.sirmem.types.dto.response;

import com.sirmem.types.enumeration.ResearchStatus;


public record ResearchResponse(ResearchStatus status, String finalReport) {
}
