package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.dto.UpdateUserDTO;
import com.tafakkoor.e_learn.services.UserService;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/profile")
    public String profile(Principal principal, Model model) {
        String username = principal.getName();
        AuthUser user = userService.getUser(username);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/user/edit")
    public String editProfile(Principal principal, Model model) {
        String username = principal.getName();
        AuthUser user = userService.getUser(username);
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/user/edit")
    public String updateProfile(UpdateUserDTO dto) {
        System.out.println("dto = " + dto);
        userService.updateUser(dto);
        return "redirect:/user/profile";
    }
}
