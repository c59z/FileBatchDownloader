package com.yuki.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileChunk {
    private String fileName;
    private int chunkIndex;
    private int totalChunks;
    private String data;
}
