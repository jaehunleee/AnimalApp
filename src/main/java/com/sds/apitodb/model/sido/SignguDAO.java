package com.sds.apitodb.model.sido;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sds.apitodb.domain.Sido;
import com.sds.apitodb.domain.Signgu;

@Mapper
public interface SignguDAO {
	public void insertAll(List<Signgu> signguList );
	public void delete();
}
