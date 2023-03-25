package com.tafakkoor.e_learn.repository;

import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.domain.Document;
import com.tafakkoor.e_learn.enums.ContentType;
import com.tafakkoor.e_learn.enums.Levels;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByLevelAndContentTypeAndDeleted(Levels level, ContentType contentType, boolean deleted);

    Content findByIdAndContentType(Long id, ContentType story);

    @Transactional
    @Modifying
    @Query(value = "update content set deleted = true, deleted_at = now(), deleted_by = :userId where contenttype = :contentType and id = :id", nativeQuery = true)
    void deleteContentByIdContentType(@Param("contentType") String contentType, @Param("id") Long id, @Param("userId") Long userId);


    @Query( "select c from Content c where c.contentType = ?1 and c.id = ?2 and c.deleted = false" )
    Optional<Content> findByContentTypeAndIdAndDeletedFalse(ContentType contentType, Long id );

    @Query( "select c from Content c where c.contentType = ?1 and c.deleted = false" )
    List<Content> findByContentTypeAndDeletedFalse(ContentType contentType);

    @Modifying
    @Transactional
    @Query("UPDATE Content c SET c.title = :title, c.author = :author, c.score = :score, c.level = :level, c.document = :documentId, c.updatedBy = :userId, c.updatedAt = CURRENT_TIMESTAMP WHERE c.contentType = :contentType AND c.id = :contentId")
    void updateContentById(@Param("title") String title, @Param("author") String author, @Param("score") int score, @Param("level") Levels level, @Param("contentType") ContentType contenttype, @Param("documentId") Document documentId, @Param("userId") AuthUser userId, @Param("contentId") Long contentId);


}
