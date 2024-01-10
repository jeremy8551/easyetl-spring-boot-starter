package icu.etl.springboot.starter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import icu.etl.concurrent.ThreadSource;
import icu.etl.ioc.EasyBeanContext;
import icu.etl.ioc.scan.EasyScanPatternList;
import icu.etl.log.LogFactory;
import icu.etl.log.slf4j.Slf4jLogBuilder;
import icu.etl.springboot.starter.concurrent.SpringExecutorsFactory;
import icu.etl.springboot.starter.ioc.SpringEasyBeanInfo;
import icu.etl.springboot.starter.ioc.SpringIocContext;
import icu.etl.util.ArrayUtils;
import icu.etl.util.ClassUtils;
import icu.etl.util.Ensure;
import icu.etl.util.FileUtils;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 场景启动器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/4
 */
public class EasySpringApplication {

    /**
     * 启动程序
     *
     * @param springContext Spring 容器的上下文信息
     * @param application   Springboot 启动程序
     * @param args          Springboot 启动程序的输入参数
     * @param log           日志接口
     */
    public static void run(ConfigurableApplicationContext springContext, SpringApplication application, String[] args, Logger log) {
        Ensure.notNull(application);
        ClassLoader classLoader = application.getClassLoader();
        Class<?> applicationClass = application.getMainApplicationClass();
        EasySpringApplication.run(classLoader, springContext, applicationClass, args, log);
    }

    /**
     * 启动程序
     *
     * @param classLoader          类加载器
     * @param springContext        Spring 容器的上下文信息
     * @param mainApplicationClass Springboot 启动程序
     * @param args                 Springboot 启动程序的输入参数
     * @param log                  日志接口
     */
    public static synchronized void run(ClassLoader classLoader, ConfigurableApplicationContext springContext, Class<?> mainApplicationClass, String[] args, Logger log) {
        long start = System.currentTimeMillis();
        Ensure.notNull(log);
        LogFactory.getContext().setStartMillis(start);
        LogFactory.getContext().setBuilder(new Slf4jLogBuilder());

        Ensure.notNull(springContext);
        String applicationName = icu.etl.ProjectPom.getArtifactID(); // 应用名
        String springbootStarterName = ProjectPom.getArtifactID(); // 场景启动器名

        // SpringCloud 启动时不执行第一次（只执行第二次）
        if ("bootstrap".equalsIgnoreCase(springContext.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("unable to start {}, because spring context id is bootstrap!", springbootStarterName);
            }
            return;
        }

        // 不能重复注册容器
        if (springContext.getBeanFactory().containsBeanDefinition(applicationName)) {
            if (log.isDebugEnabled()) {
                log.debug("unable to start {}, because {} has registered!", springbootStarterName, applicationName);
            }
            return;
        }

        // 开始启动
        log.info("{} starting ..", springbootStarterName);

        // 打印日志接口
        log.info("{} slf4j Logger is {}", springbootStarterName, log.getClass().getName());

        // 设置默认的类加载器
        if (classLoader == null) {
            classLoader = springContext.getClassLoader();
        }
        if (classLoader != null) {
            log.info("{} classLoader is {}", springbootStarterName, classLoader.getClass().getName());
        }

        // 类扫描配置信息
        EasyScanPatternList list = new EasyScanPatternList();
        list.addProperty();
        list.addArgument(args);
        log.info("{} args {}", springbootStarterName, Arrays.toString(args));

        // 读取 SpringBoot 启动类上配置的类扫描规则
        if (mainApplicationClass != null) {
            log.info("{} SpringBoot Application is {}", springbootStarterName, mainApplicationClass.getName());
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
        log.info("{} class scan pattern is {}", springbootStarterName, list.toArgumentString());

        // 初始化容器
        EasyBeanContext context = new EasyBeanContext(classLoader);
        context.setArgument(args);
        context.loadBeanInfo(list.toArray());
        context.setParent(findEasyContext(springbootStarterName, springContext, applicationName, log));
        context.addIoc(new SpringIocContext(springContext, log)); // 添加Spring容器上下文信息
        context.addBean(new SpringEasyBeanInfo(springContext)); // 将Spring容器上下文信息作为单例存储到容器中

        // 将容器注册到Spring中
        log.info("{} register to {}[id={}, appName={}, displayName={}]", springbootStarterName, springContext.getClass().getName(), springContext.getId(), springContext.getApplicationName(), springContext.getDisplayName());
        springContext.getBeanFactory().registerSingleton(applicationName, context);

        // 在Spring容器中查找可用的线程池
        findThreadPool(springbootStarterName, springContext, context, log);

        // 打印启动成功标志
        log.info("{} initialization in {} ms ..", springbootStarterName, (System.currentTimeMillis() - start));
    }

    /**
     * 在Spring容器中查找可用的线程池
     *
     * @param springbootStarterName 场景启动器名
     * @param springContext         Spring容器
     * @param context               容器
     * @param log                   日志接口
     */
    private static void findThreadPool(String springbootStarterName, ConfigurableApplicationContext springContext, EasyBeanContext context, Logger log) {
        ThreadSource source = context.getBean(ThreadSource.class);
        if (source == null) {
            return;
        }

        // 优先使用Springboot默认线程池
        try {
            ThreadPoolTaskExecutor pool = (ThreadPoolTaskExecutor) springContext.getBean("taskExecutor");
            if (pool != null) {
                log.info("{} use Springboot taskExecutor {}", springbootStarterName, pool);
                source.setExecutorsFactory(new SpringExecutorsFactory(pool));
                return;
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 使用Springboot自定义线程池
        try {
            ThreadPoolTaskExecutor service = springContext.getBean(ThreadPoolTaskExecutor.class);
            if (service != null) {
                log.info("{} use Spring ThreadPoolTaskExecutor {}", springbootStarterName, service);
                source.setExecutorsFactory(new SpringExecutorsFactory(service));
                return;
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 使用JDK自定义线程池
        try {
            ExecutorService service = springContext.getBean(ExecutorService.class);
            if (service != null) {
                log.info("{} use Spring ExecutorService {}", springbootStarterName, service);
                source.setExecutorsFactory(new SpringExecutorsFactory(service));
                return;
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 没有发现可用的线程池
        log.info("{} did not find any available thread pools in SpringContext!", springbootStarterName);
    }

    /**
     * 在Spring上级容器中查找bean
     *
     * @param springbootStarterName 场景启动器名
     * @param springContext         Spring容器
     * @param applicationName       bean名
     * @param log                   日志接口
     * @return 容器
     */
    protected static EasyBeanContext findEasyContext(String springbootStarterName, ConfigurableApplicationContext springContext, String applicationName, Logger log) {
        ApplicationContext parent = springContext;
        while ((parent = parent.getParent()) != null) {
            if (parent.containsBeanDefinition(applicationName)) {
                log.info("{} discover {}[id={}] already contains {} ..", springbootStarterName, springContext.getClass().getName(), parent.getId(), applicationName);
                EasyBeanContext context = (EasyBeanContext) parent.getBean(applicationName);
                if (context != null) {
                    return context;
                }
            }
        }
        return null;
    }

}
