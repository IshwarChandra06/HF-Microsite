package com.eikona.tech.repository;

import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.ActionDetails;

@Repository
public interface ActionDetailsRepository extends DataTablesRepository<ActionDetails, Long>{
	
	@Query("select ad from com.eikona.tech.entity.ActionDetails as ad where ad.action.id=:id")
	List<ActionDetails> findByActionIdCustom(Long id);

	List<ActionDetails> findByStatus(String string);
	

}
