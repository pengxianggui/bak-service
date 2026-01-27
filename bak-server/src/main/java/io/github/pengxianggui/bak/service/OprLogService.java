package io.github.pengxianggui.bak.service;

import io.github.pengxianggui.crud.BaseService;
import io.github.pengxianggui.bak.domain.OprLog;

import java.io.File;

public interface OprLogService extends BaseService<OprLog> {

    String getPreviewUrl(String filePath);
}