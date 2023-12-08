package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.services;

import ch.qos.logback.core.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class HttpUtil {

    @Value("${server.api.endpoint}")
    private String serverApiEndpoint;

    @Value("${server.access.api_auth}")
    private String serverAccessApiAuth;

    @Value("${temp.file.location}")
    private String tempFileLocation;

    public Map<String,Object> get(String url, boolean isAuthTokenRequired){
        if(url.startsWith("/")){
            url = url.replaceFirst("/","");
        }
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(serverApiEndpoint+url);

            httpget.setHeader("API_AUTH", serverAccessApiAuth);
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            if(isAuthTokenRequired){
                httpget.setHeader("token", Util.getAuthToken());
            }
            HttpClientResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            String responseBody = httpclient.execute(httpget, responseHandler);
            ObjectMapper mapper = getObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
            return (Map<String, Object>) responseMap.get("item");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public Map<String,Object> post(String url, Map<String, Object> requestBodyMap, boolean isAuthTokenRequired){
        if(url.startsWith("/")){
            url = url.replaceFirst("/","");
        }

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Map<String, Object> requestBodyDTOMap= new HashMap<>();
            requestBodyDTOMap.put("data", requestBodyMap);

            HttpPost httpPost = new HttpPost(serverApiEndpoint+url);

            httpPost.setHeader("API_AUTH", serverAccessApiAuth);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            ObjectMapper mapper = getObjectMapper();
            String jsonBody = mapper.writeValueAsString(requestBodyDTOMap);
            final StringEntity requestEntity = new StringEntity(jsonBody);
            httpPost.setEntity(requestEntity);

            if(isAuthTokenRequired){
                httpPost.setHeader("token", Util.getAuthToken());
            }

            HttpClientResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            String responseBody = httpclient.execute(httpPost, responseHandler);
            Map<String, Object> responseMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            return (Map<String, Object>) responseMap.get("item");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void uploadMultipart(String url, MultipartFile multipartFile, boolean isAuthTokenRequired) throws IOException {
        if(url.startsWith("/")){
            url = url.replaceFirst("/","");
        }


        String tempFileSaved = tempFileLocation + UUID.randomUUID().toString();

        Path filepath = Paths.get(tempFileSaved, multipartFile.getOriginalFilename());

        try {
            FileUtils.writeByteArrayToFile(filepath.toFile(), multipartFile.getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(filepath.toFile()));

            headers.set("API_AUTH", serverAccessApiAuth);

            if(isAuthTokenRequired){
                headers.set("token", Util.getAuthToken());
            }

            org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<MultiValueMap<String, Object>>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(serverApiEndpoint+url, requestEntity, String.class);
            System.out.println("Response code: " + response.getStatusCode());


            String responseBody = response.getBody();
            System.out.println("Response : " + responseBody);
        } finally {
            FileUtils.deleteQuietly(filepath.toFile());
        }
    }


}
