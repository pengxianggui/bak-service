package io.github.pengxianggui.bak.service;

import io.github.pengxianggui.crud.BaseService;
import io.github.pengxianggui.bak.domain.DataCategory;

public interface DataCategoryService extends BaseService<DataCategory> {

    DataCategory getByCode(String code);
}