package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.crud.dynamic.Crud;
import io.github.pengxianggui.crud.dynamic.CrudService;
import io.github.pengxianggui.bak.service.OprLogService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "备份记录")
@RestController
@RequestMapping("oprLog")
@Crud
public class OprLogController {

    @Resource
    @CrudService
    private OprLogService oprLogService;
}
