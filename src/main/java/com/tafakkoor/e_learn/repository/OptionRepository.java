package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface OptionRepository extends JpaRepository<Options, Long> {
    @Query("select o from Options o where o.questions = :id")
    Collection<? extends Options> findAllByQuestionId(Long id);
}
