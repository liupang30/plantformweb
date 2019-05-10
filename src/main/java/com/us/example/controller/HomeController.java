package com.us.example.controller;

import com.us.example.domain.Msg;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by liuyuhao on 17/1/18.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(Model model){
        Msg msg =  new Msg("鼎兴达物联网智慧平台","程序员小哥哥正在加班加点建设中……","看见这些说明你是管理员的一员");
        model.addAttribute("msg", msg);
        return "home";
    }
    @RequestMapping("/index")
    public String index1(Model model){
        return "plantformweb/index";
    }
    @RequestMapping("/index2")
    public String index2(Model model){
        return "plantformweb/index2";
    }
}
