package com.tafakkoor.e_learn.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class OAuthLoginService {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserService userService;

    public OAuthLoginService(OAuth2AuthorizedClientService authorizedClientService, UserService userService) {
        this.authorizedClientService = authorizedClientService;
        this.userService = userService;
    }

    public OAuth2AuthorizedClient getClient(OAuth2AuthenticationToken authentication) {
        return authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
    }

    public String getUserInfoEndpointUri(OAuth2AuthorizedClient client) {
        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();
        if (userInfoEndpointUri.contains("facebook"))
            return "https://graph.facebook.com/me?fields=id,name,email,first_name,last_name,picture.width(250).height(250).as(picture_large)";
        return userInfoEndpointUri;
    }

    public String getUserInfo(String accessToken, String userInfoEndpointUri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity("", headers);
        ResponseEntity<String> response = restTemplate
                .exchange(URI.create(userInfoEndpointUri), HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
