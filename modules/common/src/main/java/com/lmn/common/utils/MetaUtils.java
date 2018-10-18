package com.lmn.common.utils;

import com.google.common.collect.Lists;
import com.lmn.common.config.Const;
import com.lmn.common.entity.Meta;
import com.lmn.common.entity.MetaContent;
import com.lmn.common.service.MetaService;
import com.lmn.common.ui.UploadMsg;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传类
 */
public class MetaUtils {
    private static Logger logger = LoggerFactory.getLogger(MetaUtils.class);

    private static MetaService metaService = SpringContextHolder.getBean(MetaService.class);

    //文件扩展名过滤字段
    private static HashMap<String, String> extHash = new HashMap<String, String>();

    static {
        extHash.put("image", "gif,jpg,jpeg,png,bmp,jfif,");
        extHash.put("flash", "swf,flv,");
        extHash.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,mp4,");
        extHash.put("file", "doc,docx,xls,xlsx,ppt,pdf,htm,html,txt,zip,rar,gz,bz2,");
        //文件理论上可以上传全部的文件类型
        extHash.put("file", extHash.get("image") + extHash.get("flash") + extHash.get("media") + extHash.get("file"));
    }

    /**
     * 根据文件类型获取文件大类
     */
    public static String fileType(String fileName) {
        if (StringUtils.isBlank(fileName)) return "";
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (Map.Entry<String, String> entry : extHash.entrySet()) {
            if (entry.getValue().contains(fileExt)) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * 验证文件扩展名合法性
     */
    public static boolean validateExt(String dirName, String fileName) {
        if (StringUtils.isBlank(dirName) || StringUtils.isBlank(fileName)) return false;

        //检查扩展名
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (Arrays.<String>asList(extHash.get(dirName).split(",")).contains(fileExt)) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否为图片文件
     */
    public static boolean isImage(String fileName) {
        return MetaUtils.validateExt("image", fileName);
    }

    /**
     * 验证文件合法性
     */
    public static UploadMsg validate(MultipartFile file, String dirName, UploadMsg msg) {
        if (file == null) {
            msg.setMessage("上传文件不存在");
            msg.setCode(1);
            return msg;
        }

        if (extHash.get(dirName) == null) {
            msg.setMessage("目录名不正确");
            msg.setCode(3);
            return msg;
        }

        //检查文件大小
        if (file.getSize() > Const.getMediaSize()) {
            msg.setMessage("上传文件大小超过限制");
            msg.setCode(5);
            return msg;
        }

        String fileName = file.getOriginalFilename();
        //兼容剪贴板文件上传
        if ("blob".equals(fileName)) {
            fileName = file.getContentType().replace("/", ".");
        }

        // 检查文件扩展名
        if (!validateExt(dirName, fileName)) {
            msg.setMessage("上传文件扩展名是不允许的扩展名。\\n只允许" + extHash.get(dirName) + "格式。");
            msg.setCode(6);
            return msg;
        }
        return msg;
    }

    /**
     * 保存文件
     */
    public static UploadMsg saveFile(HttpServletRequest request, MultipartFile file, String dirName) {
        UploadMsg msg = new UploadMsg();

        if (dirName == null) {
            dirName = "file";
        }

        //验证文件是否符合规则
        validate(file, dirName, msg);
        //msg.setMessage();

        if (msg.getCode() != 0) {
            return msg;
        }

        // 上传文件夹
        String uploadDir = (Const.USERFILES_BASE_URL + dirName + File.separator + DateTime.now().toString("yyyyMMdd") + File.separator).replaceAll("/{2,}", "/");

        // 工程路径
        String projectDir;
        if (StringUtils.isNotBlank(Const.getUserfilesBaseDir())) {
            projectDir = Const.getUserfilesBaseDir();
        } else {
            projectDir = Const.getBaseDir();
        }

        File dirFile = new File((projectDir + uploadDir).replaceAll("/{2,}", "/"));
        //如果文件夹不存在就主动创建
        if (!dirFile.isDirectory()) {
            if (!dirFile.mkdirs()) {
                msg.setCode(3);
                msg.setMessage("创建上传文件夹失败。");
                return msg;
            }
        }

        //检查目录写权限
        if (!dirFile.canWrite()) {
            msg.setCode(3);
            msg.setMessage("上传目录没有写权限。");
            return msg;
        }

        //计算文件sha1值，防止重复上传
        String sha1 = FileUtils.sha1(file);

        String fileName = file.getOriginalFilename();
        if ("blob".equals(fileName)) {
            fileName = file.getContentType().replace("/", ".");
        }

        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String filePath = uploadDir + sha1 + "." + fileExt;

        Meta meta = new Meta();
        meta.setHash(sha1);
        try {
            List<Meta> metas = metaService.findList(meta);
            File serverFile;

            if (metas.size() == 0) {
                meta.setPath(StringUtils.replace(filePath, "\\", "/"));
                meta.setExt(fileExt);
                meta.setSize(file.getSize());
                meta.setName(fileName);

                serverFile = new File((projectDir + filePath).replaceAll("/{2,}", "/"));
                FileUtils.copyToFile(file.getInputStream(), serverFile);

                metaService.save(meta);
            } else {
                meta = metas.get(0);
                serverFile = new File(projectDir + meta.getPath());
                //当文件不存在时保存文件到目标文件
                if (!serverFile.exists()) {
                    //防止文件夹未创建
                    //serverFile.mkdirs();
                    FileUtils.copyToFile(file.getInputStream(), serverFile);
                }
            }

            // 仅当文章id存在时保存媒体文件与内容关联关系
            String contentId = request.getParameter("contentId");
            if (StringUtils.isNotBlank(contentId)) {
                // 处理媒体文件关系表
                MetaContent metaContent = new MetaContent();
                metaContent.setMetaId(meta.getId());
                metaContent.setContentId(contentId);

                saveMetaContent(metaContent);
            } else {
                //当未保存媒体文件关联表时返回metaId到前台
                msg.setMetaId(meta != null ? meta.getId() : "");
            }
            msg.setId(meta.getId());
            msg.setName(fileName);
            msg.setSize(meta.getSize());
            msg.setUrl(meta.getPath());

            return msg;
        } catch (Exception e) {
            msg.setMessage("上传文件失败。");
            msg.setCode(3);
            logger.warn(e.getMessage());
        }

        return msg;
    }

    public static void saveMetaContent(List<MetaContent> metaContents) {
        metaService.insertMetaContent(metaContents);
    }

    public static void saveMetaContent(MetaContent metaContent) {
        saveMetaContent(Lists.newArrayList(metaContent));
    }

    public static Meta getByHash(String hash) {
        return metaService.getByHash(hash);
    }

    public static List<Meta> findByMeta(String contentId) {
        return metaService.findByContentId(contentId);
    }

    public static List<Meta> findByMeta(String contentId, String ext) {
        if (StringUtils.isNotBlank(ext)) {
            ext = StringUtils.stripStart(ext, ".");
        }

        return metaService.findByContentId(contentId, ext);
    }

    public static List<Map> findListByOffice(Map map) {
        return metaService.findListByOffice(map);
    }

    public static List<Map> findListByStation(Map map) {
        return metaService.findListByStation(map);
    }

    public static List<Map> findMetaListByDocument(Map map) {
        return metaService.findMetaListByDocument(map);
    }

}
