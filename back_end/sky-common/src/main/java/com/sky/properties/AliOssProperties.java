package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 相关配置项。
 * <p>
 * 该类负责把 application-dev.yml 中的 sky.alioss 配置映射成 Java 对象，
 * 供 OSS 工具类在启动时注入使用。
 */
@Component // 交给 Spring 容器管理，才能自动绑定配置项
@ConfigurationProperties(prefix = "sky.alioss") // 读取 application.yml 中 sky.alioss 下的配置
@Data // Lombok 自动生成 getter、setter、toString 等方法
public class AliOssProperties {

    /** OSS 服务访问域名，例如：oss-cn-hangzhou.aliyuncs.com */
    private String endpoint;

    /** 阿里云账号的 AccessKeyId */
    private String accessKeyId;

    /** 阿里云账号的 AccessKeySecret */
    private String accessKeySecret;

    /** OSS 存储桶名称 */
    private String bucketName;

}
