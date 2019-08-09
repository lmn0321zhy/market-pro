package com.lmn.common.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

public class FTPUtils {

    private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    //    ftp服务器地址
    private static String hostname = "10.160.217.112";
    //    ftp服务器端口号默认为21
    private static Integer port = 21;
    //    ftp登录账号
    private static String username = "anonymous";
    //    ftp登录密码
    private static String password = "123";

    private static String LOCAL_CHARSET = "GBK";

    private static String DEFAULT_ZIP_PATH = "zip";

    /**
     * 初始化ftp服务器
     */
    public static FTPClient initFtpClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(LOCAL_CHARSET);
        try {
            ftpClient.connect(hostname, port); //连接ftp服务器
            ftpClient.login(username, password); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.info("connect failed...ftp服务器:");
            } else {
                logger.info("connect successfu...ftp服务器:");
            }
            //设置编码
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }


    /**
     * 上传文件
     *
     * @param pathname       ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public static boolean uploadFile(String pathname, String fileName, String originfilename) {
        InputStream inputStream = null;
        FTPClient initFtpClient = initFtpClient();
        try {
            logger.info("开始上传文件");
            //把文件转化为流
            inputStream = new FileInputStream(new File(originfilename));
            if (!initFtpClient.changeWorkingDirectory(pathname)) {
                initFtpClient.makeDirectory(pathname);
                initFtpClient.changeWorkingDirectory(pathname);
            } else {
                initFtpClient.changeWorkingDirectory(pathname);
            }
            initFtpClient.storeFile(fileName, inputStream);
            logger.info("上传文件成功");
        } catch (Exception e) {
            logger.info("上传文件失败");
            e.printStackTrace();
        } finally {
            if (initFtpClient.isConnected()) {
                try {
                    initFtpClient.logout();
                    initFtpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public static boolean downloadFile(String pathname, String filename, String localpath) {
        boolean flag = false;
        FTPClient initFtpClient = initFtpClient();
        OutputStream os = null;
        try {
            logger.info("开始下载文件");
            //切换FTP目录
            initFtpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = initFtpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    if (file.isDirectory()) {
                        initFtpClient.makeDirectory(DEFAULT_ZIP_PATH);
                        initFtpClient.changeWorkingDirectory(DEFAULT_ZIP_PATH);
                        File zipFile = new File("123.zip");
//                        initFtpClient.storeFile(filename);
                        ZipOutputStream zs = new ZipOutputStream(new FileOutputStream(zipFile));
//                        ZipOutputStream zs = new ZipOutputStream(new File(DEFAULT_ZIP_PATH + "/" + file.getName() + ".zip"));
                        writeZip(initFtpClient, pathname + "/" + filename, zs);
                        zs.closeEntry();
                    } else {
                        File localFile = new File(localpath + "/" + file.getName());
                        os = new FileOutputStream(localFile);
                        initFtpClient.retrieveFile(file.getName(), os);
                        os.close();
                    }
                }
            }
            initFtpClient.logout();
            flag = true;
            logger.info("下载文件成功");
        } catch (Exception e) {
            logger.info("下载文件失败");
            e.printStackTrace();
        } finally {
            if (initFtpClient.isConnected()) {
                try {
                    initFtpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 实现文件打包压缩下载
     *
     * @param response    服务器响应对象HttpServletResponse
     * @param zipname     压缩包的文件名
     * @param ftpPath     ftp文件路径
     * @param ftpFileList 要下载的文件名集合
     * @param namelist    压缩后的文件名集合
     */
    public static void zipDownloadFile( String zipname, String ftpPath, List<String> ftpFileList, List<String> namelist) {
        FTPClient ftp = initFtpClient();
        byte[] buf = new byte[1024];
        try {
            ftp.enterLocalPassiveMode();
            ftp.changeWorkingDirectory(ftpPath);
            File zipFile = new File(zipname);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < ftpFileList.size(); i++) {
                ZipEntry entry = new ZipEntry((i + 1) + "_" + namelist.get(i));
                zipOut.putNextEntry(entry);
                InputStream bis = ftp.retrieveFileStream(ftpFileList.get(i));
                if (bis != null) {
                    int readLen = -1;
                    while ((readLen = bis.read(buf, 0, 1024)) != -1) {
                        zipOut.write(buf, 0, readLen);
                    }
                    zipOut.closeEntry();
                    bis.close();
                    ftp.completePendingCommand();
                    //调用ftp.retrieveFileStream这个接口后，一定要手动close掉返回的InputStream，然后再调用completePendingCommand方法
                    // ，若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
                }
            }
            zipOut.close();
            ftp.logout();
            //下载
//            int len;
//            FileInputStream zipInput = new FileInputStream(zipFile);
//            OutputStream out = response.getOutputStream();
//            response.setContentType("application/octet-stream");
//            response.addHeader("Content-Disposition", "attachment; filename="
//                    + URLEncoder.encode(zipname, "UTF-8") + ".zip");
//            while ((len = zipInput.read(buf)) != -1) {
//                out.write(buf, 0, len);
//            }
//            zipInput.close();
//            out.flush();
//            out.close();
//            //删除压缩包
//            zipFile.delete();
        } catch (Exception e) {
            logger.error("文件打包下载有误: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    private static void writeZip(FTPClient initFtpClient, String parentPath, ZipOutputStream zos) throws IOException {
        initFtpClient.changeWorkingDirectory(parentPath);
        FTPFile[] ftpFiles = initFtpClient.listFiles();
        for (FTPFile file : ftpFiles) {
            if (file.isDirectory()) {
                String name = file.getName();
                writeZip(initFtpClient, parentPath + "/" + name, zos);
            } else {
                String zipName = parentPath +"/"+ file.getName();
                InputStream bis = initFtpClient.retrieveFileStream(file.getName());
                ZipEntry ze = new ZipEntry(new String(zipName.getBytes(), "Shift_JIS"));
                zos.putNextEntry(ze);
                byte[] content = new byte[1024];
                int len;
                while ((len = bis.read(content)) != -1) {
                    zos.write(content, 0, len);
                    zos.flush();
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        FTPUtils ftp = new FTPUtils();
        ftp.uploadFile("ftpFile/data", "张三.7z", "F://aa.docx");
//        ftp.downloadFile("", "ftpFile", "F://");
//        zipDownloadFile("123.zip","ftpFile",)
//            ftp.deleteFile("ftpFile/data", "123.docx");
//            logger.info("ok");
//        ZipOutputStream os=new ZipOutputStream(new File("F://test.zip"));
//        ftp.writeZip("ftpFile","",os);
//        FTPUtils f = new FTPUtils();
//        f.initFtpClient();


    }
}