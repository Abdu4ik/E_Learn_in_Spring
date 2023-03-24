package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.enums.Roles;
import com.tafakkoor.e_learn.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    private final SessionRegistry sessionRegistry;

    public AdminController(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/admin")
    // check if the user has admin role
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model) {
        List<AuthUser> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("roles", Roles.values());
        return "admin/admin-page";
    }

    @PostMapping("/admin/updateStatus")
    public String updateStatus(@RequestParam("updated_id") Long id, HttpServletRequest request) {
        String sessionId = userService.getSessionId(id, request);
        if (sessionId != null) {
            // invalidate user's session
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(id, true);
            for (SessionInformation session : sessions) {
                session.expireNow();
            }
        }
        userService.updateStatus(id);
        return "redirect:/admin";
    }
    @PostMapping("/admin/updateRole")
    public String updateRole(@RequestParam(value = "updated_id", required = false) Long id, @RequestParam(value = "isAdmin", required = false) String role , @RequestParam(value = "isTeacher", required = false) String role2) {
        System.out.println("role = " + role);
        System.out.println("role2 = " + role2);
        if(role.equals("ADMIN")){
            userService.updateRole(id, role);
        }
        else if(role2.equals("TEACHER")){
            userService.updateRole(id, role2);
        }
        else{
            userService.updateRole(id, "USER");
        }

        return "redirect:/admin";
    }
}
