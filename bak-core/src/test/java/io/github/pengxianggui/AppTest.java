package io.github.pengxianggui;

import io.github.pengxianggui.bak.mysqldump.DumpConfig;
import io.github.pengxianggui.bak.mysqldump.DumpManager;
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
                .dbUseruame("root")
                .dbPassword("123456")
                .defaultBakDir("D:\\tmp\\bak")
                .defaultArchiveDir("D:\\tmp\\archive")
                .defaultExportDir("D:\\tmp\\export")
                .loadScript();
        DumpManager dumpManager = new DumpManager(dumpConfig);
        File file = dumpManager.bak("data_category", "back-up", "data_category", "", "", true);
    }
}
