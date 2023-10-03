package icu.etl.springboot.stater;

import icu.etl.util.IO;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@ConfigurationProperties(prefix = "icu.etl")
public class EasyETLProperties {

    // 两个属性 用于绑定配置
    private String prefix;
    private String suffix;

    /**
     * 字节输入流缓冲区大小 {@linkplain IO#BYTES_BUFFER_SIZE} ，单位字节
     */
    private String readerBufferSize;

    public String getReaderBufferSize() {
        return readerBufferSize;
    }

    public void setReaderBufferSize(String readerBufferSize) {
        this.readerBufferSize = readerBufferSize;
        IO.BYTES_BUFFER_SIZE = Integer.parseInt(readerBufferSize);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}

