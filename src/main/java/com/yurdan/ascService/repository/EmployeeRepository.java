package com.yurdan.ascService.repository;

import com.yurdan.ascService.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//
//}

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
}

