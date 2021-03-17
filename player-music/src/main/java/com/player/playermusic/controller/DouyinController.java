package com.player.playermusic.controller;

import com.player.common.entity.ResultEntity;
import com.player.playermusic.service.IDouyinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@Api(value = "抖音查询和记录的接口", description = "查询抖音列表、播放记录接口")
public class DouyinController {

    @Autowired
    private IDouyinService douyinService;

    /**
     * @author: wuwenqiang
     * @methodsName: getDouyinList
     * @description: 查询抖音播放列表
     * @return: ResultEntity
     * @date: 2020-07-25 8:26
     */
    @ApiOperation("查询抖音列表，按更新时间 ")
    @GetMapping("/music/getDouyinList")
    public ResultEntity getDouyinList() {
        return douyinService.getDouyinList();
    }
}
