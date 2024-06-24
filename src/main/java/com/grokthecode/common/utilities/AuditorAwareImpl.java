package com.grokthecode.common.utilities;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return Optional.ofNullable(authentication.getName());
        } else {
            return Optional.ofNullable("SYSTEM");
        }
    }
}

