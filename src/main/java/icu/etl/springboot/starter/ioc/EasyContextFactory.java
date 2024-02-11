package icu.etl.springboot.starter.ioc;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import icu.etl.concurrent.ThreadSource;
import icu.etl.ioc.EasyBeanContext;
import icu.etl.ioc.EasyContext;
import icu.etl.ioc.EasyContextInstance;
import icu.etl.ioc.scan.EasyScanPatternList;
import icu.etl.springboot.starter.ProjectPom;
import icu.etl.springboot.starter.concurrent.SpringExecutorFactory;
import icu.etl.springboot.starter.configuration.EasySpringArgument;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.FileUtils;
import icu.etl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 脚本引擎容器的工厂类
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/2/8 09:30
 */
public class EasyContextFactory {
    private final static Logger log = LoggerFactory.getLogger(EasyContextFactory.class);

    /**
     * 创建一个脚本引擎容器实例
     *
     * @param springContext Spring容器上下文信息
     * @return 脚本引擎容器实例
     */
    public static EasyContext create(ApplicationContext springContext) {
        long start = System.currentTimeMillis();
        String starterName = ProjectPom.getArtifactID(); // 场景启动器名
        log.info("{} starting ..", starterName);
        log.info("{} slf4j Logger is {}", starterName, log.getClass().getName()); // 打印日志接口的实现类

        // 准备类加载器
        ClassLoader classLoader = getClassLoader(springContext);
        log.info("{} classLoader is {}", starterName, classLoader.getClass().getName());

        // 准备启动参数
        String[] args = EasySpringArgument.get().getArgs();
        log.info("{} args {}", starterName, Arrays.toString(args));

        // 准备类扫描规则
        String[] array = getScanRule(starterName, classLoader, args);
        log.info("{} class scan pattern is {}", starterName, StringUtils.trim(Arrays.toString(array), '[', ']'));

        // 将Spring容器封装
        EasySpringContext spring = new EasySpringContext(springContext, starterName);
        EasyBeanContext parent = spring.getParent();
        SpringExecutorFactory pool = spring.getExecutorFactory(); // 在Spring容器中查找可用的线程池工厂

        // 初始化容器
        EasyBeanContext context = new EasyBeanContext(classLoader, array);
        context.setParent(parent);
        context.getBean(ThreadSource.class).setExecutorsFactory(pool); // 添加 Spring 容器中的线程池
        context.addIoc(spring); // 添加 Spring 容器
        context.addBean(new EasySpringBeanInfo(springContext)); // 将 Spring 容器上下文信息作为单例注册到容器中
        context.refresh();
        EasyContextInstance.set(context); // 设置单例

        // 打印启动成功标志
        log.info("{} initialization in {} ms ..", starterName, (System.currentTimeMillis() - start));
        return context;
    }

    private static String[] getScanRule(String starterName, ClassLoader classLoader, String[] args) {
        // 类扫描配置信息
        EasyScanPatternList list = new EasyScanPatternList();
        list.addProperty();
        list.addArgument(args);

        // 读取 SpringBoot 启动类上配置的类扫描规则
        SpringApplication application = EasySpringArgument.get().getApplication();
        if (application != null) {
            Class<?> mainApplicationClass = application.getMainApplicationClass();
            log.info("{} SpringBoot Application is {}", starterName, mainApplicationClass.getName());
            Annotation[] annotations = mainApplicationClass.getAnnotations();
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
        }

        // 如果没有配置扫描的包名，则自动扫描classpath下的所有类
        if (list.getScanPattern().isEmpty()) {
            String[] array = ClassUtils.getJavaClassPath();
            for (String classpath : array) {
                if (FileUtils.isDirectory(classpath)) {
                    list.addAll(ClassUtils.findShortPackage(classLoader, classpath));
                }
            }
        }

        // 添加默认组件
        list.addGroupID();
        return list.toArray();
    }

    private static ClassLoader getClassLoader(ApplicationContext springContext) {
        ClassLoader classLoader = springContext.getClassLoader(); // 类加载器
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        return classLoader;
    }
}
