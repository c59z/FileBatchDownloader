package com.yuki.Socket.Handler;

import com.alibaba.fastjson.JSON;
import com.yuki.Domain.Entity.FileChunk;
import com.yuki.Socket.Entity.SocketDownloadInfo;
import com.yuki.Socket.Entity.SocketProgress;
import com.yuki.Socket.Entity.SocketRequest;
import com.yuki.Utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CountDownLatch;

public class FileDownloadProgressHandler extends TextWebSocketHandler {

//    @Value("${user.filepath}")
//    public String rootPath;
    public String rootPath = "D:/code/Project/MinProject/FileBatchDownloader/RootPath";
    private int chunkSize = 10 * 1024 * 1024; // 10MB
    private int nextChunk = 0;

    private long totalFileSize = 0L;

    // 处理文件分块发送
    private void sendFileChunks(WebSocketSession session, List<String> downloadList) throws IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        for (String filePath : downloadList) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    String base64Data = Base64.getEncoder().encodeToString(fileBytes);
//                    System.out.println("完整的Base64：" + base64Data);
                    // 单个文件大小
                    int fileSize = base64Data.length();

                    int totalChunks = (int) Math.ceil((double) base64Data.length() / chunkSize);
                    String relativeFilePath = FileUtils.getRelativeFilePath(rootPath, file.getAbsolutePath());
                    // 模拟通过WebSocket发送每个块
                    for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {



                        int startIndex = chunkIndex * chunkSize;
                        int endIndex = Math.min((chunkIndex + 1) * chunkSize, base64Data.length());
                        String chunk = base64Data.substring(startIndex, endIndex);

                        float downloadedProgress = ((float) endIndex / fileSize) * 100;
//                        System.out.println(endIndex +" / "+fileSize+" = "+downloadedProgress);


                        FileChunk fileChunk = new FileChunk(
                                relativeFilePath,
                                chunkIndex,
                                totalChunks,
                                chunk
                        );


                        SocketProgress progress = new SocketProgress();
                        progress.setAction("Downloading");
                        progress.setProgress((int)downloadedProgress); // 设置进度
                        progress.setFileChunk(fileChunk);
                        String downloadChunkProgress = JSON.toJSONString(progress);
                        session.sendMessage(new TextMessage(downloadChunkProgress));


                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }else{
                if(!file.isDirectory()){
                    System.out.println("找不到文件: "+file.getName());
                }
            }
        }
        SocketProgress progress = new SocketProgress();
        progress.Action = "Finish";
        session.sendMessage(new TextMessage(JSON.toJSONString(progress)));
    }


    /**
     * 判断传入的路径是否为文件夹
     * @param path
     * @return
     */
    private static boolean isDirectory(String path) {
        Path filePath = Paths.get(path);
        return Files.isDirectory(filePath);
    }

    private static Long getTotalFileSize(List<String> list){
        long temp = 0L;
        for (String s : list) {
            File file = new File(s);
            temp += file.length();
        }
        return temp;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 获取前端socket传来的数据
        String msg = message.getPayload();
        System.out.println("接收到的socket字符串:");
        System.out.println(msg);
        System.out.println("----------------");
        // 反序列化json
        SocketRequest socketReq = JSON.parseObject(msg, SocketRequest.class);
        // 如果Action = Download
        if(socketReq.Action.equals("Download") && socketReq.Message.equals("start download")){
            // 拼接要下载的文件的绝对路径
            List<String> downloadList = socketReq.getDownloadList();
            if (downloadList.size() == 0){
                session.close();
                return;
            }
            downloadList = downloadList.stream()
                    .map(path -> rootPath+path)
                    .filter(path -> !isDirectory(path))
                    .collect(Collectors.toList());

//            for (String s : downloadList) {
//                System.out.println("download -> ["+s+"]");
//            }

            totalFileSize = getTotalFileSize(downloadList);

            List<String> reDoanloadList = new ArrayList<>();
            for (String s : downloadList) {
                reDoanloadList.add(FileUtils.getRelativeFilePath(rootPath,s));
            }

            SocketDownloadInfo downloadInfo = new SocketDownloadInfo();
            downloadInfo.setDownloadList(reDoanloadList);
            downloadInfo.setAction("DownloadInfo");
            downloadInfo.setTotalFileSize(totalFileSize);
            session.sendMessage(new TextMessage(JSON.toJSONString(downloadInfo)));

            // 开始分块下载
            sendFileChunks(session, downloadList);


        }else if(socketReq.Action.equals("Download") && socketReq.Message.equals("NextChunk")){
            nextChunk++;
            // 非下载请求
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Socket 已建立");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Socket 已关闭");
    }

}
