package com.sds.apitodb.model.sido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.apitodb.domain.Sido;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SidoService {
	
	@Autowired
	SidoDAO sidoDAO;
	
	public void insertAll(List<Sido> sidoList ) {
		sidoDAO.insertAll(sidoList);
	}
	
	public void delete() {
		sidoDAO.delete();
	}
}
