package icu.etl.springboot.starter.ioc;

import icu.etl.ioc.BeanArgument;
import icu.etl.ioc.EasyetlIoc;
import org.springframework.context.ApplicationContext;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringEasyetlIoc implements EasyetlIoc {

    private ApplicationContext springContext;

    public SpringEasyetlIoc(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    public String getName() {
        return SpringEasyetlIoc.class.getSimpleName();
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
