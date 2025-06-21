package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.CompletedWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

//public interface CompletedWorkRepository extends CrudRepository<CompletedWork, Long> {
    public interface CompletedWorkRepository extends JpaRepository<CompletedWork, Long> {

}
