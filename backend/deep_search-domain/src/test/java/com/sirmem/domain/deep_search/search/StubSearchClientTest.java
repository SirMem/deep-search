package com.sirmem.domain.deep_search.search;

import com.sirmem.domain.deep_search.service.search.SearchClient;
import com.sirmem.domain.deep_search.service.search.SearchQuery;
import com.sirmem.domain.deep_search.service.search.SearchResult;
import com.sirmem.domain.deep_search.service.search.StubSearchClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StubSearchClientTest {

    @Test
    void returnsDeterministicProviderNeutralSearchResults() {
        SearchClient searchClient = new StubSearchClient();

        List<SearchResult> firstResponse = searchClient.search(new SearchQuery("Deep Search architecture"));
        List<SearchResult> secondResponse = searchClient.search(new SearchQuery("Deep Search architecture"));

        assertThat(firstResponse).isEqualTo(secondResponse);
        assertThat(firstResponse).containsExactly(
                new SearchResult(
                        "Stub result for Deep Search architecture",
                        "https://example.test/deep-search",
                        "Deterministic provider-neutral search result for: Deep Search architecture"
                )
        );
    }
}
