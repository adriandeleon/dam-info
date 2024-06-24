package com.grokthecode.configs;

import com.grokthecode.common.utilities.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAwareImpl auditorAware() {

        return new AuditorAwareImpl();
    }
}
