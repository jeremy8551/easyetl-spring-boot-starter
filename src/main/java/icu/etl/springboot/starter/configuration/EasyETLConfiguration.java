package icu.etl.springboot.starter.configuration;

import icu.etl.ioc.BeanFactory;
import icu.etl.ioc.Codepage;
import icu.etl.ioc.NationalHoliday;
import icu.etl.script.UniversalScriptEngine;
import icu.etl.script.UniversalScriptEngineFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * 场景启动器的配置类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
@ConditionalOnClass(UniversalScriptEngineFactory.class)
@EnableConfigurationProperties(EasyETLProperties.class)
public class EasyETLConfiguration {

    @Bean(destroyMethod = "destroyMethod")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngine getScriptEngine() {
        return this.getScriptEngineFactory().getScriptEngine();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngineFactory getScriptEngineFactory() {
        return new UniversalScriptEngineFactory();
    }

    @Bean
    public NationalHoliday getNationalHoliday() {
        return BeanFactory.get(NationalHoliday.class);
    }

    @Bean
    public Codepage getCodepage() {
        return BeanFactory.get(Codepage.class);
    }

}

