package com.antra.service.impl;

import com.antra.constant.SystemConstants;
import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Link;
import com.antra.domain.vo.LinkVO;
import com.antra.mapper.LinkMapper;
import com.antra.service.LinkService;
import com.antra.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-02-11 15:27:02
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        // 查询所有审核通过的

        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        // 转换成VO
        List<LinkVO> linkVOs = BeanCopyUtils.copyBeanList(links, LinkVO.class);

        return ResponseResult.okResult(linkVOs);
    }
}

