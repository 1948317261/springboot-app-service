package com.player.toutiao.service;

import com.player.common.entity.ResultEntity;

import java.util.List;

public interface IToutiaoService {
    ResultEntity getArticleList(int pageNum, int pageSize,String type, String channelId, String userId, String keyword, String isTop,String path);

    ResultEntity getArticleDetail(int id,String path);

    ResultEntity getFavoriteChannels(String token);

    ResultEntity getUserData(String token);

    ResultEntity getAllChannels(List<Integer> status);

}
