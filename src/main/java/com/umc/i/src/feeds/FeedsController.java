package com.umc.i.src.feeds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;

}
