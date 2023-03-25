package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Questions;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    @Query("select q from Questions q where q.content.id = :id")
    List<Questions> findAllByContentId(Long id);

    @Transactional
    @Modifying
    void deleteByContentId(Long contentId);


    @Transactional
    Optional<Questions> findByContentId(Long id);

    void deleteById(Long id);

    Optional<Questions> findByIdIs(Long id);
}
