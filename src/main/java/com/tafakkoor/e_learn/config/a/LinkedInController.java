package com.tafakkoor.e_learn.config.a;

import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LinkedInController {

    private final LinkedIn linkedIn;

    private final OAuth2Operations oauthOperations;
    private final Environment env;

    private final OAuth2ConnectionFactory<LinkedIn> connectionFactory;


    public LinkedInController(LinkedIn linkedIn, OAuth2Operations oauthOperations, Environment env, OAuth2ConnectionFactory<LinkedIn> connectionFactory) {
        this.linkedIn = linkedIn;
        this.oauthOperations = oauthOperations;
        this.env = env;
        this.connectionFactory = connectionFactory;
    }

    @GetMapping(value = "/linkedin")
    public String linkedin(Model model) {
        OAuth2Parameters params = new OAuth2Parameters();
        params.setScope("r_liteprofile r_emailaddress"); // set the desired scope of access
        params.setState("987654321"); // set a CSRF protection state parameter
        params.setRedirectUri("http://localhost:8080/linkedin/callback"); // set the callback URL
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(params);
        return "redirect:" + authorizeUrl;
    }

//    @GetMapping(value = "/linkedin/callback")
//    public String linkedinCallback(@RequestParam("code") String code, Model model) {
//        System.out.println(code);
//        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, "http://localhost:8080/linkedin/callback", null);
//        Connection<LinkedIn> connection = connectionFactory.createConnection(accessGrant);
//        LinkedInProfile profile = connection.getApi().profileOperations().getUserProfile();
//        // Do something with the user's profile data
//        model.addAttribute("profile", profile);
//        System.out.println(profile);
//        return "linkedin_profile";
//    }


    @GetMapping(value = "/linkedin/callback")
    public String linkedinCallback(@RequestParam(value = "code") String code, Model model) {
        System.out.println(code);
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, "http://localhost:8080/linkedin/callback", null);
        System.out.println(accessGrant);
        if (accessGrant == null) {
            System.out.println("Access grant was null");
            return "redirect:/";
        }

        Connection<LinkedIn> connection = connectionFactory.createConnection(accessGrant);
        System.out.println(connection);
        if (connection == null) {
            System.out.println("Connection was null");
            return "redirect:/";
        }

        LinkedInProfile profile = connection.getApi().profileOperations().getUserProfile();
        System.out.println(profile);
        if (profile == null) {
            System.out.println("Profile was null");
            return "redirect:/";
        }

        // Do something with the user's profile data
        model.addAttribute("profile", profile);
        System.out.println(profile);

        return "home";
    }


}
