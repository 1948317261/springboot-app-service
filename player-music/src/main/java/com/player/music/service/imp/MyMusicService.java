package com.player.music.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.player.common.entity.ResultEntity;
import com.player.common.entity.ResultUtil;
import com.player.music.mapper.MyMusicMapper;
import com.player.music.service.IMyMusicService;
import com.player.music.utils.RedisUitls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class MyMusicService implements IMyMusicService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MyMusicMapper myMusicMapper;

    /**
     * @author: wuwenqiang
     * @methodsName: getKeywordMusic
     * @description: 获取搜索框中推荐的音乐
     * @return: String
     * @date: 2023-05-25 20:55
     */
    @Override
    public ResultEntity getKeywordMusic(String redisKey) {
        String result = (String) redisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.isEmpty(result)){
            return JSON.parseObject(result,ResultEntity.class);
        }else{
            ResultEntity resultEntity = ResultUtil.success(myMusicMapper.getKeywordMusic());
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONStringWithDateFormat(resultEntity, "yyyy-MM-dd hh:mm:ss", SerializerFeature.WriteDateUseDateFormat),1, TimeUnit.DAYS);
            return resultEntity;
        }
    }

    /**
     * @author: wuwenqiang
     * @methodsName: getKeywordMusic
     * @description: 获取推荐音乐列表
     * @return: String
     * @date: 2023-05-25 21:00
     */
    @Override
    public ResultEntity getMusicClassify(String redisKey) {
        String result = (String) redisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.isEmpty(result)){
            return JSON.parseObject(result,ResultEntity.class);
        }else{
            ResultEntity resultEntity = ResultUtil.success(myMusicMapper.getMusicClassify());
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONStringWithDateFormat(resultEntity, "yyyy-MM-dd hh:mm:ss", SerializerFeature.WriteDateUseDateFormat),1, TimeUnit.DAYS);
            return resultEntity;
        }
    }

    /**
     * @author: wuwenqiang
     * @methodsName: getKeywordMusic
     * @description: 获取推荐音乐列表
     * @return: String
     * @date: 2023-05-25 21:00
     */
    @Override
    public ResultEntity getMusicByClassifyName(String redisKey,String classifyName,int pageNum,int pageSize) {
        redisKey += "?classifyName="+classifyName+"&pageNum=" + pageNum + "&pageSize=" + pageNum;
        String result = (String) redisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.isEmpty(result)){
            return JSON.parseObject(result,ResultEntity.class);
        }else{
            if(pageSize > 100)pageSize = 100;
            int start = (pageNum - 1) * pageSize;
            ResultEntity resultEntity = ResultUtil.success(myMusicMapper.getMusicByClassifyName(classifyName,start,pageSize));
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONStringWithDateFormat(resultEntity, "yyyy-MM-dd hh:mm:ss", SerializerFeature.WriteDateUseDateFormat),1, TimeUnit.DAYS);
            return resultEntity;
        }
    }
}
