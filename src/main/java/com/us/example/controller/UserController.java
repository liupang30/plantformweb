package com.us.example.controller;

import com.us.example.domain.SysUser;
import com.us.example.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api("userController相关api")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",name="username",dataType="String",required=true,value="用户的姓名",defaultValue="admin"),
            @ApiImplicitParam(paramType="query",name="password",dataType="String",required=true,value="用户的密码",defaultValue="admin")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/getUser",method= RequestMethod.GET)
    public SysUser getUser(@RequestHeader("username") String username, @RequestParam("password") String password) {
        return userService.searchUserByUsername(username);
    }
}
