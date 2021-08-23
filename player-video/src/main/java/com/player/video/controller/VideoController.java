package com.player.video.controller;

import com.player.common.entity.ResultEntity;
import com.player.common.utils.HttpUtils;
import com.player.video.entity.CommentEntity;
import com.player.video.service.IVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("/service")
@Api(value = "抖音查询和记录的接口", description = "查询抖音列表、播放记录接口")
@RestController
public class VideoController {
    @Autowired
    private IVideoService videoService;

    @ApiOperation("获取用户登录信息")
    @GetMapping("/video/getUserData")
    public ResultEntity getUserData(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return videoService.getUserData(token);
    }

    @ApiOperation("获取视频分类信息")
    @GetMapping("/video-getway/getFavoriteChannels")
    public ResultEntity getFavoriteChannels(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return videoService.getFavoriteChannels(token);
    }

    @ApiOperation("获取视频分类信息")
    @GetMapping("/video-getway/getFavoriteList")
    public ResultEntity getFavoriteList(
            @RequestHeader("Authorization") String token,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    ) {
        return videoService.getFavoriteList(token,pageNum,pageSize);
    }

    @ApiOperation("获取视频列表")
    @GetMapping("/video/getVideoList")
    public ResultEntity getVideoList(
            @RequestParam("pageSize") int pageSize,
            @RequestParam("pageNum") int pageNum,
            @RequestParam(required = false, value="star") String star,
            @RequestParam(required = false, value="category") String category,
            @RequestParam(required = false, value="type") String type,
            @RequestParam(required = false, value="label") String label,
            @RequestParam(required = false, value="userId") String userId,
            @RequestParam(required = false, value="keyword") String keyword,
            HttpServletRequest request
    ) {
        String path = HttpUtils.getPath(request);
        return videoService.getVideoList(pageNum,pageSize,star,category,type,label,userId,keyword,path);
    }

    @ApiOperation("查询是否收藏该视频")
    @GetMapping("/video-getway/isFavorite")
    public ResultEntity isFavorite(@RequestHeader("Authorization") String token,@RequestParam("videoId") int videoId) {
        return videoService.isFavorite(token,videoId);
    }

    @ApiOperation("插入收藏")
    @PostMapping("/video-getway/insertFavorite")
    public ResultEntity insertFavorite(@RequestHeader("Authorization") String token, @RequestBody Map<String,Integer> params) {
        return videoService.insertFavorite(token,params.get("videoId"));
    }

    @ApiOperation("删除收藏")
    @DeleteMapping("/video-getway/deleteFavorite")
    public ResultEntity deleteFavorite(
            @RequestHeader("Authorization") String token,
            @RequestParam("videoId") int videoId
    ) {
        return videoService.deleteFavorite(token,videoId);
    }

    @ApiOperation("获取视频分类信息")
    @GetMapping("/video-getway/getVideoRecordList")
    public ResultEntity getVideoRecordList(@RequestHeader("Authorization") String token) {
        return videoService.getVideoRecordList(token);
    }

    @ApiOperation("查询是否点赞过该视频")
    @GetMapping("/video-getway/isLike")
    public ResultEntity isLike(@RequestHeader("Authorization") String token,@RequestParam("videoId") int videoId) {
        return videoService.isLike(token,videoId);
    }

    @ApiOperation("插入点赞")
    @PostMapping("/video-getway/insertLike")
    public ResultEntity insertLike(@RequestHeader("Authorization") String token,@RequestBody Map<String,Integer> params) {
        return videoService.insertLike(token,params.get("videoId"));
    }

    @ApiOperation("删除点赞")
    @DeleteMapping("/video-getway/deleteLike")
    public ResultEntity deleteLike(
            @RequestHeader("Authorization") String token,
            @RequestParam("videoId") int videoId
    ) {
        return videoService.deleteLike(token,videoId);
    }

    @ApiOperation("查询是否点关注过该作者")
    @GetMapping("/video-getway/isFocus")
    public ResultEntity isFocus(@RequestHeader("Authorization") String token,@RequestParam("authorId") String authorId) {
        return videoService.isFocus(token,authorId);
    }

    @ApiOperation("新增关注")
    @PostMapping("/video-getway/insertFocus")
    public ResultEntity insertFocus(@RequestHeader("Authorization") String token,@RequestBody Map<String,String> params) {
        return videoService.insertFocus(token,params.get("authorId"));
    }

    @ApiOperation("取消关注")
    @DeleteMapping("/video-getway/deleteFocus")
    public ResultEntity deleteFocus(
            @RequestHeader("Authorization") String token,
            @RequestParam("videoId") String authorId
    ) {
        return videoService.deleteFocus(token,authorId);
    }

    @ApiOperation("获取总评论数量")
    @GetMapping("/video/getCommentCount")
    public ResultEntity getCommentCount(
            @RequestParam("videoId") int videoId
    ) {
        return videoService.getCommentCount(videoId);
    }

    @ApiOperation("获取一级评论列表")
    @GetMapping("/video/getTopCommentList")
    public ResultEntity getTopCommentList(
            @RequestParam("videoId") int videoId,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize")int pageSize
    ) {
        return videoService.getTopCommentList(videoId,pageNum,pageSize);
    }

    @ApiOperation("新增评论")
    @PostMapping("/video-getway/insertComment")
    public ResultEntity insertComment(
            @RequestHeader("Authorization") String token,
            @RequestBody CommentEntity commentEntity
    ) {
        return videoService.insertComment(token,commentEntity);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/video-getway/deleteComment/{id}")
    public ResultEntity deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") int id
    ) {
        return videoService.deleteComment(id,token);
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/video/getReplyCommentList")
    public ResultEntity getReplyCommentList(
            @RequestParam("topId") int topId,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize")int pageSize
    ) {
        return videoService.getReplyCommentList(topId,pageNum,pageSize);
    }

    @ApiOperation("获取新增的单条评论或者回复")
    @GetMapping("/video/getCommentItem")
    public ResultEntity getCommentItem(
            @RequestParam("id") int id
    ) {
        return videoService.getCommentItem(id);
    }
}
