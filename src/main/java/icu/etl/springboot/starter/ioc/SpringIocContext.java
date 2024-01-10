package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.EasyContainerContext;
import icu.etl.ioc.impl.BeanArgument;
import icu.etl.util.Ensure;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Spring容器上下文的适配器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringIocContext implements EasyContainerContext {

    /** Spring容器上下文信息 */
    private ApplicationContext springContext;

    /** 日志接口 */
    private Logger log;

    /**
     * 初始化
     *
     * @param springContext Spring容器上下文信息
     * @param log           日志接口
     */
    public SpringIocContext(ApplicationContext springContext, Logger log) {
        this.springContext = Ensure.notNull(springContext);
        this.log = Ensure.notNull(log);
    }

    public String getName() {
        return this.springContext.getId();
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean(Class<E> cls, Object[] args) {
        if (args.length == 0) {
            return this.springContext.getBean(cls);
        }

        if (args[0] instanceof String) {
            BeanArgument argument = new BeanArgument(args);
            E bean = null;

            // 组件名和匹配类查询
            try {
                bean = this.springContext.getBean(argument.getName(), cls);
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }

            // args 作为构造方法的参数
            try {
                bean = (E) this.springContext.getBean(argument.getName(), argument.getArgs());
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }

            // 按组件名查询
            try {
                bean = (E) this.springContext.getBean(argument.getName());
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }
        }

        // args 作为构造方法的参数
        return this.springContext.getBean(cls, args);
    }

}
