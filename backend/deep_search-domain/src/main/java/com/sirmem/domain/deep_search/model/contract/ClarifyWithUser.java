package com.sirmem.domain.deep_search.model.contract;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class ClarifyWithUser {
    boolean needClarification;
    Optional<String> question;
    Optional<String> verification;

    public ClarifyWithUser(boolean needClarification, Optional<String> question, Optional<String> verification) {
        this.needClarification = needClarification;
        this.question = normalize(question);
        this.verification = normalize(verification);

        if (needClarification && this.question.isEmpty()) {
            throw new IllegalArgumentException("Clarification question is required when clarification is needed.");
        }
        if (!needClarification && this.verification.isEmpty()) {
            throw new IllegalArgumentException("Clarification verification is required when clarification is not needed.");
        }
    }

    public static ClarifyWithUser needsClarification(String question) {
        return new ClarifyWithUser(true, Optional.ofNullable(question), Optional.empty());
    }

    public static ClarifyWithUser verified(String verification) {
        return new ClarifyWithUser(false, Optional.empty(), Optional.ofNullable(verification));
    }

    private static Optional<String> normalize(Optional<String> value) {
        return value == null ? Optional.empty() : value.map(String::strip).filter(text -> !text.isBlank());
    }
}
