package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

}
