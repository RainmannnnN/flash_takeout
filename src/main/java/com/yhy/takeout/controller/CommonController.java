package com.yhy.takeout.controller;

import com.yhy.takeout.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${flash.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info(file.toString());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID生成随机文件名
        String fileName = UUID.randomUUID().toString() + substring;
        // 创建一个目录对象，如果目录不存在，则要创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 把文件保存在指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            // 输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 输出流写会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
