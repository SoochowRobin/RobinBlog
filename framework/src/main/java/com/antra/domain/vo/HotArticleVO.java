package com.antra.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
这个 VO就是写跟前端商量好的，想返回给前端的字段，类似于DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotArticleVO {
    private Long id;
    private String title;
    private Long viewCount;
}
