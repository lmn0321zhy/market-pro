package com.lmn.common.sys.web;

import cn.wenwuyun.common.persistence.ApiData;
import cn.wenwuyun.common.ui.FileInfo;
import cn.wenwuyun.common.ui.ManageMsg;
import cn.wenwuyun.common.ui.UploadMsg;
import cn.wenwuyun.common.utils.StringUtils;
import cn.wenwuyun.common.web.BaseController;
import cn.wenwuyun.common.web.Servlets;
import cn.wenwuyun.modules.sys.entity.Meta;
import cn.wenwuyun.modules.sys.entity.MetaContent;
import cn.wenwuyun.modules.sys.service.MetaService;
import cn.wenwuyun.modules.sys.utils.MetaUtils;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 媒体数据Controller
 */
@RestController
@RequestMapping(value = "${apiPath}/sys/meta")
public class MetaController extends BaseController {

    @Autowired
    private MetaService metaService;

    @ModelAttribute
    public Meta get(@RequestParam(required = false) String id) {
        Meta entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = metaService.get(id);
        }
        if (entity == null) {
            entity = new Meta();
        }
        return entity;
    }

    @RequiresUser
    @RequestMapping(value = "upload")
    public Object upload(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String dir) {

        List<UploadMsg> list = new ArrayList<>();

        if (StringUtils.isBlank(dir)) {
            dir = "image";
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> item = multipartRequest.getFileNames();
        MultipartFile file = null;
        while (item.hasNext()) {
            String fileName = item.next();

            List<MultipartFile> files = multipartRequest.getFiles(fileName);
            for (int i = 0; i < files.size(); i++) {
                file = files.get(i);
                list.add(MetaUtils.saveFile(request, file, dir));
            }
            /*file = multipartRequest.getFile(fileName);
            list.add(MetaUtils.saveFile(request, file, dir));*/
        }

        Map result = new HashMap();
        String editor = request.getParameter("editor");
        //对 simditor编辑器上传逻辑处理
        if ("simditor".equals(editor)) {
            /*
            * simditor返回数据格式
            * {
                  "success": true/false,
                  "msg": "error message", # optional
                  "file_path": "[real file path]"
               }
            * */

            for (UploadMsg msg : list) {
                result.put("success", msg.getCode() == 0);
                result.put("msg", msg.getMessage());
                result.put("file_path", Servlets.domain(request) + msg.getUrl());
            }
            return result;
        } else if ("froala".equals(editor)) {
            for (UploadMsg msg : list) {
                if (StringUtils.isBlank(msg.getMessage())) {
                    result.put("code", msg.getCode());
                    result.put("link", Servlets.domain(request) + msg.getUrl());
                } else {
                    result.put("code", msg.getCode());
                    result.put("message", msg.getMessage());
                }
            }
            return result;
        }

        return new ApiData<>(list);
    }

    @RequiresUser
    @RequestMapping(value = "manager")
    public Object manager(HttpServletRequest request, @RequestParam(required = false) String contentId) {
        String editor = request.getParameter("editor");
        ManageMsg manageMsg = metaService.findManageMeta(contentId);

        if ("froala".equals(editor)) {
            /*[
            {
                url: 'http://exmaple.com/images/photo1.jpg',
                        thumb: "http://exmaple.com/thumbs/photo1.jpg",
                    tag: 'flower'
                name: "Photo 1 Name",
                        id: 103454285,
            }
            ]*/
            List<Map> result = new ArrayList<>();
            String url = Servlets.domain(request);
            for (FileInfo fileInfo : manageMsg.getFile_list()) {
                Map temp = new HashMap();
                temp.put("url", url + fileInfo.getUrl());
                temp.put("thumb", url + fileInfo.getUrl() + "?w=120");
                temp.put("name", fileInfo.getName());
                temp.put("id", fileInfo.getId());
                result.add(temp);
            }
            return result;
        }

        return new ApiData<>(manageMsg);
    }

    @RequiresPermissions("sys:file:edit")
    @RequestMapping(value = "delete")
    public ApiData delete(Meta meta, HttpServletRequest request) {
        ApiData apiData = new ApiData();

        String contentId = request.getParameter("contentId");
        if (StringUtils.isNotBlank(meta.getId()) && StringUtils.isNotBlank(contentId)) {
            MetaContent metaContent = new MetaContent(meta.getId(), contentId);
            metaService.deleteMetaContent(metaContent);
            apiData.setMessage("删除媒体关联成功");
        } else {
            apiData.setMessage("删除媒体关联失败");
        }
        return apiData;
    }

    @RequiresPermissions("sys:file:edit")
    @RequestMapping(value = "metadelete")
    public ApiData delete(Meta meta) {
        ApiData apiData = new ApiData();
        if (StringUtils.isNotBlank(meta.getId())) {
            metaService.delete(meta);
            apiData.setMessage("删除文件成功");
        } else {
            apiData.setMessage("删除文件失败");
        }
        return apiData;
    }


    @RequiresUser
    @RequestMapping(value = "deletemetacontent")
    public ApiData deleteMetaContent(@RequestParam String id, @RequestParam String contentId) {
        ApiData apiData = new ApiData();

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(contentId)) {
            MetaContent metaContent = new MetaContent(id, contentId);
            try {
                metaService.deleteMetaContent(metaContent);
                apiData.setMessage("删除成功！");
            } catch (Exception e) {
                apiData.setMessage("删除失败！");
            }
        } else {
            apiData.setMessage("参数错误！");
        }
        return apiData;
    }

    @RequestMapping(value = "files")
    public ApiData files(Meta meta, HttpServletRequest request, HttpServletResponse response, Model model) {
        PageInfo<Meta> page = metaService.findByUserPage(request, meta);
        return new ApiData<>(page);
    }
}