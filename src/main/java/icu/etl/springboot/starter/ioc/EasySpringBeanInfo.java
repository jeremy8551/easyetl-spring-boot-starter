package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.impl.EasyBeanInfoImpl;
import org.springframework.context.ApplicationContext;

/**
 * 将 Spring 容器上下文信息转为可识别的组件信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class EasySpringBeanInfo extends EasyBeanInfoImpl {

    /**
     * 将 Spring 容器上下文信息转为 Easyetl 中可识别的组件信息
     *
     * @param springContext Spring 容器上下文信息
     */
    public EasySpringBeanInfo(ApplicationContext springContext) {
        super(springContext.getClass());
        this.singleton = true;
        this.setBean(springContext);
    }
}
