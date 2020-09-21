package com.zwayam.jobboard.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zwayam.jobboard.models.JobProviderHistory;

public interface JobProviderHistoryRepo   extends JpaRepository<JobProviderHistory, Integer>{

}
