package com.sds.apitodb.model.animal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sds.apitodb.domain.Animal;

@Mapper
public interface AnimalDAO {
public void insertAll(List<Animal> animalList );
	public void delete();
}
