package com.player.video.mapper;

import com.player.common.entity.LogEntity;
import com.player.video.entity.VideoEntity;

import java.util.List;
import java.util.Map;

public interface VideoMapper {
    /**
     * @author: wuwenqiang
     * @description: 插入日志
     * @date: 2020-5-29 19:22
     */
    Long log(LogEntity logEntity);

    /**
     * @author: wuwenqiang
     * @description: 查询日志列表
     * @date: 2020-5-29 19:22
     */
    List<VideoEntity> getVideoList(int start,int pageSize,String star,String category,String type,String label,String userId,String keyword);

    List<Map> getVideoCategory();
}
