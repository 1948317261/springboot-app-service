package com.player.toutiao.service.imp;

import com.alibaba.fastjson.JSON;
import com.player.common.entity.ResultEntity;
import com.player.common.entity.ResultUtil;
import com.player.common.entity.UserEntity;
import com.player.common.utils.Common;
import com.player.common.utils.JwtToken;
import com.player.toutiao.entity.ArticleEntity;
import com.player.toutiao.entity.ChannelEntity;
import com.player.toutiao.mapper.ToutiaoMapper;
import com.player.toutiao.service.IToutiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ToutiaoService implements IToutiaoService {

    @Autowired
    private ToutiaoMapper toutiaoMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public ResultEntity getRequestData(String url,String token,HttpMethod type,Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", token);
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(params),headers);
        ResponseEntity<ResultEntity> responseEntity = restTemplate.exchange(
                url,
                type,
                httpEntity,
                ResultEntity.class
        );
        return  responseEntity.getBody();
    }

    /**
     * @author: wuwenqiang
     * @description: 获取文章列表
     * @date: 2021-12-25 22:29
     */
    @Override
    public ResultEntity getArticleList(int pageNum, int pageSize,  String type, String channelId,String authorId, String keyword,String path) {
        String result = (String) redisTemplate.opsForValue().get(path);
        if(!StringUtils.isEmpty(result)){
            ResultEntity resultEntity= JSON.parseObject(result,ResultEntity.class);
            return resultEntity;
        }else{
            if(pageSize > 100)pageSize = 100;
            int start = pageSize * (pageNum-1);
            ResultEntity resultEntity = ResultUtil.success(toutiaoMapper.getArticleList(start,pageSize, type, channelId, authorId, keyword));
            redisTemplate.opsForValue().set(path, JSON.toJSONString(resultEntity),1, TimeUnit.HOURS);
            return  resultEntity;
        }
    }

    /**
     * @author: wuwenqiang
     * @description: 获取文章详情
     * @date: 2021-5-29 19:22
     */
    @Override
    public ResultEntity getArticleDetail(int id,String token) {
        String key = "articleId_" + id;
        String result = (String) redisTemplate.opsForValue().get(key);
        ResultEntity resultEntity;
        ArticleEntity articleEntity;
        if(!StringUtils.isEmpty(result)){
            resultEntity = JSON.parseObject(result,ResultEntity.class);
            articleEntity =  JSON.parseObject(JSON.toJSONString(resultEntity.getData()),ArticleEntity.class);
        }else{
            articleEntity = toutiaoMapper.getArticleDetail(id);
            resultEntity = ResultUtil.success(articleEntity);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(resultEntity),1, TimeUnit.DAYS);
        }
        String userId = JwtToken.getUserId(token);
        toutiaoMapper.saveArticleRecord(userId,articleEntity.getId());
        toutiaoMapper.deleteArticleRecord(userId);
        return resultEntity;
    }

    /**
     * @author: wuwenqiang
     * @description: 查询用户收藏的频道,如果没有查询到，讲用所有频道里面的数据插入到个人频道列表中
     * @date: 2021-5-29 19:22
     */
    @Override
    public ResultEntity getFavoriteChannels(String token) {
        UserEntity userEntity = JwtToken.parserToken(token, UserEntity.class);
        String key = "findFavoriteChannels_" + userEntity.getUserId();
        String result = (String) redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(result)){
            ResultEntity resultEntity= JSON.parseObject(result,ResultEntity.class);
            return resultEntity;
        }else{
            List<ChannelEntity> favoriteChannels = toutiaoMapper.getFavoriteChannels(userEntity.getUserId());
            if (favoriteChannels.size() == 0){
                List<Integer> status = new ArrayList<>(Arrays.asList(0,1,2));//状态，公开:0,推荐:1,默认:2,非公开:3
                favoriteChannels = toutiaoMapper.getAllChannels(status);
                favoriteChannels.forEach((favoriteChannel)->{
                    favoriteChannel.setUserId(userEntity.getUserId());
                });
                toutiaoMapper.insertFavoriteChannels(favoriteChannels);
            }
            ResultEntity resultEntity  = ResultUtil.success(favoriteChannels);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(resultEntity),1, TimeUnit.DAYS);
            return resultEntity;
        }
    }

    /**
     * @author: wuwenqiang
     * @description: 查询所有品达
     * @date: 2021-5-29 19:22
     */
    @Override
    public ResultEntity getAllChannels(List<Integer>  status) {
        return ResultUtil.success(toutiaoMapper.getAllChannels(status));
    }

    /**
     * @author: wuwenqiang
     * @description: 获取用户信息
     * @date: 2021-5-29 19:22
     */
    @Override
    public ResultEntity getUserData(String token) {
        return getRequestData("http://player-user/service/user/getUserData",token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 获取视频分类
     * @date: 2021-06-28 23:50
     */
    @Override
    public ResultEntity getVideoCategory(String token) {
        return getRequestData("http://player-video/service/video-getway/getFavoriteChannels",token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 获取文章列表
     * @date: 2021-12-25 22:29
     */
    @Override
    public ResultEntity getVideoList(int pageSize,int pageNum,String star,String category,String type,String label,String authorId,String keyword,String token) {
        if(pageSize > 100)pageSize = 100;
        String url = "http://player-video/service/video/getVideoList?pageSize="+pageSize+"&pageNum="+pageNum+"&star="+Common.nullToString(star)+"&category="+Common.nullToString(category)+"&label="+Common.nullToString(label)+"&authorId="+Common.nullToString(authorId)+"&keyword="+Common.nullToString(keyword);
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 获取文章列表
     * @date: 2020-12-25 22:29
     */
    @Override
    public ResultEntity getMovieList(int pageSize,int pageNum,String star,String classify,String category,String type,String label,String keyword,String token) {
        if(pageSize > 100)pageSize = 100;
        String url = "http://player-movie/service/movie/search?pageSize="+pageSize+"&pageNum="+pageNum+"&star="+ Common.nullToString(star)+"&classify="+Common.nullToString(classify)+"&category="+Common.nullToString(category)+"&type="+Common.nullToString(type)+"&label="+Common.nullToString(label)+"&keyword="+Common.nullToString(keyword);
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 获取历史记录
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity getRecordList(String token,String type){
        String url = "";
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.getArticleRecordList(JwtToken.getUserId(token)));
        }else if(type.equals("video")){
            url = "http://player-video/service/video-getway/getVideoRecordList?";
        }else if(type.equals("movie")){
            url = "http://player-movie/service/movie-getway/getPlayRecord";
        }
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity isFavorite(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.isFavorite(userId, id));
        }else if(type.equals("video")){
            url = "http://player-video/service/video-getway/isFavorite?videoId="+id;
        }else if(type.equals("movie")){
            url = "http://player-movie/service/movie-getway/isFavorite?movieId="+id;
        }
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity getFavoriteList(String token,String type,int pageNum,int pageSize){
        String userId = JwtToken.getUserId(token);
        String url = "";
        if(pageSize > 100) pageSize = 100;
        if(type.equals("article")){
            List<ArticleEntity> favorite = toutiaoMapper.getFavoriteList(userId,(pageNum-1)*pageSize, pageSize);
            return ResultUtil.success(favorite.size() > 0);
        }else if(type.equals("video")){
            url = "http://player-video/service/video-getway/getFavoriteList?pageNum="+pageNum+"&pageSize="+pageSize;
        }else if(type.equals("movie")){
            url = "http://player-movie/service/movie-getway/getFavoriteList?pageNum="+pageNum+"&pageSize="+pageSize;
        }
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity insertFavorite(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        Map<String,Object> params = new HashMap<>();
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.insertFavorite(userId,id));
        }else if(type.equals("video")){
            params.put("videoId",id);
            url = "http://player-video/service/video-getway/insertFavorite";
        }else if(type.equals("movie")){
            params.put("movieId",id);
            url = "http://player-movie/service/movie-getway/insertFavorite";
        }
        return getRequestData(url,token,HttpMethod.POST,params);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity deleteFavorite(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        Map<String,Object> params = new HashMap<>();
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.deleteFavorite(userId,id));
        }else if(type.equals("video")){
            params.put("videoId",id);
            url = "http://player-video/service/video-getway/deleteFavorite";
        }else if(type.equals("movie")){
            params.put("movieId",id);
            url = "http://player-movie/service/movie-getway/deleteFavorite";
        }
        return getRequestData(url,token,HttpMethod.DELETE,params);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity isLike(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.isLike(userId, id));
        }else if(type.equals("video")){
            url = "http://player-video/service/video-getway/isLike?videoId="+id;
        }else if(type.equals("movie")){
            url = "http://player-movie/service/movie-getway/isLike?movieId="+id;
        }
        return getRequestData(url,token,HttpMethod.GET,null);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity insertLike(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        Map<String,Object> params = new HashMap<>();
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.insertLike(userId,id));
        }else if(type.equals("video")){
            params.put("videoId",id);
            url = "http://player-video/service/video-getway/insertLike";
        }else if(type.equals("movie")){
            params.put("movieId",id);
            url = "http://player-movie/service/movie-getway/insertLike";
        }
        return getRequestData(url,token,HttpMethod.POST,params);
    }

    /**
     * @author: wuwenqiang
     * @description: 查询是否已经收视频
     * @date: 2021-08-14 22:29
     */
    @Override
    public ResultEntity deleteLike(String token,String type,int id){
        String userId = JwtToken.getUserId(token);
        String url = "";
        Map<String,Object> params = new HashMap<>();
        if(type.equals("article")){
            return ResultUtil.success(toutiaoMapper.deleteLike(userId,id));
        }else if(type.equals("video")){
            params.put("videoId",id);
            url = "http://player-video/service/video-getway/deleteLike";
        }else if(type.equals("movie")){
            params.put("movieId",id);
            url = "http://player-movie/service/movie-getway/deleteLike";
        }
        return getRequestData(url,token,HttpMethod.DELETE,params);
    }
}
