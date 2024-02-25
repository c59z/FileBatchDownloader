package com.yuki.Socket.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SocketRequest {
    public String Action;
    public String Message;
    public List<String> downloadList;
}
