package com.lmn.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtils {

    //    ftp服务器地址
    private static String hostname = "10.160.217.112";
    //    ftp服务器端口号默认为21
    private static Integer port = 21;
    //    ftp登录账号
    private static String username = "anonymous";
    //    ftp登录密码
    private static String password = "123";

    private static FTPClient ftpClient = null;

    /**
     * 初始化ftp服务器
     */
    public static FTPClient initFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
//            System.out.println("connecting...ftp服务器:"+ftp.get("ftpIp")+":"+ftp.get("port"));
            ftpClient.connect(hostname, port); //连接ftp服务器

            ftpClient.login(username, password); //登录ftp服务器

            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器:");
            } else {
                System.out.println("connect successfu...ftp服务器:");
            }
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
        boolean flag = false;
        InputStream inputStream = null;
        FTPClient initFtpClient = initFtpClient();
        try {
            System.out.println("开始上传文件");
            //把文件转化为流
            inputStream = new FileInputStream(new File(originfilename));
            //初始化ftp
            initFtpClient();
            //设置编码
            initFtpClient.setFileType(initFtpClient.BINARY_FILE_TYPE);
            //文件需要保存的路径
            CreateDirecroty(pathname, initFtpClient);
            //
            initFtpClient.makeDirectory(pathname);
            //
            initFtpClient.changeWorkingDirectory(pathname);
            //
            initFtpClient.storeFile(fileName, inputStream);

            inputStream.close();
            initFtpClient.logout();
            flag = true;
            System.out.println("上传文件成功");
        } catch (Exception e) {
            System.out.println("上传文件失败");
            e.printStackTrace();
        } finally {
            if (initFtpClient.isConnected()) {
                try {
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
     * 上传文件
     *
     * @param pathname    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public static boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
        FTPClient initFtpClient = initFtpClient();
        boolean flag = false;
        try {
            System.out.println("开始上传文件");
            initFtpClient();
            initFtpClient.setFileType(initFtpClient.BINARY_FILE_TYPE);
            CreateDirecroty(pathname, initFtpClient);
            initFtpClient.makeDirectory(pathname);
            initFtpClient.changeWorkingDirectory(pathname);
            initFtpClient.storeFile(fileName, inputStream);
            inputStream.close();
            initFtpClient.logout();
            flag = true;
            System.out.println("上传文件成功");
        } catch (Exception e) {
            System.out.println("上传文件失败");
            e.printStackTrace();
        } finally {
            if (initFtpClient.isConnected()) {
                try {
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

    //改变目录路径
    public static boolean changeWorkingDirectory(String directory, FTPClient initFtpClient) {
        boolean flag = true;
        try {
            flag = initFtpClient.changeWorkingDirectory(directory);
            if (flag) {
                System.out.println("进入文件夹" + directory + " 成功！");

            } else {
                System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public static boolean CreateDirecroty(String remote, FTPClient initFtpClient) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory), initFtpClient)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existFile(path, initFtpClient)) {
                    if (makeDirectory(subDirectory, initFtpClient)) {
                        changeWorkingDirectory(subDirectory, initFtpClient);
                    } else {
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory, initFtpClient);
                    }
                } else {
                    changeWorkingDirectory(subDirectory, initFtpClient);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    //判断ftp服务器文件是否存在
    public static boolean existFile(String path, FTPClient initFtpClient) throws IOException {

        boolean flag = false;
        FTPFile[] ftpFileArr = initFtpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    public static boolean makeDirectory(String dir, FTPClient initFtpClient) {
        boolean flag = true;
        try {
            flag = initFtpClient.makeDirectory(dir);
            if (flag) {
                System.out.println("创建文件夹" + dir + " 成功！");

            } else {
                System.out.println("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
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
            System.out.println("开始下载文件");
            initFtpClient();
            //切换FTP目录
            initFtpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = initFtpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    initFtpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            initFtpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            System.out.println("下载文件失败");
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

    //读取文件
    public static boolean readFile(String pathname, String filename, String localpath) {
        FTPClient initFtpClient = initFtpClient();
        boolean flag = false;
        OutputStream os = null;
        try {
            System.out.println("开始下载文件");
            initFtpClient();
            //切换FTP目录
            initFtpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = initFtpClient.listFiles();

            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {

                    File localFile = new File("/usr/dlconfig/temp/" + file.getName());
                    os = new FileOutputStream(localFile);
                    initFtpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            initFtpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            System.out.println("下载文件失败");
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
     * 删除文件 *
     *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return
     */
    public static boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        FTPClient initFtpClient = initFtpClient();
        try {
            System.out.println("开始删除文件");
            initFtpClient();
            //切换FTP目录
            initFtpClient.changeWorkingDirectory(pathname);
            initFtpClient.dele(filename);
            initFtpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
            if (initFtpClient.isConnected()) {
                try {
                    initFtpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

        public static void main(String[] args) {
            FTPUtils ftp =new FTPUtils();
            //ftp.uploadFile("ftpFile/data", "123.docx", "E://123.docx");
            ftp.downloadFile("ftpFile/data", "123.docx", "F://");
//            ftp.deleteFile("ftpFile/data", "123.docx");
//            System.out.println("ok");
        	FTPUtils f = new FTPUtils();
        	f.initFtpClient();


        }
}