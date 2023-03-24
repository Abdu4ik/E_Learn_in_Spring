package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthRoleRepository extends JpaRepository<AuthRole, Long> {
    @Query("select a from AuthRole a  where a.code = :role")
    AuthRole findRoleByName(@Param("role") String role);
}
