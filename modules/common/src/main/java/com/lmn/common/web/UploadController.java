package com.lmn.common.web;

import com.lmn.common.base.ApiData;
import com.lmn.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/file")
public class UploadController extends BaseController {


    @RequestMapping(value = "/upload")
    public ApiData uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile) {

        ApiData apiData = new ApiData();
        try {
            String filename = uploadfile.getOriginalFilename();
            String directory = "image";
            String filepath = Paths.get(directory, filename).toString();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            apiData.setMessage("文件上传失败");
            System.out.println(e.getMessage());
        }

        return apiData;
    }

}
