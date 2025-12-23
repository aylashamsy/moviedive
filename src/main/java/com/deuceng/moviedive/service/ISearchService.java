package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.EventResponse;
import com.deuceng.moviedive.dto.SearchResponse;

import java.util.List;

public interface ISearchService {
    SearchResponse search(String q);

}
