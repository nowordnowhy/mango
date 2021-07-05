package com.mango.log.service.impl;

import com.mango.log.constant.SecurityConstants;
import com.mango.log.model.UserInfo;
import com.mango.log.service.IUserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认实现方式
 **/
@Service
public class UserInfoServiceImpl implements IUserInfoService {
    @Override
    public UserInfo getUserInfo(HttpServletRequest request) {
        String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
        String userName = request.getHeader(SecurityConstants.USER_NAME_HEADER);
        String customerId = request.getHeader(SecurityConstants.CUSTOMER_HEADER);
        if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(userName) && StringUtils.isEmpty(customerId)) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserName(userName);
        userInfo.setCustomerId(customerId);
        return userInfo;
    }
}
