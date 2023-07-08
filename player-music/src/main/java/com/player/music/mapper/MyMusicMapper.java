package com.player.music.mapper;

import com.player.music.Entity.MyMusiPlayListEntity;
import com.player.music.Entity.MyMusicEntity;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.List;

@Repository
public interface MyMusicMapper {
    MyMusicEntity getKeywordMusic();

    List<MyMusicEntity> getMusicClassify();

    List<MyMusicEntity> getMusicListByClassifyId(int classifyId,int start,int pageSize,String userId);

    Long getMusicTotalByClassifyId(int classifyId);

    List<MyMusicEntity> getSingerList(int start,int pageSize);

    Long getSingerTotal();

    List<MyMusiPlayListEntity> getMusiPlayMenu(String userId);
}
