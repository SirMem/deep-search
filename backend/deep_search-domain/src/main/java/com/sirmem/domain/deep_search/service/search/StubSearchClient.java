package com.sirmem.domain.deep_search.service.search;

import java.util.List;

public class StubSearchClient implements SearchClient {

    @Override
    public List<SearchResult> search(SearchQuery searchQuery) {
        return List.of(new SearchResult(
                "Stub result for " + searchQuery.query(),
                "https://example.test/deep-search",
                "Deterministic provider-neutral search result for: " + searchQuery.query()
        ));
    }
}
