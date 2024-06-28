package com.sds.apitodb.model.animal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.apitodb.domain.Animal;

@Service
public class AnimalService {
	
	@Autowired
	AnimalDAO animalDAO;
	
	public void insertAll(List<Animal> animalList ){
		animalDAO.insertAll(animalList);
	}
	public void delete() {
		animalDAO.delete();
	}
}
 