package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.bak.domain.DataCategory;
import io.github.pengxianggui.bak.service.DataCategoryService;
import io.github.pengxianggui.crud.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "数据类目")
@RestController
@RequestMapping("dataCategory")
public class DataCategoryController extends BaseController<DataCategory> {

    public DataCategoryController(DataCategoryService dataCategoryService) {
        super(dataCategoryService, DataCategory.class);
    }
}
