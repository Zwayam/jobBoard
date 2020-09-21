package com.zwayam.jobboard.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.zwayam.jobboard.models.Job;

@Repository
public interface JobsRepo extends JpaRepository<Job, Integer>{
	public Job findByReferenceNumber(String url);

}
