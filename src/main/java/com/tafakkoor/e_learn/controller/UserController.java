package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.config.security.UserSession;
import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.services.UserService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller

public class UserController {
    private final UserSession userSession;
    private final UserService userService;

    public UserController(UserSession userSession, UserService userService) {
        this.userSession = userSession;
        this.userService = userService;
    }

    @GetMapping("/practise")
    public String practise() {
        return "user/practise";
    }

    @GetMapping("/practise/story")
    public ModelAndView story() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userLevel", userSession.getLevel());
        modelAndView.setViewName("user/story/levelsStory");
        AuthUser user = userService.getUser(userSession.getId());
        List<Levels> levelsList = userService.getLevels(user.getLevel());
        modelAndView.addObject("levels", levelsList);
        return modelAndView;
    }

    @GetMapping("/practise/grammar")
    public ModelAndView grammar() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("levelNotFound", null);

        modelAndView.addObject("userLevel", userSession.getLevel());
        modelAndView.setViewName("user/grammar/levelsGrammar");
        AuthUser user = userService.getUser(userSession.getId());
        List<Levels> levelsList = userService.getLevels(user.getLevel());
        modelAndView.addObject("levels", levelsList);
        return modelAndView;
    }


    @GetMapping("/practise/story/{level}")
    public ModelAndView testStory(@PathVariable String level) {
        ModelAndView modelAndView = new ModelAndView();
        Levels levels = null;
        try {
            levels = Levels.valueOf(level.toUpperCase());
        } catch (Exception e) {
            modelAndView.addObject("flag", true);
            modelAndView.addObject("levelNotFound", "Level not found named %s".formatted(level.toUpperCase()));
            modelAndView.setViewName("user/levelNotFound");
            return modelAndView;
        }
        List<Content> contents = userService.getContentsStories(levels, userSession.getId());
        AuthUser user = userService.getUser(userSession.getId());
        if (!checkLevel(levels, user.getLevel())) {
            modelAndView.addObject("flag", true);
            modelAndView.addObject("levelNotFound", "I think it's too early for you to try this level %s".formatted(level.toUpperCase()));
            modelAndView.setViewName("user/levelNotFound");
            return modelAndView;
        }
        contents.forEach(System.out::println);
        modelAndView.setViewName("user/story/Stories");
        modelAndView.addObject("flag", false);
        modelAndView.addObject("stories", contents);
        return modelAndView;
    }

    @GetMapping("/practise/grammar/{level}")
    public ModelAndView testGrammar(@PathVariable String level) {
        ModelAndView modelAndView = new ModelAndView();
        Levels levels = null;
        try {
            levels = Levels.valueOf(level.toUpperCase());
        } catch (Exception e) {
            modelAndView.addObject("flag", true);
            modelAndView.addObject("levelNotFound", "Level not found named %s".formatted(level.toUpperCase()));
            modelAndView.setViewName("user/levelNotFound");
            return modelAndView;
        }
        List<Content> contents = userService.getContentsGrammar(levels, userSession.getId());
        AuthUser user = userService.getUser(userSession.getId());
        if (!checkLevel(levels, user.getLevel())) {
            modelAndView.addObject("flag", true);
            modelAndView.addObject("levelNotFound", "I think it's too early for you to try this level %s".formatted(level.toUpperCase()));
            modelAndView.setViewName("user/levelNotFound");
            return modelAndView;
        }
        contents.forEach(System.out::println);
        modelAndView.setViewName("user/grammar/Grammars");
        modelAndView.addObject("flag", false);
        modelAndView.addObject("grammars", contents);
        return modelAndView;
    }


    public boolean checkLevel(@NonNull Levels level, @NonNull Levels userLevel) {
        int ordinal = level.ordinal();
        int userLevelOrdinal = userLevel.ordinal();
        return ordinal <= userLevelOrdinal;
    }


    public static void main(String[] args) {
        Levels levels = Levels.valueOf("A1".toUpperCase());
        System.out.println("levels = " + levels);
    }

}
