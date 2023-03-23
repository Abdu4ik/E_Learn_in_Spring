package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.config.security.UserSession;
import com.tafakkoor.e_learn.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController {

    private final UserSession userSession;
    private final UserService userService;

    public HomeController(UserSession userSession, UserService userService) {
        this.userSession = userSession;
        this.userService = userService;
    }

    // allow only authenticated users
    @GetMapping("/home")
    public ModelAndView hasAdminRole(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", userService.getUser(principal.getName()));
        modelAndView.setViewName("home/index");
        return modelAndView;
    }
}