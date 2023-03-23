package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
