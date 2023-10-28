package icu.etl.springboot.starter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import icu.etl.ioc.EasyBeanContext;
import icu.etl.springboot.starter.ioc.SpringEasyBeanInfo;
import icu.etl.springboot.starter.ioc.SpringIocContext;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * easyetl组件的启动器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/4
 */
public class SpringEasyApplication {

    /**
     * 启动 easyetl 组件
     */
    public static void run(ConfigurableApplicationContext springContext, SpringApplication application, String[] args, Logger log) {
        long start = System.currentTimeMillis();
        String app = "easyetl-spring-boot-starter";

        if (application == null) {
            log.error("{} start fail!", app);
            throw new NullPointerException();
        } else {
            log.info("{} starting ..", app);
        }

        Class<?> cls = application.getMainApplicationClass();
        log.info("SpringBoot Application {}", cls.getName());
        log.info("SpringBoot Application args {}", StringUtils.toString(args));

        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();
        Annotation[] annotations = cls.getAnnotations();
        for (Object obj : annotations) {
            if (obj instanceof SpringBootApplication) {
                SpringBootApplication anno = (SpringBootApplication) obj;
                includes.addAll(ArrayUtils.asList(anno.scanBasePackages()));
                includes.addAll(ClassUtils.asNameList(anno.scanBasePackageClasses()));
                excludes.addAll(ArrayUtils.asList(anno.excludeName()));
                excludes.addAll(ClassUtils.asNameList(anno.exclude()));
            } else if (obj instanceof ComponentScan) {
                ComponentScan anno = (ComponentScan) obj;
                includes.addAll(ArrayUtils.asList(anno.value()));
                includes.addAll(ArrayUtils.asList(anno.basePackages()));
                includes.addAll(ClassUtils.asNameList(anno.basePackageClasses()));
            }
        }

        // 打印参数信息
        String argument = mergeToPackageExpression(includes, excludes);
        ClassLoader classLoader = application.getClassLoader();
        log.info("{} classLoader {}", app, (classLoader == null ? "" : classLoader.getClass().getName()));
        log.info("{} includeds package {}", app, StringUtils.join(includes, ","));
        log.info("{} excludeds package {}", app, StringUtils.join(excludes, ","));

        // 初始化组件容器的上下文信息
        EasyBeanContext context = new EasyBeanContext(classLoader, argument);
        context.addIoc(new SpringIocContext(springContext)); // 添加Spring容器上下文信息
        context.addBean(new SpringEasyBeanInfo(springContext)); // 将Spring容器上下文信息作为单例存储到容器中
        springContext.getBeanFactory().registerSingleton(EasyBeanContext.class.getSimpleName(), context); // 将Easyetl容器注册到Spring上下文中
        log.info("{} initialization in " + (System.currentTimeMillis() - start) + " ms ..", app);
    }

    private static String mergeToPackageExpression(List<String> includes, List<String> excludes) {
        StringBuilder buf = new StringBuilder(100);
        for (String pkg : includes) {
            buf.append(pkg).append(',');
        }

        // 添加排除包名
        for (String pkg : excludes) {
            buf.append('!').append(pkg).append(',');
        }

        return StringUtils.replaceLast(buf.toString(), ",", "");
    }

}
