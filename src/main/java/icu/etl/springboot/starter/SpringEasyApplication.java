package icu.etl.springboot.starter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import icu.etl.ioc.EasyBeanContext;
import icu.etl.ioc.EasyScanPatternList;
import icu.etl.springboot.starter.ioc.SpringEasyBeanInfo;
import icu.etl.springboot.starter.ioc.SpringIocContext;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.FileUtils;
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

    /** true表示Spring容器已刷新完，可以初始化容器 */
    public final static AtomicBoolean SPRING_CONTEXT_REFRESHED = new AtomicBoolean(false);

    /** 应用上下文信息在Spring容器中的名字 */
    public final static String APP_SPRING_BEANNAME = "easyetl";

    /** 应用名 */
    public final static String APP_NAME = "easyetl-spring-boot-starter";

    /**
     * 启动 easyetl
     *
     * @param springContext Spring 容器的上下文信息
     * @param application   Springboot 启动程序
     * @param args          Springboot 启动程序的输入参数
     * @param log           日志接口
     */
    public static synchronized void run(ConfigurableApplicationContext springContext, SpringApplication application, String[] args, Logger log) {
        if (!SpringEasyApplication.SPRING_CONTEXT_REFRESHED.get()) { // 等待启动通知
            if (log.isDebugEnabled()) {
                log.debug("unable to start {}, because spring context not refreshed!", APP_NAME);
            }
            return;
        }

        // SpringCloud 启动时不执行第一次（只执行第二次）
        if ("bootstrap".equalsIgnoreCase(springContext.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("unable to start {}, because spring context id is bootstrap!", APP_NAME);
            }
            return;
        }

        // 不能重复注册容器
        if (springContext.getBeanFactory().containsBeanDefinition(APP_SPRING_BEANNAME)) {
            if (log.isDebugEnabled()) {
                log.debug("unable to start {}, because {} has registered!", APP_NAME, APP_SPRING_BEANNAME);
            }
            return;
        }

        long start = System.currentTimeMillis();
        if (application == null) {
            log.error("{} start fail!", APP_NAME);
            throw new UnsupportedOperationException();
        } else {
            log.info("{} starting ..", APP_NAME);
        }

        Class<?> cls = application.getMainApplicationClass();
        log.info("{} SpringBoot {}", APP_NAME, cls.getName());
        log.info("{} SpringBoot args {}", APP_NAME, Arrays.toString(args));

        ClassLoader classLoader = application.getClassLoader();
        log.info("{} classLoader {}", APP_NAME, (classLoader == null ? "" : classLoader.getClass().getName()));

        EasyScanPatternList list = new EasyScanPatternList();
        Annotation[] annotations = cls.getAnnotations();
        for (Object obj : annotations) {
            if (obj instanceof SpringBootApplication) {
                SpringBootApplication anno = (SpringBootApplication) obj;
                list.addAll(ArrayUtils.asList(anno.scanBasePackages()));
                list.addAll(ClassUtils.asNameList(anno.scanBasePackageClasses()));
                list.exclude(ArrayUtils.asList(anno.excludeName()));
                list.exclude(ClassUtils.asNameList(anno.exclude()));
            } else if (obj instanceof ComponentScan) {
                ComponentScan anno = (ComponentScan) obj;
                list.addAll(ArrayUtils.asList(anno.value()));
                list.addAll(ArrayUtils.asList(anno.basePackages()));
                list.addAll(ClassUtils.asNameList(anno.basePackageClasses()));
            }
        }

        // 如果没有配置扫描的包名，则自动扫描classpath下的类文件
        if (list.getScanPattern().isEmpty()) {
            String[] array = ClassUtils.getJavaClassPath();
            for (String classpath : array) {
                if (FileUtils.isDirectory(classpath)) {
                    list.addAll(ClassUtils.findShortPackage(classLoader, classpath));
                }
            }
        }

        list.addProperty();
        list.addGroupID();
        log.info("{} scan pattern {}", APP_NAME, list.toArgumentString());

        // 初始化组件容器的上下文信息
        EasyBeanContext context = new EasyBeanContext(classLoader);
        context.setArgument(args);
        context.loadBeanInfo(list.toArray());
        context.addIoc(new SpringIocContext(springContext)); // 添加Spring容器上下文信息
        context.addBean(new SpringEasyBeanInfo(springContext)); // 将Spring容器上下文信息作为单例存储到容器中
        springContext.getBeanFactory().registerSingleton(APP_SPRING_BEANNAME, context); // 将Easyetl容器注册到Spring上下文中
        log.info("{} initialization in {} ms ..", APP_NAME, (System.currentTimeMillis() - start));
    }

}
