package com.intifix.shared.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiPageResponse < T > {
    private List < T > content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;

    public static < T > ApiPageResponse < T > of(List < T > content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean isLast) {
        return ApiPageResponse. < T > builder()
        .content(content)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .isLast(isLast)
        .build();
    }
}
