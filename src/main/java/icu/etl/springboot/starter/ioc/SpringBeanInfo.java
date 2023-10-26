package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.AnnotationBeanInfo;
import org.springframework.context.ApplicationContext;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringBeanInfo extends AnnotationBeanInfo {

    /**
     * 初始化
     */
    public SpringBeanInfo(ApplicationContext springContext) {
        super(ApplicationContext.class);
        this.singleton = true;
        this.setInstance(springContext);
    }
}
