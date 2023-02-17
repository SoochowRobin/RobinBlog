package com.antra.utils;

import com.antra.domain.entity.Article;
import com.antra.domain.vo.HotArticleVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    // 工具类私有构造方法
    private BeanCopyUtils() {
    }

    /*
    利用范型来做这个事情
     */
    public static <V> V copyBean(Object source, Class<V> clazz) {

        // 创建目标对象
        V result = null;

        try {
            result = clazz.newInstance();
            //实现属性拷贝
            // 用Spring提供的方法 BeanUtils
            BeanUtils.copyProperties(source, result);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 返回结果
        return result;
    }

    /*
    拷贝list， 用stream流 来完成操作
     */
    public static <O,V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());

    }


    public static void main(String[] args) {
        Article a = new Article();
        a.setId(1L);
        a.setContent("hahahha");
        a.setTitle("ssss");

        HotArticleVO hotArticleVO = copyBean(a, HotArticleVO.class);
        System.out.println(hotArticleVO);
    }
}
