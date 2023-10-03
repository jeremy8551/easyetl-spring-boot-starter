package icu.etl.springboot.stater;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 场景启动器的配置类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
public class EasyETLConfiguration {

    @Bean
    public ScriptEngine getScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        return manager.getEngineByName("etl");
    }

}

