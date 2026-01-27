package io.github.pengxianggui.bak.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.pengxianggui.bak.domain.DataCategory;
import io.github.pengxianggui.bak.mapper.DataCategoryMapper;
import io.github.pengxianggui.bak.service.DataCategoryService;
import io.github.pengxianggui.crud.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DataCategoryServiceImpl extends BaseServiceImpl<DataCategoryMapper, DataCategory> implements DataCategoryService {
    
    @Override
    public DataCategory getByCode(String code) {
        QueryWrapper<DataCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return getOne(queryWrapper);
    }
}
