package com.sds.apitodb.model.sido;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sds.apitodb.domain.Sido;

@Mapper
public interface SidoDAO {
	public void insertAll(List<Sido> sidoList );
	public void delete();
}
