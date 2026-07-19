package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 通用接口，主要提供文件上传等公共能力。
 */
@RestController // 声明这是一个 REST 风格控制器，返回值直接写入响应体
@RequestMapping("/admin/common") // 统一定义接口前缀
@Api(tags = "通用接口") // Swagger 文档分组名称
@Slf4j // 生成日志对象，方便记录上传过程
public class CommonController {

    /** 注入 OSS 工具类，负责真正的文件上传 */
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传到 OSS。
     *
     * @param file 前端上传的文件
     * @return 上传成功后文件的访问地址
     */
    @PostMapping("/upload") // 指定上传接口的 HTTP 路径和请求方法
    @ApiOperation("文件上传") // Swagger 接口说明
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求: {}", file != null ? file.getOriginalFilename() : "null");
        // 获取原始文件名，例如 "image.jpg"
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀名，例如 ".jpg"
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成唯一文件名，避免覆盖
        String uniqueFileName = UUID.randomUUID().toString() + suffix;
        try {
            String failPath = aliOssUtil.upload(file.getBytes(), uniqueFileName);
            return Result.success(failPath);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
