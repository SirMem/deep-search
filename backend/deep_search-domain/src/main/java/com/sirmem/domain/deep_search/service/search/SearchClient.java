package com.sirmem.domain.deep_search.service.search;

import java.util.List;

public interface SearchClient {

    List<SearchResult> search(SearchQuery searchQuery);
}
