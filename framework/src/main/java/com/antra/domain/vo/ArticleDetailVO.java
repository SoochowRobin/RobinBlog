package com.antra.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailVO {

    private Long id;

    private String categoryName;

    private Long categoryId;

    private String content;

    private String title;

    private String summary;

    private Long viewCount;

    private Long likeCount;

    private String thumbnail;

    private Date createTime;
}
