package com.zwayam.jobboard.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zwayam.jobboard.models.Applies;

public interface AppliesRepo  extends JpaRepository<Applies, Integer> {

}
