package com.sds.apitodb.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sds.apitodb.domain.Animal;
import com.sds.apitodb.domain.Sido;
import com.sds.apitodb.domain.Signgu;
import com.sds.apitodb.model.animal.AnimalApiService;
import com.sds.apitodb.model.animal.AnimalService;
import com.sds.apitodb.model.sido.SidoApiService;
import com.sds.apitodb.model.sido.SidoService;
import com.sds.apitodb.model.sido.SignguApiService;
import com.sds.apitodb.model.sido.SignguService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {

	@Autowired
	SidoApiService sidoApiService;

	@Autowired
	SignguApiService signguApiService;

	@Autowired
	SidoService sidoService;

	@Autowired
	SignguService signguService;
	
	@Autowired
	AnimalService animalService;
	
	@Autowired
	AnimalApiService animalApiService;

	@GetMapping("/")
	public String getMain(Model model) {
		return "main/index";
	}
	@GetMapping("/sidoapi")
	public String getSido(Model model) {
		return "main/sidoapi";
	}
	@GetMapping("/animalapi")
	public String getAnimal(Model model) {
		return "main/animalapi";
	}
	 
	//시도 불러오기
	@GetMapping("/call/sido")
	public String getCallSido(Model model) throws IOException {
		
		signguService.delete();
		sidoService.delete();
		
		List<Sido> sidoList = sidoApiService.call();

		sidoService.insertAll(sidoList);
		
		for (Sido sido : sidoList) {
			log.info("시도코드=="+sido.getOrgCd());
			List<Signgu> signguList = signguApiService.call(sido.getOrgCd());
			if(signguList != null) {
				log.info("시군구코드=="+signguList.get(0).getOrgCd());
				signguService.insertAll(signguList);
			}
		}

		return "main/index";
	}
	
	//유기동물 불러오기
	@GetMapping("/call/animal")
	public String getCallAnimal(Model model) throws IOException {
		
		animalService.delete();
		
		List<Animal> animalList = animalApiService.call();
		
		System.out.println(animalList.get(0).getAge()); 
		
		animalService.insertAll(animalList);
		
		return "main/index";
	}
}
