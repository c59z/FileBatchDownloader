package com.yuki.Socket.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketDownloadInfo {
    public String Action;
    public Long totalFileSize;
    public List<String> downloadList;
}
