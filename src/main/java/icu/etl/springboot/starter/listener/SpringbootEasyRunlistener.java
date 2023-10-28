package icu.etl.springboot.starter.listener;

import java.util.concurrent.atomic.AtomicBoolean;

import icu.etl.springboot.starter.SpringEasyApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * SpringBoot 启动监听器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
public class SpringbootEasyRunlistener implements SpringApplicationRunListener {

    /** 日志接口 */
    private static Logger log = LoggerFactory.getLogger(SpringbootEasyRunlistener.class);

    /** 在启动多个监听器实例的场景下，防止多次启动 easyetl 组件 */
    private final static AtomicBoolean notstart = new AtomicBoolean(true);

    /** 当前SpringBoot应用 */
    private SpringApplication application;

    /** SpringBoot应用的启动参数 */
    private String[] args;

    /**
     * 初始化
     *
     * @param application SpringBoot应用
     * @param args        SpringBoot应用的启动参数
     */
    public SpringbootEasyRunlistener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    public void starting() {
    }

    public void environmentPrepared(ConfigurableEnvironment environment) {
    }

    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    /**
     * 为了兼容SpringBoot不通版本
     *
     * @param context 上下文信息
     */
    public void started(ConfigurableApplicationContext context) {
        if (notstart.getAndSet(false)) {
            SpringEasyApplication.run(context, this.application, this.args, log);
        }
    }

    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        this.started(context);
    }

}
