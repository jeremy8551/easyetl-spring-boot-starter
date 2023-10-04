package icu.etl.springboot.starter.listener;

import java.util.concurrent.atomic.AtomicBoolean;

import icu.etl.util.ClassUtils;
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
public class EasyETLRunListener implements SpringApplicationRunListener {

    /** 日志接口 */
    private static Logger log = LoggerFactory.getLogger(EasyETLRunListener.class);

    /** 在启动多个监听器实例的场景下，防止多次执行初始化操作 */
    private final static AtomicBoolean notinit = new AtomicBoolean(true);

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
    public EasyETLRunListener(SpringApplication application, String[] args) {
        if (notinit.getAndSet(false)) {
            this.application = application;
            this.args = args;
        }
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
     * SpringBoot应用启动成功后，再启动easyetl
     *
     * @param context   the application context or null if a failure occurred before the
     *                  context was created
     * @param exception any run exception or null if run completed successfully.
     */
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        if (notstart.getAndSet(false)) {
            EasyETLStarter.run(this.application, this.args, log);
            String[] filepaths = ClassUtils.getJavaClassPath();
            for (String filepath : filepaths) {
                System.out.println(filepath);
            }
        }
    }

}
