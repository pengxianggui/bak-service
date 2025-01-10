package io.github.pengxianggui.bak;

import io.github.pengxianggui.bak.mysqldump.DumpConfig;
import io.github.pengxianggui.bak.mysqldump.DumpManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BakAutoConfiguration {

    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "bak")
    @Bean
    public DumpConfig dumpConfig() {
        return new DumpConfig();
    }

    @ConditionalOnMissingBean
    @Bean
    public DumpManager dumpManager() {
        return new DumpManager(dumpConfig());
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskManager taskManager() {
        return new TaskManager();
    }
}
