package com.zwayam.jobboard.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.zwayam.jobboard.models.JobProvider;

@Repository
public interface JobProviderRepo  extends JpaRepository<JobProvider, Integer>{

}
