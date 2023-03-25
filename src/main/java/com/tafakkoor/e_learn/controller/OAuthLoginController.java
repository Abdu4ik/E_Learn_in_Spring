package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.repository.AuthUserRepository;
import com.tafakkoor.e_learn.services.OAuthLoginService;
import com.tafakkoor.e_learn.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuthLoginController {
    private final AuthenticationManager authenticationManager;
    private final Environment env;
    private static final String authorizationRequestBaseUri
            = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final UserService userService;
    private final OAuthLoginService oAuthLoginService;

    public OAuthLoginController(ClientRegistrationRepository clientRegistrationRepository /*OAuth2AuthorizedClientService authorizedClientService*/, AuthUserRepository authUserRepository, AuthenticationManager authenticationManager, Environment env, UserService userService, OAuthLoginService oAuthLoginService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authenticationManager = authenticationManager;
        this.env = env;
        this.userService = userService;
        this.oAuthLoginService = oAuthLoginService;
    }

    @GetMapping("/oauth_login")

    public String getLoginPage(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);


        return "/oauth_login";
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken authentication, HttpServletRequest request, @RequestParam(value = "code", required = false) String code) {
        OAuth2AuthorizedClient client;
        String userInfoEndpointUri;
        String tokenValue;
        if (code == null) {
            client = oAuthLoginService.getClient(authentication);
            tokenValue = client.getAccessToken().getTokenValue();
            userInfoEndpointUri = oAuthLoginService.getUserInfoEndpointUri(client);
        } else {
            userInfoEndpointUri = "https://api.linkedin.com/v2/userinfo";
            tokenValue = env.getProperty("spring.security.oauth2.client.registration.linkedin.accessToken");
        }

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            String userInfo = oAuthLoginService.getUserInfo(tokenValue, userInfoEndpointUri);
            if (userInfoEndpointUri.contains("google")) {
                userService.saveGoogleUser(userInfo);
            } else if (userInfoEndpointUri.contains("facebook")) {
                userService.saveFacebookUser(userInfo);
            } else if (userInfoEndpointUri.contains("linkedin")) {
                String username = userService.saveLinkedinUser(userInfo);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, "the.Strongest.Password@Ever9");
                Authentication authenticate = authenticationManager.authenticate(authToken);
                SecurityContext sc = SecurityContextHolder.getContext();
                sc.setAuthentication(authenticate);
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
            }
        }
        return "redirect:/home";
    }
}
