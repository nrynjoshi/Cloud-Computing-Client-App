package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services;

import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.AuthenticateDTO;
import com.narayanjoshi.awslearning.clientapp.awslearningclientapp.dto.User;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class HttpServerService {

    @Autowired
    private HttpUtil httpUtil;
    public AuthenticateDTO loginUser(String username, String password){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        Map<String, Object> response = httpUtil.post("/user/login", requestBody, false);


        AuthenticateDTO authenticateDTO= new AuthenticateDTO();
        authenticateDTO.setToken((String) response.get("token"));
        authenticateDTO.setUsername(username);
        return authenticateDTO;
    }

    public Map<String, Object> profile(){

        Map<String, Object> response = httpUtil.get("/user/profile", true);

        return response;
    }

    public String getProfileViewLink(){
       try{
           Map<String, Object> response = httpUtil.get("/user/view/profile-image", true);
           return (String) response.get("link");
       }catch (Exception x){
           x.printStackTrace();
           return null;
       }
    }

    public void signup(User userInfo){
        Map map = HttpUtil.getObjectMapper().convertValue(userInfo, Map.class);
        Map<String, Object> response = httpUtil.post("/user/registration", map, false);
    }

    public void uploadProfile(MultipartFile multipartFile) throws IOException {
        httpUtil.uploadMultipart("/user/upload/profile", multipartFile, true);
    }

}
