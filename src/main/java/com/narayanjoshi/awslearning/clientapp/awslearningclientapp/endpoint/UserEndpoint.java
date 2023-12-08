package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.endpoint;

import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.dto.User;
import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services.HttpServerService;
import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserEndpoint {

    @Autowired
    HttpServerService httpServerService;

    @GetMapping("/")
    public String redirectTologinPage(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(){
        if(Util.isAuthenticated()){
            return "redirect:/profile";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(){

        return "registration";
    }

    @PostMapping("/signup")
    public String signupPage(@ModelAttribute User userInfo){
        httpServerService.signup(userInfo);
        return "registration";
    }

    @GetMapping("/profile")
    public String profilePage(Model model){
        model.addAttribute("userInfo", new User());
        model.addAllAttributes(httpServerService.profile());
        return "profile";
    }

    @PostMapping("/upload/profile")
    public String profilePage(@RequestParam("file") MultipartFile file) throws IOException {
        httpServerService.uploadProfile(file);
        return "redirect:/profile";
    }

}
