package com.lmn.common.servlet;

import com.lmn.common.config.Const;
import com.lmn.common.utils.FileUtils;
import com.lmn.common.utils.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 查看上传的文件，如果是图片文件自动根据参数对图片进行处理
 */
public class UserfilesDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(UserfilesDownloadServlet.class);
    private static BufferedImage watermarkImage = null;
    private static String parentPath = null;

    //默认文件
    private static File defaultFile;

    private void fileOutputStream(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filepath = req.getRequestURI();
        int index = filepath.indexOf(Const.USERFILES_BASE_URL);
        if (index >= 0) {
            filepath = filepath.substring(index + Const.USERFILES_BASE_URL.length());
        }

        filepath = UriUtils.decode(filepath, "UTF-8");
        if (parentPath == null) {
            parentPath = Const.getUserfilesDir();

            defaultFile = new File(parentPath + "default.jpg");

            try {
                InputStream inputStream = UserfilesDownloadServlet.class.getResourceAsStream("/default.jpg");
                FileUtils.copyInputStreamToFile(inputStream, defaultFile);
            } catch (IOException e) {
                logger.error("文件默认图片加载错误{}", e.getMessage());
            }
        }


        //缩略图水印
        /*if (watermarkImage == null) {
            try {
                watermarkImage = ImageIO.read(new File(parentPath + "watermark.png"));
            } catch (Exception e) {
                watermarkImage = null;
            }
        }*/

        File file = new File((parentPath + filepath).replaceAll("/{2,}", "/"));
        boolean flag = false;

        if (!file.exists()) {
            file = defaultFile;
            flag = true;
        }

        try {
            resp.setHeader("Content-Type", FileUtils.getContentType(file.getPath()));

            if (!StringUtils.startsWith(filepath, "doc/")) {
                //显示下载对话框
                resp.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            }

            resp.setDateHeader("Last-Modified", file.lastModified());

            Integer dw = StringUtils.toInteger(req.getParameter("w"));
            Integer dh = StringUtils.toInteger(req.getParameter("h"));

            if (dw == 0 && dh == 0 || !FileUtils.isImage(file)) {
                FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
            } else {

                BufferedImage image = ImageIO.read(file);
                Integer sw = image.getWidth(), sh = image.getHeight();

                //当指定大小大于原始图片时直接返回结果
                /*if (dw > sw || dh > sh) {
                    FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
                } else {*/
                if (dh == 0) dh = dw;
                if (dw == 0) dw = dh;

                int tw = dw * sh / dh;
                int th = sw * dh / dw;

                if (tw > sw || th > sh) {
                    if (tw > sw) {
                        tw = sw;
                    } else {
                        th = sh;
                    }
                }

                if (flag) {
                    resp.sendRedirect(String.format("/userfiles/default.jpg?w=%s&h=%s", dw, dh));
                    return;
                }
                Thumbnails.of(file).outputQuality(1.0f).sourceRegion(Positions.CENTER, tw, th).forceSize(dw, dh).toOutputStream(resp.getOutputStream());
            }
        } catch (Exception e) {
            resp.setStatus(404);
            //resp.sendError(404, "无资源");
            //resp.sendRedirect("/assets/common/img/default.jpg");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        fileOutputStream(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        fileOutputStream(req, resp);
    }
}
