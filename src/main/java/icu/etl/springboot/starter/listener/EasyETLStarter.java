package icu.etl.springboot.starter.listener;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import icu.etl.ioc.BeanContext;
import icu.etl.ioc.EasyetlContext;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * easyetl组件的启动器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/4
 */
public class EasyETLStarter {

    /** 组件容器的上下文信息 */
    public static EasyetlContext CONTEXT;

    /**
     * 启动 easyetl 组件
     */
    public static void run(SpringApplication application, String[] args, Logger log) {
        if (application == null) {
            log.error("easyetl-spring-boot-starter start fail!");
            throw new NullPointerException();
        } else {
            log.info("easyetl-spring-boot-starter starting ..");
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
        log.info("easyetl scanner classLoader {}", (classLoader == null ? "" : classLoader.getClass().getName()));
        log.info("easyetl includeds package {}", StringUtils.join(includes, ","));
        log.info("easyetl excludeds package {}", StringUtils.join(excludes, ","));

        // 初始化组件容器的上下文信息
        long start = System.currentTimeMillis();
        CONTEXT = new BeanContext(classLoader, argument);
        log.info("easyetl initialization context in " + (System.currentTimeMillis() - start) + " ms ..");
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
