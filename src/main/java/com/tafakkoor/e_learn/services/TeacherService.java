package com.tafakkoor.e_learn.services;

import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.domain.Questions;
import com.tafakkoor.e_learn.enums.ContentType;
import com.tafakkoor.e_learn.repository.ContentRepository;
import com.tafakkoor.e_learn.repository.OptionRepository;
import com.tafakkoor.e_learn.repository.QuestionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final ContentRepository contentRepository;
    private final OptionRepository optionRepository;
    private final QuestionsRepository questionRepository;

    public TeacherService(ContentRepository contentRepository, OptionRepository optionRepository, QuestionsRepository questionRepository) {
        this.contentRepository = contentRepository;
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    public List<Content> getAllGrammars() {
        return contentRepository.findByContentTypeAndDeletedFalse(ContentType.GRAMMAR);
    }

    public List<Content> getAllStories() {
        return contentRepository.findByContentTypeAndDeletedFalse(ContentType.STORY);
    }

    public Content getContentByIdContentType(Long id, ContentType contentType) {
        Optional<Content> content = contentRepository.findByContentTypeAndIdAndDeletedFalse(contentType, id);
        if (content.isPresent()) {
            return content.get();
        } else return null;
    }


    public boolean deleteGrammarById(Long id, Long userId) {
        Optional<Questions> question = questionRepository.findByContentId(id);
        if (question.isPresent()) {
            optionRepository.deleteByQuestionsId(question.get().getId());
        }
        questionRepository.deleteByContentId(id);
        contentRepository.deleteContentByIdContentType("GRAMMAR", id, userId);
        return true;
    }

    public boolean deleteStoryById(Long id, Long userId) {
        contentRepository.deleteContentByIdContentType("STORY", id, userId);
        return true;
    }


    public Long getGrammarIdByQuestionId(Long questionId) {
        Optional<Questions> byIdIs = questionRepository.findByIdIs(questionId);
        return byIdIs.map(questions -> questions.getContent().getId()).orElse(null);
    }
}
