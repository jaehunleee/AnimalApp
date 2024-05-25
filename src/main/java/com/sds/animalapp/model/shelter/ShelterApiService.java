package com.sds.animalapp.model.shelter;

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
import com.sds.animalapp.domain.Shelter;

@Service
public class ShelterApiService {
	
	@Value("${shelter.api.key}")
	private String key;
	
	/*-----------------------------------
	 영화 리스트 조회
	 -----------------------------------*/
	public List getShelterList(Shelter Shelter) throws IOException {
		
		String shelterCdResponse = null;
		List<Shelter> shelterList= null;
		
		try {
		
			StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1543061/animalShelterSrvc/shelterInfo"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("care_reg_no","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*보호센터등록번호*/
	        urlBuilder.append("&" + URLEncoder.encode("care_nm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*동물보호센터명*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수 (1,000 이하)*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
	        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*xml(기본값) 또는 json*/
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
	        shelterCdResponse = sb.toString();
	        
	        JSONParser parser = new JSONParser();
	        JSONObject json = (JSONObject) parser.parse(shelterCdResponse);
	        json = (JSONObject) json.get("response");
	        json = (JSONObject) json.get("body");
	        JSONArray array = (JSONArray) json.get("items");
	        
	        Gson gson = new Gson();
	        Type shelterArray = new TypeToken<List<Shelter>>() {}.getType();
	        shelterList = gson.fromJson(array.toJSONString(), shelterArray);
        
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        
    
		
		return shelterList;
	}
	
	/*-----------------------------------
	  보호소 1건 조회
	 -----------------------------------*/
	
	
	

}