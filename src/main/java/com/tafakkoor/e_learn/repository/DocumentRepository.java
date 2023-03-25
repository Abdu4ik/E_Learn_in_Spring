package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface DocumentRepository extends JpaRepository<Document,Long> {
    @Transactional
    @Modifying
    @Query( value = "update document set deleted = true,deleted_by = :userId,deleted_at = now() where id = :id",nativeQuery = true )
    int updateToDeleteById( @Param("id") Long id,@Param("userId") Long userId );


}
