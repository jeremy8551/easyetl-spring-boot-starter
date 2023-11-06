package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.EasyBeanInfo;
import org.springframework.context.ApplicationContext;

/**
 * 将 Spring 容器上下文信息转为可识别的组件信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringEasyBeanInfo extends EasyBeanInfo {

    /**
     * 将 Spring 容器上下文信息转为 Easyetl 中可识别的组件信息
     *
     * @param springContext Spring 容器上下文信息
     */
    public SpringEasyBeanInfo(ApplicationContext springContext) {
        super(ApplicationContext.class);
        this.singleton = true;
        this.setBean(springContext);
    }
}
