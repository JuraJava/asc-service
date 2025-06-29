package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.CompletedWork;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface CompletedWorkRepository extends JpaRepository<CompletedWork, Long> {
}
