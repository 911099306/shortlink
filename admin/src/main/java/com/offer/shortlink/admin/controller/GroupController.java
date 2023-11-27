package com.offer.shortlink.admin.controller;

import com.offer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
}
