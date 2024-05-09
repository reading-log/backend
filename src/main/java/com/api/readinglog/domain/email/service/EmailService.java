package com.api.readinglog.domain.email.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.EmailException;
import com.api.readinglog.common.redis.service.RedisService;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.member.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final MemberService memberService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Async
    public void sendAuthCode(String toEmail) {
        String authCode = generateRandomCode();
        saveAuthCode(toEmail, authCode);
        sendEmail(toEmail, authCode, "[리딩 로그] 이메일 인증 코드", "authCode.html");
    }

    @Async
    public void sendTemporaryPassword(String toEmail, String tempPassword) {
        sendEmail(toEmail, tempPassword, "[리딩 로그] 임시 비밀번호", "tempPassword.html")
                .thenRun(() -> log.info("{}에 임시 비밀번호 이메일 전송 성공", toEmail))
                .exceptionally(ex -> {
                    log.error("{} 이메일 전송 실패: ", toEmail, ex);
                    return null;
                });
    }

    @Async
    public CompletableFuture<Void> sendEmail(String toEmail, String code, String subject, String templateName) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(setContext(code, templateName), true);

            javaMailSender.send(mimeMessage);
            return CompletableFuture.completedFuture(null);

        } catch (MailException | MessagingException e) {
            log.error("이메일 전송 중 예외 발생", e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    public String updatePassword(String email) {
        String tempPassword = generateRandomCode();
        Member member = memberService.getMemberByEmailAndRole(email, MemberRole.MEMBER_NORMAL);
        member.updatePassword(passwordEncoder.encode(tempPassword));
        return tempPassword;
    }

    public void validateAuthCode(String email, String authCode) {
        findByEmailAndAuthCode(authCode)
                .filter(e -> e.equals(email))
                .orElseThrow(() -> new EmailException(ErrorCode.INVALID_AUTH_CODE));
    }

    public void validateMember(String email) {
        if (!isMemberExists(email)) {
            throw new EmailException(ErrorCode.NOT_FOUND_MEMBER);
        }
    }

    // 인증번호 및 임시 비밀번호 생성
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            switch (random.nextInt(3)) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97)); // 소문자
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65)); // 대문자
                    break;
                case 2:
                    key.append(random.nextInt(10)); // 숫자
                    break;
            }
        }
        return key.toString();
    }

    private String setContext(String code, String templateName) {
        Context context = new Context();
        log.info(code);
        context.setVariable("code", code);
        return templateEngine.process(templateName, context);
    }

    private void saveAuthCode(String email, String authCode) {
        redisService.setData(authCode, email, 5L, TimeUnit.MINUTES); // 5분 설정
    }

    private Optional<String> findByEmailAndAuthCode(String authCode) {
        Object email = redisService.getData(authCode);
        return Optional.ofNullable(email != null ? email.toString() : null);
    }

    private boolean isMemberExists(String email) {
        return (memberService.getMemberByEmailAndRole(email, MemberRole.MEMBER_NORMAL)) != null;
    }
}

