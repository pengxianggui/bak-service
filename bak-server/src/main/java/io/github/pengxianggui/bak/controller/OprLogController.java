package io.github.pengxianggui.bak.controller;

import io.github.pengxianggui.bak.domain.OprLog;
import io.github.pengxianggui.bak.service.OprLogService;
import io.github.pengxianggui.crud.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "备份记录")
@RestController
@RequestMapping("oprLog")
public class OprLogController extends BaseController<OprLog> {

    public OprLogController(OprLogService oprLogService) {
        super(oprLogService, OprLog.class);
    }
}
