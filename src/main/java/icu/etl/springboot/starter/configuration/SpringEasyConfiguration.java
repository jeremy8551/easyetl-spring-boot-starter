package icu.etl.springboot.starter.configuration;

import icu.etl.cn.NationalHoliday;
import icu.etl.io.Codepage;
import icu.etl.ioc.EasyContext;
import icu.etl.script.UniversalScriptContext;
import icu.etl.script.UniversalScriptEngine;
import icu.etl.script.UniversalScriptEngineFactory;
import icu.etl.springboot.starter.script.EasySpringEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Spring配置类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
@ConditionalOnClass(UniversalScriptEngineFactory.class)
public class SpringEasyConfiguration {

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngine getScriptEngine(EasyContext context, ApplicationContext springContext) {
        UniversalScriptEngineFactory factory = this.getScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        EasySpringEnvironment bindings = new EasySpringEnvironment(springContext);
        engine.setBindings(bindings, UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngineFactory getScriptEngineFactory(EasyContext context) {
        return new UniversalScriptEngineFactory(context);
    }

    @Lazy
    @Bean
    public NationalHoliday getNationalHoliday(EasyContext context) {
        return context.getBean(NationalHoliday.class);
    }

    @Lazy
    @Bean
    public Codepage getCodepage(EasyContext context) {
        return context.getBean(Codepage.class);
    }

}

