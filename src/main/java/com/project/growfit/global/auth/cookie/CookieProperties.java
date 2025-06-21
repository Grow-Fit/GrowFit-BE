package com.project.growfit.global.auth.cookie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom.cookie")
@Getter
@Setter
public class CookieProperties {
    private boolean secure;
    private boolean httpOnly;
    private String sameSite;
    private int maxAge;
    private String domain;
}