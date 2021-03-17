package com.player.playermusic.service;

import com.player.common.entity.ResultEntity;
import com.player.playermusic.Entity.UserEntity;

import javax.servlet.http.HttpServletResponse;

public interface IUserService {
    ResultEntity findUserByUserId(String userId);

    ResultEntity getUserData(HttpServletResponse response, String userId);

    ResultEntity login(HttpServletResponse response, String userId, String password);

    ResultEntity logout(HttpServletResponse response);

    ResultEntity register(UserEntity userEntity);

    ResultEntity findUser(String userId);
}
