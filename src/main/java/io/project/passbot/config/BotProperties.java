package io.project.passbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    String name;
    String token;
    long owner;
}
