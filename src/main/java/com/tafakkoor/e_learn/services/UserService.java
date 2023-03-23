package com.tafakkoor.e_learn.services;

import com.google.gson.Gson;
import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.domain.Image;
import com.tafakkoor.e_learn.domain.UserContent;
import com.tafakkoor.e_learn.dto.UserRegisterDTO;
import com.tafakkoor.e_learn.enums.ContentType;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.enums.Progress;
import com.tafakkoor.e_learn.enums.Status;
import com.tafakkoor.e_learn.repository.AuthUserRepository;
import com.tafakkoor.e_learn.repository.ContentRepository;
import com.tafakkoor.e_learn.repository.TokenRepository;
import com.tafakkoor.e_learn.repository.UserContentRepository;
import com.tafakkoor.e_learn.utils.Util;
import com.tafakkoor.e_learn.utils.mail.EmailService;
import com.tafakkoor.e_learn.vos.FacebookVO;
import com.tafakkoor.e_learn.vos.GoogleVO;
import com.tafakkoor.e_learn.vos.LinkedInVO;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final ImageService imageService;
    private final UserContentRepository userContentRepository;
    private final ContentRepository contentRepository;
    private final Gson gson = Util.getInstance().getGson();

    public UserService(AuthUserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, TokenService tokenService, ImageService imageService, UserContentRepository userContentRepository, ContentRepository contentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.imageService = imageService;
        this.userContentRepository = userContentRepository;
        this.contentRepository = contentRepository;
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
                .username(dto.getUsername().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail().toLowerCase())
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

    // Dilshod's code below


    public List<Content> getContentsStories(Levels level, Long id) {
        if (checkUserStatus(id)) {
            return null;
        }
        return contentRepository.findByLevelAndContentTypeAndDeleted(level, ContentType.STORY, false);
    }


    private boolean checkUserStatus(Long id) {
        List<UserContent> userContents = userContentRepository.findByUserIdAndProgress(id, Progress.IN_PROGRESS);
        return userContents.size() > 0;
    }

    public List<Content> getContentsGrammar(Levels level, Long id) {
        if (checkUserStatus(id)) {
            return null;
        }
        return contentRepository.findByLevelAndContentTypeAndDeleted(level, ContentType.GRAMMAR, false);
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

    // Abdullo's code below that


    public boolean userExist(String username) {
        AuthUser user = userRepository.findByUsername(username);
        return user != null;
    }

    public void saveGoogleUser(String userInfo) {
        Image image = null;
        GoogleVO googleUser = gson.fromJson(userInfo, GoogleVO.class);
        String email = googleUser.getEmail();
        String username = googleUser.getSub();
        if (userExist(username)) {
            changeLastLogin(username);
            return;
        }

        if (googleUser.getPicture() != null)
            image = imageService.buildAndSaveImage(googleUser.getPicture(), "google" + googleUser.getSub());

        String password = "the.Strongest.Password@Ever9";
        AuthUser user = AuthUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(googleUser.getGiven_name())
                .lastName(googleUser.getFamily_name())
                .image(image)
                .status(Status.ACTIVE)
                .isOAuthUser(true)
                .lastLogin(LocalDateTime.now(ZoneId.of("Asia/Tashkent")))
                .build();
        userRepository.save(user);
    }

    public void saveFacebookUser(String userInfo) {
        Image image = null;
        FacebookVO facebookUser = gson.fromJson(userInfo, FacebookVO.class);
        String email = facebookUser.getEmail();
        String username = facebookUser.getId();
        if (userExist(username)) {
            changeLastLogin(username);
            return;
        }

        if (facebookUser.getPicture_large() != null)
            image = imageService.buildAndSaveImage(facebookUser.getPicture_large().getData().getUrl(), "facebook" + facebookUser.getId());

        String password = "the.Strongest.Password@Ever9";
        AuthUser user = AuthUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(facebookUser.getFirst_name())
                .lastName(facebookUser.getLast_name())
                .image(image)
                .status(Status.ACTIVE)
                .lastLogin(LocalDateTime.now(ZoneId.of("Asia/Tashkent")))
                .isOAuthUser(true)
                .build();
        userRepository.save(user);
    }

    public String saveLinkedinUser(String userInfo) {
        Image image = null;
        LinkedInVO linkedInUser = gson.fromJson(userInfo, LinkedInVO.class);
        String email = linkedInUser.getEmail();
        String username = linkedInUser.getSub();
        if (userExist(username)) {
            changeLastLogin(username);
            return username;
        }

        if (linkedInUser.getPicture() != null)
            image = imageService.buildAndSaveImage(linkedInUser.getPicture(), "linkedIn" + username);
        String password = "the.Strongest.Password@Ever9";
        AuthUser user = AuthUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(linkedInUser.getGiven_name())
                .lastName(linkedInUser.getFamily_name())
                .image(image)
                .status(Status.ACTIVE)
                .lastLogin(LocalDateTime.now(ZoneId.of("Asia/Tashkent")))
                .isOAuthUser(true)
                .build();
        userRepository.save(user);
        return username;
    }

    public void changeLastLogin(String username) {
        AuthUser user = userRepository.findByUsername(username);
        user.setLastLogin(LocalDateTime.now(ZoneId.of("Asia/Tashkent")));
        userRepository.save(user);
    }

    public AuthUser getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
