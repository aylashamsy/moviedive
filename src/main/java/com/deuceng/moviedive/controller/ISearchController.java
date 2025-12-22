package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.SearchResponse;
import org.springframework.web.bind.annotation.RequestParam;

public interface ISearchController {
    SearchResponse search(@RequestParam(name = "q") String q);
}