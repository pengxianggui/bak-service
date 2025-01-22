package io.github.pengxianggui;

import io.github.pengxianggui.bak.mysqldump.DumpConfig;
import io.github.pengxianggui.bak.mysqldump.DumpManager;
import io.github.pengxianggui.bak.mysqldump.ExecuteResult;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    public void test() throws IOException {
        DumpConfig dumpConfig = new DumpConfig();
        dumpConfig.dbIp("127.0.0.1")
                .dbPort(3306)
                .dbUsername("root")
                .dbPassword("123456")
                .bakDir("D:\\tmp\\bak")
                .archiveDir("D:\\tmp\\archive")
                .exportDir("D:\\tmp\\export")
                .loadScript();
        DumpManager dumpManager = new DumpManager(dumpConfig);
        ExecuteResult<File> result = dumpManager.bak("data_category", "back-up", "data_category", "", "", true);
        Assert.assertTrue(result.getResult() != null && result.getResult().isFile() && result.getResult().exists());
    }
}
