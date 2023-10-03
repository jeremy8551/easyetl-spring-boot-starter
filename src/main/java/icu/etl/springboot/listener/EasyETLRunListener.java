package icu.etl.springboot.listener;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import icu.etl.ioc.BeanFactory;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
public class EasyETLRunListener implements SpringApplicationRunListener {

    private static Logger log = LoggerFactory.getLogger(EasyETLRunListener.class);

    private final static AtomicBoolean notinit = new AtomicBoolean(true);

    public EasyETLRunListener(SpringApplication application, String[] args) {
        if (notinit.getAndSet(false)) {
            System.out.println(this.getClass().getSimpleName() + " init ..");
            System.out.println(application.getClass().getName());
            System.out.println(StringUtils.toString(args));

            Class<?> cls = application.getMainApplicationClass();
            log.info("current SpringBoot Application Class -> " + (cls == null ? null : cls.getName()));

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
                    includes.addAll(ArrayUtils.asList(anno.basePackages()));
                    includes.addAll(ClassUtils.asNameList(anno.basePackageClasses()));
                }
            }

            ClassLoader classLoader = application.getClassLoader();
            BeanFactory.load(classLoader, includes, excludes);
        }
    }

    public void starting() {
        System.out.println(this.getClass().getSimpleName() + " starting ");
    }

    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println(this.getClass().getSimpleName() + " finished ");
    }
}
