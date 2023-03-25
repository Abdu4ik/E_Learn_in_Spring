package com.tafakkoor.e_learn.services;

import com.tafakkoor.e_learn.config.security.UserSession;
import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.domain.Document;
import com.tafakkoor.e_learn.domain.Options;
import com.tafakkoor.e_learn.domain.Questions;
import com.tafakkoor.e_learn.dto.ContentUpdateDTO;
import com.tafakkoor.e_learn.dto.DocumentCreateDTO;
import com.tafakkoor.e_learn.dto.GrammarCreateDTO;
import com.tafakkoor.e_learn.dto.StoryCreateDTO;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.repository.ContentRepository;
import com.tafakkoor.e_learn.repository.DocumentRepository;
import com.tafakkoor.e_learn.repository.OptionRepository;
import com.tafakkoor.e_learn.repository.QuestionsRepository;
import com.tafakkoor.e_learn.utils.helpers.QuestionHelper;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private final DocumentService documentService;

    private final UserSession userSession;

    private final ContentRepository contentRepository;
    private final DocumentRepository documentRepository;
    private final QuestionsRepository questionRepository;
    private final OptionRepository optionRepository;

    public ContentService(DocumentService documentService, UserSession userSession,
                          ContentRepository contentRepository,
                          DocumentRepository documentRepository,
                          QuestionsRepository questionRepository,
                          OptionRepository optionRepository) {
        this.documentService = documentService;
        this.userSession = userSession;
        this.contentRepository = contentRepository;
        this.documentRepository = documentRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }


    public Long storyCreate(StoryCreateDTO dto) {
        Document document = documentService.createAndGet(new DocumentCreateDTO(dto.getFile()));
        Levels level = Arrays.stream(Levels.values()).filter(levels -> levels.name().equals(dto.getLevel())).findFirst().get();
        Content content = Content.builder()
                .contentType(dto.getContentType())
                .level(level)
                .title(dto.getTitle())
                .document(document)
                .author(dto.getAuthor())
                .score(dto.getScore())
                .build();

        contentRepository.save(content);
        return content.getId();
    }

    public Long grammarCreate(GrammarCreateDTO dto) {
        Document document = documentService.createAndGet(new DocumentCreateDTO(dto.getFile()));
        Levels level = Arrays.stream(Levels.values()).filter(levels -> levels.name().equalsIgnoreCase(dto.getLevel().toUpperCase())).findAny().get();
        Content content = Content.builder()
                .contentType(dto.getContentType())
                .level(level)
                .title(dto.getTitle())
                .document(document)
                .author(dto.getAuthor())
                .score(dto.getScore())
                .build();

        contentRepository.save(content);

        Questions questions = Questions.builder()
                .content(content)
                .title(dto.getQuestion())
                .build();

        questionRepository.save(questions);

        List<String> options = new ArrayList<>();
        options.add(dto.getOption1());
        options.add(dto.getOption2());
        options.add(dto.getOption3());
        options.add(dto.getOption4());

        List<Options> optionsList = getOptions(options, Integer.valueOf(dto.getCorrectAnswer()), questions);


        optionRepository.saveAll(optionsList);

        return content.getId();
    }

    public Long update(ContentUpdateDTO dto, Long id, Long docId) {
        documentRepository.updateToDeleteById(docId, userSession.getUser().getId());
        Document document = documentService.createAndGet(new DocumentCreateDTO(dto.getFile()));
        Levels level = Arrays.stream(Levels.values()).filter(levels -> levels.name().equals(dto.getLevel())).findFirst().get();
        contentRepository.updateContentById(dto.getTitle(), dto.getAuthor(), dto.getScore(), level, dto.getContentType(), document, userSession.getUser(), id);
        return id;
    }


    public Content findById(Long contentId) {
        return contentRepository.findById(contentId).get();
    }


    private List<Options> getOptions(List<String> options, int correctAnswer, Questions questions) {
        List<Options> optionsList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String value = options.get(i);
            int index = i + 1;
            Options options1 = Options.builder()
                    .value(value)
                    .questions(questions)
                    .isCorrect(index == correctAnswer)
                    .build();
            optionsList.add(options1);
        }
        return optionsList;
    }

    public List<QuestionHelper> findAllQuestionByGrammar(Long id) {
        List<QuestionHelper> helpers = new ArrayList<>();
        List<Questions> questions = questionRepository.findAllByContentId(id);
        for (Questions question : questions) {
            List<Options> options = optionRepository.findAllByQuestions(question);
            QuestionHelper questionHelper = QuestionHelper.builder()
                    .questionId(question.getId())
                    .title(question.getTitle())
                    .option1(options.get(0).getValue())
                    .option2(options.get(1).getValue())
                    .option3(options.get(2).getValue())
                    .option4(options.get(3).getValue())
                    .correctAnswer(getCorrectAnswer(options))
                    .build();
            helpers.add(questionHelper);
        }
        return helpers;
    }

    private String getCorrectAnswer(List<Options> options) {
        for (Options option : options) {
            if (option.isCorrect()) {
                return option.getValue();
            }
        }
        return null;
    }


    public boolean saveQuestion(@NonNull QuestionHelper questionHelper, Long grammarId) {
        Optional<Content> optionalContent = contentRepository.findById(grammarId);
        Content grammar = null;
        if (optionalContent.isPresent()) {
            grammar = optionalContent.get();
            Questions questions = Questions.builder()
                    .content(grammar)
                    .title(questionHelper.getTitle())
                    .build();
            questionRepository.save(questions);
            optionRepository.saveAll(createOptionsByQuestion(questions, questionHelper));
            return true;
        }
        return false;

    }


    List<Options> createOptionsByQuestion(Questions questions, QuestionHelper helper) {
        List<Options> options = new ArrayList<>();
        List<String> valueList = getOptionValueList(helper);
        for (String value : valueList) {
            options.add(Options.builder()
                    .questions(questions)
                    .value(value)
                    .isCorrect(value.equals(helper.getCorrectAnswer()))
                    .build());
        }
        return options;
    }


    public boolean deleteQuestion(@NonNull Long id){
        optionRepository.deleteByQuestionsId(id);
        questionRepository.deleteById(id);
        return true;
    }

    private List<String> getOptionValueList(QuestionHelper helper){
        return List.of(helper.getOption1(),helper.getOption2(),helper.getOption3(),helper.getOption4());
    }


}
