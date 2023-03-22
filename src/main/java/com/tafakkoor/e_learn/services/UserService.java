package com.tafakkoor.e_learn.services;

import com.tafakkoor.e_learn.domain.*;
import com.tafakkoor.e_learn.dto.UserRegisterDTO;
import com.tafakkoor.e_learn.enums.ContentType;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.enums.Progress;
import com.tafakkoor.e_learn.enums.Status;
import com.tafakkoor.e_learn.repository.*;
import com.tafakkoor.e_learn.utils.Util;
import com.tafakkoor.e_learn.utils.mail.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserContentRepository userContentRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;
    private final VocabularyRepository vocabularyRepository;

    public UserService(AuthUserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, TokenService tokenService, UserContentRepository userContentRepository, ContentRepository contentRepository, CommentRepository commentRepository, VocabularyRepository vocabularyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userContentRepository = userContentRepository;
        this.contentRepository = contentRepository;
        this.commentRepository = commentRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    public List<Levels> getLevels(@NonNull Levels level) {
        List<Levels> levelsList = new ArrayList<>();
        if (level.equals(Levels.DEFAULT)) {
            return levelsList;
        }
        Levels[] levels = Levels.values();
        for (int i = 0; i < levels.length; i++) {
            if (!levels[i].equals(level)) {
                levelsList.add(levels[i]);
            } else {
                levelsList.add(levels[i]);
                break;
            }
        }
        return levelsList;
    }

    public AuthUser getUser(Long id) {
        return userRepository.findById(id);
    }

    public void saveUserAndSendEmail(UserRegisterDTO dto) {
        AuthUser user = AuthUser.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();
        userRepository.save(user);
        sendActivationEmail(user);
    }

    public void sendActivationEmail(AuthUser authUser) {
        Util util = Util.getInstance();
        String token = tokenService.generateToken();  // TODO: 3/12/23 encrypt token
        String email = authUser.getEmail();
        String body = util.generateBody(authUser.getUsername(), token);
        tokenService.save(util.buildToken(token, authUser));
        CompletableFuture.runAsync(() -> EmailService.getInstance().sendEmail(email, body, "Activate Email"));
    }


    public List<Content> getContentsStories(Levels level, Long id) throws RuntimeException {
        UserContent userContent = checkUserStatus(id);
        if (userContent != null) {
            Content content = userContent.getContent();
            throw new RuntimeException("You have a content in progress named \"%s\". Please complete the content first. id=%d".formatted(content.getTitle(), content.getId()));
        }
        return contentRepository.findByLevelAndContentTypeAndDeleted(level, ContentType.STORY, false);
    }

    public ModelAndView getInProgressPage(ModelAndView modelAndView, Exception e) {
        String eMessage = e.getMessage();
        Long id = Long.parseLong(eMessage.substring(eMessage.indexOf("id") + 3));
        modelAndView.addObject("inProgress", eMessage.substring(0, eMessage.indexOf("id")));
        modelAndView.addObject("content", getContent(id).get());
        modelAndView.setViewName("user/inProgress");
        return modelAndView;
    }


    public UserContent checkUserStatus(Long id) {
        return userContentRepository.findByUserIdAndProgressOrProgress(id, Progress.IN_PROGRESS, Progress.TAKE_TEST);
    }

    public List<Content> getContentsGrammar(Levels level, Long id) {
        UserContent userContent = checkUserStatus(id);
        if (userContent != null) {
            Content content = userContent.getContent();
            throw new RuntimeException("You have a content in progress named \"%s\". Please complete the content first. id=%d".formatted(content.getTitle(), content.getId()));
        }
        return contentRepository.findByLevelAndContentTypeAndDeleted(level, ContentType.GRAMMAR, false);
    }

    public Optional<Content> getContent(Long id) {
        return contentRepository.findById(id);
    }

    public Optional<Content> getContent(String storyId, Long userId) {
        Optional<Content> content = contentRepository.findById(Long.valueOf(storyId));
        return Optional.empty();
    }

    public List<AuthUser> getAllUsers() {
        return userRepository.findByDeleted(false);
    }

    public void updateStatus(Long id) {
        AuthUser byId = userRepository.findById(id);
        boolean blocked = byId.getStatus().equals(Status.BLOCKED);
        if (blocked) {
            byId.setStatus(Status.ACTIVE);
        } else {
            byId.setStatus(Status.BLOCKED);
        }
        userRepository.save(byId);
    }


    public Content getStoryById(Long id) {
        return contentRepository.findByIdAndContentType(id, ContentType.STORY);
    }

    public List<Comment> getComments(Long id) {
        return Objects.requireNonNullElse(commentRepository.findAllByContentIdAndDeleted(id, false), new ArrayList<>());
    }

    public void addComment(Comment comment) {
        commentRepository.saveComment(comment.getComment(), String.valueOf(comment.getCommentType()), comment.getUserId().getId(), comment.getContentId(), comment.getParentId());
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteCommentById(Long id) {
        commentRepository.setAsDelete(id);
    }

    public void updateComment(Comment comment1) {
        commentRepository.updateComment(comment1.getComment(), comment1.getId());
    }

    public void saveUserContent(UserContent userContent) {
        userContentRepository.save(userContent);
    }

    public List<Vocabulary> mapRequestToVocabularyList(HttpServletRequest request, Content content, AuthUser authUser) {
        List<Vocabulary> vocabularyList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            vocabularyList.add(mapVocabulary(request, i, authUser, content));
        }
        String[] uzbekWords = request.getParameterValues("uzbekWord");
        String[] englishWords = request.getParameterValues("englishWord");
        String[] definitions = request.getParameterValues("definition");
        if (uzbekWords == null || englishWords == null ||
                uzbekWords.length == 0 ||
                englishWords.length == 0 ||
                uzbekWords.length != englishWords.length
        ) {
            throw new RuntimeException("Please fill all fields");
        }
        for (int i = 0; i < uzbekWords.length; i++) {
            Vocabulary vocabulary = Vocabulary.builder()
                    .story(content)
                    .authUser(authUser)
                    .word(englishWords[i])
                    .translation(uzbekWords[i])
                    .definition(Objects.requireNonNullElse(definitions[i], ""))
                    .build();
            vocabularyList.add(vocabulary);
        }
        return vocabularyList;
    }

    private Vocabulary mapVocabulary(HttpServletRequest request, int i, AuthUser authUser, Content content) {
        return Vocabulary.builder()
                .word(request.getParameter("word" + i))
                .translation(request.getParameter("translation" + i))
                .definition(request.getParameter("definition" + i))
                .story(content)
                .authUser(authUser)
                .build();
    }

    public void addVocabularyList(List<Vocabulary> vocabularies) {
        vocabularyRepository.saveAll(vocabularies);
    }
}