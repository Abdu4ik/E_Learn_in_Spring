package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Options;
import com.tafakkoor.e_learn.domain.Questions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OptionRepository extends JpaRepository<Options, Long> {
    @Query("select o from Options o where o.questions = :id")
    Collection<? extends Options> findAllByQuestionId(Long id);

    @Transactional
    @Modifying
    void deleteByQuestionsId(Long questionId);

    List<Options> findAllByQuestions(Questions questions);
}
