package com.sds.apitodb.model.sido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.apitodb.domain.Signgu;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignguService {
	@Autowired
	SignguDAO signguDAO;
	
	public void insertAll(List<Signgu> signguList ) {
		signguDAO.insertAll(signguList);
	}
	public void delete() {
		signguDAO.delete();
	}
}
