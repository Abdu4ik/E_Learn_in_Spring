package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    @Query("select q from Questions q where q.content.id = :id")
    List<Questions> findAllByContentId(Long id);
}
