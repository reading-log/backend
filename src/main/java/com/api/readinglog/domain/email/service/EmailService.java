package com.api.readinglog.domain.email.service;

import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.EmailException;
import com.api.readinglog.domain.email.entity.EmailAuth;
import com.api.readinglog.domain.email.repository.EmailAuthRepository;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Async
    public void sendAuthCode(String toEmail) {
        String authCode = createRandomCode();
        emailAuthRepository.save(EmailAuth.of(toEmail, authCode));
        sendEmail(toEmail, authCode, "[리딩 로그] 이메일 인증 코드", "authCode.html");
    }

    @Async
    public void sendTemporaryPassword(Long memberId, String toEmail) {
        String tempPassword = createRandomCode();
        sendEmail(toEmail, tempPassword, "[리딩 로그] 임시 비밀번호", "tempPassword.html");

        Member member = memberService.getMemberById(memberId);
        member.updatePassword(passwordEncoder.encode(tempPassword));
    }

    @Scheduled(fixedDelay = 60000)
    public void deleteExpiredAuthCodes() {
        emailAuthRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }

    @Async
    public void sendEmail(String toEmail, String code, String subject, String templateName) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(setContext(code, templateName), true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void verifyAuthCode(String email, String authCode) {
        emailAuthRepository.findByEmailAndAuthCode(email, authCode)
                .orElseThrow(() -> new EmailException(ErrorCode.INVALID_AUTH_CODE));
    }

    // 인증번호 및 임시 비밀번호 생성
    private String createRandomCode() {
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

}

