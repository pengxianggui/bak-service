package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.crud.dynamic.Crud;
import io.github.pengxianggui.crud.dynamic.CrudService;
import io.github.pengxianggui.bak.service.DataCategoryService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags="数据品类")
@RestController
@RequestMapping("dataCategory")
@Crud
public class DataCategoryController {

    @Resource
    @CrudService
    private DataCategoryService dataCategoryService;

}
