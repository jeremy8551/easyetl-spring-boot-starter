package icu.etl.springboot.starter.ioc;

import java.util.Objects;

import icu.etl.ioc.BeanArgument;
import icu.etl.ioc.IocContext;
import org.springframework.context.ApplicationContext;

/**
 * Spring容器上下文的适配器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringIocContext implements IocContext {

    private ApplicationContext springContext;

    public SpringIocContext(ApplicationContext springContext) {
        this.springContext = Objects.requireNonNull(springContext);
    }

    public String getName() {
        return SpringIocContext.class.getSimpleName();
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean(Class<E> cls, Object[] args) {
        if (args.length == 0) {
            return springContext.getBean(cls);
        }

        if (args[0] instanceof String) {
            BeanArgument argument = new BeanArgument(args);

            // 组件名和匹配类查询
            E bean = this.springContext.getBean(argument.getName(), cls);
            if (bean != null) {
                return bean;
            }

            // args 作为构造方法的参数
            bean = (E) this.springContext.getBean(argument.getName(), argument.getArgs());
            if (bean != null) {
                return bean;
            }

            // 按组件名查询
            bean = (E) this.springContext.getBean(argument.getName());
            if (bean != null) {
                return bean;
            }
        }

        // args 作为构造方法的参数
        return this.springContext.getBean(cls, args);
    }

}
