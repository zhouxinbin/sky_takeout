package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.ByteArrayInputStream;
import lombok.extern.slf4j.Slf4j;

@Data // 生成属性的 getter、setter，便于读取 OSS 配置
@AllArgsConstructor // 生成全参构造方法，方便配置类直接创建对象
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 将文件字节流上传到阿里云 OSS。
     *
     * @param bytes      文件内容的字节数组
     * @param objectName OSS 中的对象名称，也就是文件在桶内的存储路径
     * @return 文件上传后的公网访问地址
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建 OSSClient 实例，用于和 OSS 服务交互。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 将字节流写入指定 bucket 的指定路径。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            // 无论上传成功还是失败，都及时释放客户端资源。
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // OSS 的公网访问地址规则： https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://")
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件上传到: {}", stringBuilder.toString());

        return stringBuilder.toString();
    }
}
