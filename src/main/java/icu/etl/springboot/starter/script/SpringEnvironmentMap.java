package icu.etl.springboot.starter.script;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 代理类，从Spring容器的Environment中取值
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/25
 */
public class SpringEnvironmentMap implements Map<String, String> {

    private Environment env;

    public SpringEnvironmentMap(ApplicationContext context) {
        this.env = context.getEnvironment();
    }

    public boolean containsKey(Object key) {
        return this.env.containsProperty((String) key);
    }

    public String get(Object key) {
        return this.env.getProperty((String) key);
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
