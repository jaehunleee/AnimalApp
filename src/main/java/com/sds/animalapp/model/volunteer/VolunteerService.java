package com.sds.animalapp.model.volunteer;

import java.util.List;
import java.util.Map;

import com.sds.animalapp.domain.VolunteerNotice;

public interface VolunteerService {
	public int selectCount(String keyword); //게시물 수 
	public List selectAll(Map map);//모든 게시물 가져오기
	public VolunteerNotice select(int id);
	public void insert(VolunteerNotice volunteerNotice);
	public void update(VolunteerNotice volunteerNotice);
	public void delete(VolunteerNotice volunteerNotice);	
}