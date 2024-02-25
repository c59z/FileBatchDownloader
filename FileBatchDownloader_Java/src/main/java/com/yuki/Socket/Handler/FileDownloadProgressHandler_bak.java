package com.yuki.Socket.Handler;

import com.alibaba.fastjson.JSON;
import com.yuki.Domain.Entity.FileChunk;
import com.yuki.Socket.Entity.SocketProgress;
import com.yuki.Socket.Entity.SocketRequest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class FileDownloadProgressHandler_bak extends TextWebSocketHandler {

//    @Value("${user.filepath}")
//    public String rootPath;
    public String rootPath = "D:/code/Project/MinProject/FileBatchDownloader/RootPath";
    private int chunkSize = 1 * 10 * 1024; // 10MB

    public static byte[] readChunk(File file, long start, long end) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            System.out.println("start: "+start+" end: "+end);

            int chunkSize = (int) (end - start);
            byte[] chunkData = new byte[chunkSize];
            randomAccessFile.seek(start);
            randomAccessFile.readFully(chunkData);
            String base64String = Base64.getEncoder().encodeToString(chunkData);
            System.out.println("-------------");
            System.out.println(base64String);
            System.out.println("-------------");
            return chunkData;
        }
    }

    // 处理文件分块发送
    private void sendFileChunks(WebSocketSession session, List<String> downloadList) throws IOException {
        for (String filePath : downloadList) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    int chunkIndex = 0;
                    byte[] buffer = new byte[chunkSize];
                    int bytesRead;

                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    String base64Data = Base64.getEncoder().encodeToString(fileBytes);
                    System.out.println("当前文件的完整Base64:" + base64Data);

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        FileChunk fileChunk = new FileChunk(
//                                file.getName(),
//                                chunkIndex,
//                                calculateTotalChunks(file.length()),

//                                Arrays.copyOfRange(buffer, 0, bytesRead)
                        );

                        System.out.println("当前这个块的base64");
                        String base64String = Base64.getEncoder().encodeToString(buffer);
                        System.out.println("-------------");
                        System.out.println(base64String);
                        System.out.println("-------------");

                        SocketProgress progress = new SocketProgress();
                        progress.setAction("Downloading");
                        progress.setFileChunk(fileChunk);
                        progress.setProgress(100);
                        String downloadChunkProgress = JSON.toJSONString(progress);
                        session.sendMessage(new TextMessage(downloadChunkProgress));

                        chunkIndex++;

//                Resource resource = new FileSystemResource(file);
//                long fileSize = resource.contentLength();
//                int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
//                SocketProgress socketProgress = new SocketProgress();
//                socketProgress.Action = "Downloading";
//
//                try {
//                    for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
//                        long start = chunkIndex * chunkSize;
//                        long end = Math.min((chunkIndex + 1) * chunkSize, fileSize);
//                        byte[] chunkData = readChunk(file, start, end);
//
//                        String relativeFilePath = FileUtils.getRelativeFilePath(rootPath, file.getAbsolutePath());
//
//                        FileChunk fileChunk = new FileChunk();
//                        fileChunk.setFileName(relativeFilePath);
//                        fileChunk.setChunkIndex(chunkIndex);
//                        fileChunk.setTotalChunks(totalChunks);
//                        fileChunk.setData(chunkData);
//
//                        socketProgress.Progress = 100;
//                        socketProgress.fileChunk = fileChunk;
//
//                        String downloadChunkProgress = JSON.toJSONString(socketProgress);
//
//                        // 把文件分段发给前端
//                        session.sendMessage(new TextMessage(downloadChunkProgress));
////                        Thread.sleep(100); // 模拟文件块传输延迟
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    }
                }
            }else{
                System.out.println("找不到文件: "+file.getName());
            }
        }
        SocketProgress progress = new SocketProgress();
        progress.Action = "Finish";
        session.sendMessage(new TextMessage(JSON.toJSONString(progress)));
    }

    private int calculateTotalChunks(long fileSize) {
        return (int) Math.ceil((double) fileSize / chunkSize);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 获取前端socket传来的数据
        String msg = message.getPayload();
        System.out.println("接收到的socket字符串:");
        System.out.println(msg);
        // 反序列化json
        SocketRequest socketReq = JSON.parseObject(msg, SocketRequest.class);
        // 如果Action = Download
        if(socketReq.Action.equals("Download")){
            // 拼接要下载的文件的绝对路径
            List<String> downloadList = socketReq.getDownloadList();
            if (downloadList.size() == 0){
                session.close();
                return;
            }
            downloadList = downloadList.stream()
                    .map(path -> rootPath+path)
                    .collect(Collectors.toList());

            for (String s : downloadList) {
                System.out.println("download -> ["+s+"]");
            }

            /**
             * 开始下载
             */
//            // 创建下载进度实体类
//            SocketProgress progress = new SocketProgress();
//            // 动作为 Downloading
//            progress.Action = "Downloading";
            sendFileChunks(session, downloadList);

            /**
             * 这里开始写如何把文件传给前端
             */
//            // 模拟下载文件
//            for (int i = 0; i <= 100; i += 10) {
//                System.out.println(">>FileDownloadProgressHandler Downloading");
//                progress.Progress = i;
//                Thread.sleep(100); // 模拟文件下载进度更新
//                session.sendMessage(new TextMessage(JSON.toJSONString(progress)));
//            }
//
//            // 下载完毕设置动作为 Finish
//            progress.Action = "Finish";
//            // 发送完成数据
//            session.sendMessage(new TextMessage(JSON.toJSONString(progress)));
        }else{
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
