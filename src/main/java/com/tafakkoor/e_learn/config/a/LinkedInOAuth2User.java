package com.tafakkoor.e_learn.config.a;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class LinkedInOAuth2User implements OAuth2User {

    private final OAuth2User delegate;

    public LinkedInOAuth2User(OAuth2User delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = delegate.getAttributes();
        attributes.entrySet().forEach(System.out::println);
        // Map the LinkedIn profile attributes to your user object
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
