package com.yuki.Socket.Entity;

import com.yuki.Domain.Entity.FileChunk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SocketProgress {
    public String Action;
    public Integer Progress;
    public FileChunk fileChunk;
}
