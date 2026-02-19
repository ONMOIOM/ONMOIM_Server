package backend.onmoim.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProperties mailProperties;

    @Bean
    public List<JavaMailSender> javaMailSenders() {
        // accounts()가 null일 경우 빈 리스트를 반환하여 stream() 호출 시 NPE 방지
        List<MailProperties.MailAccount> accounts = Optional.ofNullable(mailProperties.accounts())
                .orElse(Collections.emptyList());

        return accounts.stream()
                .map(account -> {
                    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                    mailSender.setHost(mailProperties.host());
                    mailSender.setPort(mailProperties.port());
                    mailSender.setUsername(account.username());
                    mailSender.setPassword(account.password());

                    Properties props = new Properties();
                    // NPE 방지 - properties가 null이 아닐 때만 복사
                    if (mailProperties.properties() != null) {
                        mailProperties.properties().forEach(props::setProperty);
                    }
                    mailSender.setJavaMailProperties(props);

                    return mailSender;
                })
                .collect(Collectors.toList());
    }
}