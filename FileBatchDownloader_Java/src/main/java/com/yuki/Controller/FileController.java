package com.yuki.Controller;


import com.yuki.Domain.Dto.RequestDto;
import com.yuki.Domain.Entity.FileTreeNode;
import com.yuki.Domain.ResponseResult;
import com.yuki.Domain.Vo.FileVo;
import com.yuki.Utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/File")
public class FileController {

    @Value("${user.filepath}")
    private String rootPath;

    /**
     * 获取根目录下的所有文件以及子文件夹的内容
     * @return
     */
    @GetMapping("/getFileTree")
    public ResponseResult getFileTree(){
        List<FileTreeNode> fileTree = FileUtils.getAllFileTree(rootPath);
        return ResponseResult.success(fileTree.toArray());
    }

    /**
     * 获取获取指定路径下的文件与文件夹（不进行递归）
     * @return
     */
    @PostMapping("/getCurrentFiles")
    public ResponseResult GetCurrentPathFiles(@RequestBody RequestDto request){
        String currentPath = rootPath+request.path;
//        System.out.println("当前所在的路径: "+currentPath);
        List<FileVo> fileList = FileUtils.getCurrentFolder(rootPath, currentPath);
        return ResponseResult.success(fileList);
    }



}
