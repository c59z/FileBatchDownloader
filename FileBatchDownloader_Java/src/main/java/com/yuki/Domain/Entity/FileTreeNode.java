package com.yuki.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTreeNode {
    private String id;
    private String label;
    private List<FileTreeNode> children;
}
