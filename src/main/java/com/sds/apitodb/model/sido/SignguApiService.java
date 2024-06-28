package com.sds.apitodb.model.sido;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sds.apitodb.domain.Signgu;

@Service
public class SignguApiService {
	
	@Value("${animal.api.key}")
	private String key;
	
    public List call(String sido_orgCd) throws IOException {
    	
    	String signguCdResponse = null;
    	List<Signgu> signguList= null;
        
        try {
        	   StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1543061/abandonmentPublicSrvc/sigungu"); /*URL*/
        	   urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/
               urlBuilder.append("&" + URLEncoder.encode("upr_cd","UTF-8") + "=" + URLEncoder.encode(sido_orgCd, "UTF-8")); /*시군구 상위코드(시도코드) (입력 시 데이터 O, 미입력 시 데이터 X)*/
               urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml(기본값) 또는 json*/
	        URL url = new URL(urlBuilder.toString());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        System.out.println("Response code: " + conn.getResponseCode());
	        BufferedReader rd;
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        } else {
	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = rd.readLine()) != null) {
	            sb.append(line);
	        }
	        rd.close();
	        conn.disconnect();
	        signguCdResponse = sb.toString();
	        
	        JSONParser parser = new JSONParser();
	        JSONObject json = (JSONObject) parser.parse(signguCdResponse);
	        json = (JSONObject) json.get("response");
	        json = (JSONObject) json.get("body");
	        json = (JSONObject) json.get("items");
	        JSONArray array = (JSONArray) json.get("item");
	        
	        Gson gson = new Gson();
	        Type shelterArray = new TypeToken<List<Signgu>>() {}.getType();
	        signguList = gson.fromJson(array.toJSONString(), shelterArray);
	        
        } catch(Exception e) {
			e.printStackTrace();
		}
        
        return signguList;
    }
	
}
