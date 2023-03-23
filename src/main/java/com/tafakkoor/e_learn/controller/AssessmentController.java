package com.tafakkoor.e_learn.controller;

import com.google.gson.Gson;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.services.UserService;
import com.tafakkoor.e_learn.utils.Util;
import com.tafakkoor.e_learn.vos.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;

@Controller
public class AssessmentController {
    private final UserService userService;
    private final Gson gson = Util.getInstance().getGson();

    public AssessmentController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/assessment")
    public String getAssessmentPage() {
        return "assessment/LevelTest";
    }

    @PostMapping("/assessment")
    public String postAssessmentPage(HttpServletRequest request, Principal principal) {
        try {
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            ResponseVO response = gson.fromJson(requestBody, ResponseVO.class);
            Levels level = Util.getInstance().determineLevel(response.getScore());
            var user = userService.getUser(principal.getName());
            user.setLevel(level);
            userService.update(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/home";
    }
}
