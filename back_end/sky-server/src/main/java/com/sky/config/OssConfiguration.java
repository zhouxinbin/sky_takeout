package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 声明当前类是 Spring 配置类
@Slf4j // 生成日志对象，方便记录 Bean 创建过程
public class OssConfiguration {

    /**
     * 将配置文件中的 OSS 参数组装成工具类 Bean，
     * 这样控制器层只需要注入 {@link AliOssUtil} 即可完成上传。
     */
    @Bean // 将方法返回值交给 Spring 管理
    @ConditionalOnMissingBean // 容器中没有该 Bean 时才创建，避免重复定义
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云OSS上传工具类对象: {}", aliOssProperties);
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}
