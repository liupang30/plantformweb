package com.us.example.dao;

import com.us.example.domain.SysUser;
import org.springframework.stereotype.Service;

public interface UserDaoMapper {
    public SysUser findByUserName(String username);
}
