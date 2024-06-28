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
import com.sds.apitodb.domain.Sido;

@Service
public class SidoApiService {
	
	@Value("${animal.api.key}")
	private String key;
	
    public List call() throws IOException {
    	
    	String sidoCdResponse = null;
    	List<Sido> sidoList= null;
        
        try {
        	StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1543061/abandonmentPublicSrvc/sido"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수(1,000 이하)*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
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
	        sidoCdResponse = sb.toString();
	        
	        JSONParser parser = new JSONParser();
	        JSONObject json = (JSONObject) parser.parse(sidoCdResponse);
	        json = (JSONObject) json.get("response");
	        json = (JSONObject) json.get("body");
	        json = (JSONObject) json.get("items");
	        JSONArray array = (JSONArray) json.get("item");
	        
	        Gson gson = new Gson();
	        Type shelterArray = new TypeToken<List<Sido>>() {}.getType();
	        sidoList = gson.fromJson(array.toJSONString(), shelterArray);
	        
        } catch(Exception e) {
			e.printStackTrace();
		}
        
        return sidoList;
    }
	
}
