package com.tafakkoor.e_learn.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    @GetMapping("/error")
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        ModelAndView errorPage = new ModelAndView();
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorPage.setViewName("/errors/400");
            }
            case 401: {
                errorPage.setViewName("/errors/401");
            }
            case 404: {
                errorPage.setViewName("/errors/404");
            }
            case 500: {
                errorPage.setViewName("/errors/500");
            }
            case 429: {
                errorPage.setViewName("/errors/429");
            }
            case 403: {
                errorPage.setViewName("/errors/403");
            }
            case 503: {
                errorPage.setViewName("/errors/503");
            }
            case 504: {
                errorPage.setViewName("/errors/504");
            }
        }
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }
}
