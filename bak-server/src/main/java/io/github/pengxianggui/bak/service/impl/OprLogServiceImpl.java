package io.github.pengxianggui.bak.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.pengxianggui.bak.domain.OprLog;
import io.github.pengxianggui.bak.mapper.OprLogMapper;
import io.github.pengxianggui.bak.service.OprLogService;
import io.github.pengxianggui.crud.BaseServiceImpl;
import io.github.pengxianggui.crud.query.PagerQuery;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Service
public class OprLogServiceImpl extends BaseServiceImpl<OprLogMapper, OprLog> implements OprLogService {

    @Override
    public String getPreviewUrl(String filePath) {
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }
        return "/oprLog/download?path=" + URLEncoder.encode(filePath);
    }

    @Override
    public IPage<OprLog> queryPage(PagerQuery query) {
        IPage<OprLog> page = super.queryPage(query);
        for (OprLog record : page.getRecords()) {
            record.setFilePath(getPreviewUrl(record.getFilePath()));
        }
        return page;
    }
}
