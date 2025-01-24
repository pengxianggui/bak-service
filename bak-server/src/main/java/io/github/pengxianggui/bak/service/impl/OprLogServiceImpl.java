package io.github.pengxianggui.bak.service.impl;

import io.github.pengxianggui.crud.BaseServiceImpl;
import io.github.pengxianggui.bak.domain.OprLog;
import io.github.pengxianggui.bak.mapper.OprLogMapper;
import io.github.pengxianggui.bak.service.OprLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLEncoder;

@Service
public class OprLogServiceImpl extends BaseServiceImpl<OprLog, OprLogMapper> implements OprLogService {

    @Resource
    private OprLogMapper oprLogMapper;

    @Override
    public void init() {
        this.baseMapper = oprLogMapper;
        this.clazz = OprLog.class;
    }

    @Override
    public String getPreviewUrl(File file) {
        return "/oprLog/download?path=" + URLEncoder.encode(file.getAbsolutePath());
    }


}
