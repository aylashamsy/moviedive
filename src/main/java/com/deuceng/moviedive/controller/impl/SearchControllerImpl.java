package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.ISearchController;
import com.deuceng.moviedive.dto.SearchResponse;
import com.deuceng.moviedive.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/movie_dive/search")
@RequiredArgsConstructor
public class SearchControllerImpl implements ISearchController {

    private final ISearchService searchService;

    @Override
    @GetMapping(path ="/search-by-name")
    public SearchResponse search(@RequestParam(name = "q") String q) {
        return searchService.search(q);
    }
}
