package com.antra.service.impl;

import com.antra.constant.SystemConstants;
import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Comment;
import com.antra.domain.vo.CommentVO;
import com.antra.domain.vo.PageVO;
import com.antra.enums.AppHttpCodeEnum;
import com.antra.exception.SystemException;
import com.antra.mapper.CommentMapper;
import com.antra.service.CommentService;
import com.antra.service.UserService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 13:36:37
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private UserService userService;

    @Autowired
    public CommentServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {

        // 查询对应文章的根评论

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        // 加上boolean 判断，如果是文章评论就执行，不是就不执行，因为link comment， 传入的值是null
        // 对articleId 进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId, articleId);

        // 根评论rootId = -1
        // TODO: 最好定义常量使用
        queryWrapper.eq(Comment::getRootId, -1);

        queryWrapper.eq(Comment::getType, commentType);

        // 分页查询
        Page<Comment> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);

        List<CommentVO> commentVOs = toCommentVOList(page.getRecords());

        // 查询所有根评论对应的子评论集合，并赋值给对应的属性
        for (CommentVO commentVO : commentVOs) {
            // 查询对应的子评论
            List<CommentVO> children = getChildren(commentVO.getId());
            // 赋值
            commentVO.setChildren(children);
        }

        return ResponseResult.okResult(new PageVO(commentVOs,page.getTotal()));
    }




    // 传入根评论id, 返回此根评论所有子评论的list
    private List<CommentVO> getChildren(Long id) {
        //
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        // 排序
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        List<CommentVO> commentVOS = toCommentVOList(comments);
        return commentVOS;
    }


    private List<CommentVO> toCommentVOList(List<Comment> list){
        List<CommentVO> commentVOS = BeanCopyUtils.copyBeanList(list, CommentVO.class);
        //遍历VO 集合

        for(CommentVO commentVO: commentVOS){
            String nickName = userService.getById(commentVO.getCreateBy()).getNickName();
            commentVO.setUsername(nickName);

            // 通过toCommentUserId查询用户的昵称并赋值
            // 如果toCommentUserId不为-1才进行查询
            if(commentVO.getToCommentId()!=-1L){
                String toCommentUserName = userService.getById(commentVO.getToCommentUserId()).getNickName();
                commentVO.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVOS;
    }


    @Override
    public ResponseResult addComment(Comment comment) {
        // 评论内容不能为空，或者敏感词的处理
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();
    }

}

