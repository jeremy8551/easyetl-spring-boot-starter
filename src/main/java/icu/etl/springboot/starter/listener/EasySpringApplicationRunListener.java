package icu.etl.springboot.starter.listener;

import icu.etl.springboot.starter.EasySpringApplication;
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
public class EasySpringApplicationRunListener implements SpringApplicationRunListener {

    /** 日志接口 */
    private final static Logger log = LoggerFactory.getLogger(EasySpringApplicationRunListener.class);

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
    public EasySpringApplicationRunListener(SpringApplication application, String[] args) {
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
     * 为了兼容不同版本的SpringBoot
     *
     * @param context 容器上下文信息
     */
    public void started(ConfigurableApplicationContext context) {
        EasySpringApplication.run(context, this.application, this.args, log);
    }

    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        EasySpringApplication.run(context, this.application, this.args, log);
    }

}
