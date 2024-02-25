package com.yuki.Utils;

import com.yuki.Domain.Entity.FileTreeNode;
import com.yuki.Domain.Vo.FileVo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtils {

//    /**
//     * 获取指定目录下的所有文件
//     *
//     * @param rootFolderPath
//     * @return
//     */
//    public static HashMap<String, List<String>> getFileTree(String rootFolderPath) {
//        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
//        getAllFile(rootFolderPath, rootFolderPath, map);
//        return map;
//    }

//    /**
//     * 递归获取指定目录下的所有文件夹与文件(不使用)
//     *
//     * @param rootFolderPath
//     * @param currentFolderPath
//     * @param map
//     */
//    private static void getAllFile(String rootFolderPath, String currentFolderPath, HashMap<String, List<String>> map) {
//        File folder = new File(currentFolderPath);
//        // 如果文件夹存在
//        if (folder.exists() && folder.isDirectory()) {
//            // 获取当前文件夹的所有文件名称
//            File[] files = folder.listFiles();
//            // 如果文件夹不是空的
//            if (files != null) {
//                // 这个list用于存放当前目录的所有文件名称
//                List<String> fileList = new ArrayList<>();
//                // 遍历每一个文件
//                for (File file : files) {
//                    // 如果文件是一个文件夹则会递归
//                    if (file.isDirectory()) {
//                        // 获取这个文件的相对路径 ，原理：获取当前文件的绝对路径，然后减去传过来的上一级目录的路径长度
//                        String relativePath = file.getAbsolutePath().substring(rootFolderPath.length());
//                        // 递归遍历子文件夹
//                        getAllFile(rootFolderPath, file.getAbsolutePath(), map);
//                        // 将这个目录的相对路径存入list中
//                        fileList.add(relativePath);
//                    } else {
//                        fileList.add(file.getName());
//                    }
//                }
//                String relativeFolder = currentFolderPath.substring(rootFolderPath.length());
//                map.put(relativeFolder, fileList);
//            }
//        } else {
//            System.out.println("文件夹不存在或不是一个文件夹");
//        }
//    }



    /**
     * 获取指定路径下的文件夹
     *
     * @param currentPath
     */
    public static List<FileVo> getCurrentFolder(String rootPath, String currentPath) {
        File folder = new File(currentPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            List<FileVo> fileList = new ArrayList<>();


            for (File file : files) {
                if (file.isDirectory()) {
                    String relativePath = FileUtils.filePathReset(file.getAbsolutePath().substring(rootPath.length()));
                    FileVo fileVo = new FileVo();
                    fileVo.setName(file.getName());
                    fileVo.setType("folder");
                    fileVo.setFilePath(relativePath);
                    fileList.add(fileVo);
                } else if (file.isFile()) {
                    String relativePath = FileUtils.filePathReset(file.getAbsolutePath().substring(rootPath.length()));
                    FileVo fileVo = new FileVo();
                    fileVo.setName(file.getName());
                    fileVo.setType("file");
                    fileVo.setFilePath(relativePath);
                    fileList.add(fileVo);
                }
            }
            return fileList;
        } else {
            return null;
        }
    }


    /**
     * 重新格式化url
     * @param filePath
     * @return
     */
    public static String filePathReset(String filePath){
        if (filePath == null) {
            return "/";
        }else if(!filePath.contains("\\")){
            return filePath;
        }

        // 将所有的\替换为/
        String realPath = filePath.replace("\\", "/");

        // 检查字符串长度，如果大于1，则在开头添加/
        if (realPath.length() > 1) {
            realPath = "/" + realPath;
        }

        // 检测相邻的\\，只保留第一个
        realPath = realPath.replaceAll("/+", "/");

        return realPath;
    }


    /**
     * 获取指定文件树
     * @param rootPath
     * @return
     */
    public static List<FileTreeNode> getAllFileTree(String rootPath) {
        File root = new File(rootPath);
        List<FileTreeNode> result = new ArrayList<>();
        traverse(root, result, "");
        return result;
    }

    /**
     * 递归把相应的文件添加的对应的文件夹中
     * @param file
     * @param result
     * @param relativePath
     */
    private static void traverse(File file, List<FileTreeNode> result, String relativePath) {
        FileTreeNode node = new FileTreeNode();
        node.setId(relativePath);
        node.setLabel(file.getName());
        node.setChildren(new ArrayList<>());

        result.add(node);

        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                traverse(subFile, node.getChildren(), relativePath + "/" + subFile.getName());
            }
        }
    }

    /**
     * 计算指定文件的相对路径
     * @param rootPath
     * @param absoluteFilePath
     * @return
     */
    public static String getRelativeFilePath(String rootPath,String absoluteFilePath){
        String relativeFilePath = absoluteFilePath.substring(rootPath.length());
        return relativeFilePath;
    }

}
