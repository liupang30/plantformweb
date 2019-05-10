package com.us.example.service;

import com.us.example.dao.UserDaoMapper;
import com.us.example.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDaoMapper userDaoMapper;

    public SysUser searchUserByUsername(String username){
        return userDaoMapper.findByUserName(username);
    }
}
