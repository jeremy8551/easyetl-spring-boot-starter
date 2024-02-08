package icu.etl.springboot.starter.configuration;

import org.springframework.boot.SpringApplication;

/**
 * Springboot程序启动参数
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/2/7 15:24
 */
public class EasySpringArgument {

    private static final EasySpringArgument instance = new EasySpringArgument();

    /**
     * 返回启动参数实例对象
     *
     * @return 启动参数
     */
    public static EasySpringArgument get() {
        return instance;
    }

    /** 当前SpringBoot应用 */
    private SpringApplication application;

    /** SpringBoot应用的启动参数 */
    private String[] args;

    public EasySpringArgument() {
    }

    public SpringApplication getApplication() {
        return application;
    }

    public void setApplication(SpringApplication application) {
        this.application = application;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
