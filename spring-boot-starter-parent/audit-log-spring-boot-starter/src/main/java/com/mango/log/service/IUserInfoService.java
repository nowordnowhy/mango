package com.mango.log.service;

import com.mango.log.model.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 *
 **/
public interface IUserInfoService {
    UserInfo getUserInfo(HttpServletRequest request);
}
