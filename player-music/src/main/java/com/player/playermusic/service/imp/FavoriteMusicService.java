package com.player.playermusic.service.imp;

import com.player.playermusic.Entity.DouyinEntity;
import com.player.playermusic.Entity.FavoriteMusicEntity;
import com.player.playermusic.Entity.ResultEntity;
import com.player.playermusic.Entity.UserEntity;
import com.player.playermusic.dao.DouyinDao;
import com.player.playermusic.dao.FavoriteMusicDao;
import com.player.playermusic.dao.UserDao;
import com.player.playermusic.service.IFavoriteMusicService;
import com.player.playermusic.utils.HttpUtils;
import com.player.playermusic.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteMusicService implements IFavoriteMusicService {
    @Autowired
    private FavoriteMusicDao favoriteMusicDao;

    @Autowired
    private DouyinDao douyinDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HttpUtils httpUtils;

    /**
     * @param : mid歌曲mid
     * @author: wuwenqiang
     * @methodsName: queryFavorite
     * @description: 查询是否收藏歌曲
     * @param: userId用户id
     * @return: ResultEntity
     * @date: 2020-07-25 8:26
     */
    @Override
    public ResultEntity queryFavorite(String userId, String mid) {
        if (StringUtils.isEmpty(userId)) {
            return ResultUtil.fail("用户为空");
        }
        List<FavoriteMusicEntity> favoriteMusicEntities = favoriteMusicDao.findAllByUserIdAndMid(userId, mid);
        return ResultUtil.success(favoriteMusicEntities);
    }

    /**
     * @param : mid歌曲mid
     * @author: wuwenqiang
     * @methodsName: addFavorite
     * @description: 添加收藏，如果是管理员用户，添加到抖音歌曲表
     * @param: userId用户id
     * @return: ResultEntity
     * @date: 2020-07-25 8:26
     */
    @Transactional
    @Override
    public ResultEntity addFavorite(FavoriteMusicEntity favoriteMusicEntity, String userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResultUtil.fail(null, "请先登录");
        } else {
            List<FavoriteMusicEntity> favoriteMusicEntities = favoriteMusicDao.findAllByUserIdAndMid(userId, favoriteMusicEntity.getMid());
            if (favoriteMusicEntities.size() != 0) {
                return ResultUtil.fail("已经收藏过，请勿重复收藏");
            }
            FavoriteMusicEntity musicEntity = favoriteMusicDao.save(favoriteMusicEntity);

            Optional<UserEntity> userEntityOptional = userDao.findById(userId);

            if (userEntityOptional.isPresent()) {//查询用户是否是管理员，如果是管理员才能插入
                UserEntity userEntity = userEntityOptional.get();

                if (userEntity.getRole() == "admin") {
                    Optional<DouyinEntity> douyinEntityOptional = douyinDao.findById(favoriteMusicEntity.getId());
                    if (douyinEntityOptional.isPresent() == false) {//如果抖音歌曲表里面没有这首歌，才能插入
                        DouyinEntity douyinEntity = new DouyinEntity();
                        String audioName = null;
                        String picName = null;
                        if (!StringUtils.isEmpty(favoriteMusicEntity.getUrl())) {//如果有url地址，下载歌曲
                            audioName = httpUtils.doGetFile(favoriteMusicEntity.getUrl(), "E:\\Node\\music\\public\\audio\\");
                            douyinEntity.setLocalUrl("/audio/" + audioName);
                        } else {//如果没有歌曲地址，设置默认的歌曲地址
                            douyinEntity.setLocalUrl("/audio/" + favoriteMusicEntity.getName() + ".m4a");
                        }
                        if (!StringUtils.isEmpty(favoriteMusicEntity.getImage())) {//如果有图片地址，下载图片
                            picName = httpUtils.doGetFile(favoriteMusicEntity.getImage(), "E:\\Node\\music\\public\\images\\song\\");
                            douyinEntity.setLocalImage("/images/song/" + audioName);
                        } else {//如果没有图片，设置默认的图片的地址
                            douyinEntity.setLocalImage("/images/song/" + favoriteMusicEntity.getName() + ".jpg");
                        }
                        douyinEntity.setPlayMode("local");
                        douyinEntity.setLocalUrl(picName);
                        douyinEntity.setId(favoriteMusicEntity.getId());
                        douyinEntity.setAlbummid(favoriteMusicEntity.getAlbummid());
                        douyinEntity.setDuration(favoriteMusicEntity.getDuration());
                        douyinEntity.setImage(favoriteMusicEntity.getImage());
                        douyinEntity.setMid(favoriteMusicEntity.getMid());
                        douyinEntity.setSinger(favoriteMusicEntity.getSinger());
                        douyinEntity.setUrl(favoriteMusicEntity.getUrl());
                        douyinEntity.setCreateTime(favoriteMusicEntity.getCreateTime());
                        douyinEntity.setTimer(0);
                        douyinEntity.setUpdateTime(favoriteMusicEntity.getUpdateTime());
                        douyinEntity.setKugouUrl(favoriteMusicEntity.getKugouUrl());
                        douyinEntity.setPlayMode(favoriteMusicEntity.getPlayMode());
                        douyinEntity.setOtherUrl(favoriteMusicEntity.getOtherUrl());
                        douyinEntity.setLocalUrl(favoriteMusicEntity.getLocalUrl());
                        douyinEntity.setDisabled("0");
                        douyinEntity.setLyric(favoriteMusicEntity.getLyric());
                        douyinEntity.setLocalImage(favoriteMusicEntity.getLocalImage());
                        douyinDao.save(douyinEntity);
                    }
                }
            }
            if (musicEntity != null) {
                return ResultUtil.success("收藏成功");
            } else {
                return ResultUtil.fail("收藏失败");
            }
        }

    }

    /**
     * @author: wuwenqiang
     * @methodsName: deleteFavorite
     * @description: 取消收藏
     * @param: favoriteMusicEntity歌曲的json
     * @return: ResultEntity
     * @date: 2020-07-30 23:58
     */
    @Override
    public ResultEntity deleteFavorite(FavoriteMusicEntity favoriteMusicEntity) {
        favoriteMusicDao.deleteAllByMidAndUserId(favoriteMusicEntity.getMid(), favoriteMusicEntity.getUserId());
        return ResultUtil.success("删除成功");
    }

    @Override
    public ResultEntity getFavorite(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResultUtil.fail(null, "请先登录");
        }
        return ResultUtil.success(favoriteMusicDao.findAllByUserId(userId));
    }
}
