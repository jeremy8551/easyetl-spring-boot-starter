package icu.etl.springboot.starter.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 通知Spring已启动完毕
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/8
 */
public class EasySpringApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * 通知Spring已启动完毕
     *
     * @param event the event to respond to
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }
}
