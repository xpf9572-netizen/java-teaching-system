package com.teach.studentadmin.dto;

import lombok.Data;

@Data
public class PageRequest {
    private int page = 1;
    private int size = 10;
    private String sortBy;
    private String sortOrder = "asc";

    public int getOffset() {
        return (page - 1) * size;
    }
}
