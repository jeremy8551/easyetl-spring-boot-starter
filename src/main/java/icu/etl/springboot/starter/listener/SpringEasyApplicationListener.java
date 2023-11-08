package icu.etl.springboot.starter.listener;

import icu.etl.springboot.starter.SpringEasyApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 通知Spring已启动完毕，可以启动应用
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/8
 */
public class SpringEasyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * 通知Spring已启动完毕，可以启动应用
     *
     * @param event the event to respond to
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringEasyApplication.SPRING_CONTEXT_REFRESHED.getAndSet(true);
    }
}
