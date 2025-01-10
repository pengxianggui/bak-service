package io.github.pengxianggui.bak.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.pengxianggui.crud.BaseServiceImpl;
import io.github.pengxianggui.bak.domain.DataCategory;
import io.github.pengxianggui.bak.mapper.DataCategoryMapper;
import io.github.pengxianggui.bak.service.DataCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataCategoryServiceImpl extends BaseServiceImpl<DataCategory, DataCategoryMapper> implements DataCategoryService {

    @Resource
    private DataCategoryMapper dataCategoryMapper;

    @Override
    public void init() {
        this.baseMapper = dataCategoryMapper;
        this.clazz = DataCategory.class;
    }


    @Override
    public DataCategory getByCode(String code) {
        QueryWrapper<DataCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return getOne(queryWrapper);
    }
}
