package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.EasyBeanInfo;
import org.springframework.context.ApplicationContext;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringEasyBeanInfo extends EasyBeanInfo {

    /**
     * 初始化
     */
    public SpringEasyBeanInfo(ApplicationContext springContext) {
        super(ApplicationContext.class);
        this.singleton = true;
        this.setBean(springContext);
    }
}
