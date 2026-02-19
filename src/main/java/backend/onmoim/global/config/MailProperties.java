package backend.onmoim.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        String host,
        int port,
        List<MailAccount> accounts,
        Map<String, String> properties
) {
    public record MailAccount(String username, String password) {}
}
