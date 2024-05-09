package com.api.readinglog.domain.email.service;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("이메일 전송 성공 테스트")
    void sendEmailTest() throws Exception {
        // given
        MimeMessageHelper mockMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Test HTML Content");

        // javaMailSender.send가 성공적으로 이루어지도록 설정
        // when
        CompletableFuture<Void> resultFuture = emailService.sendEmail("test@example.com", "code", "subject",
                "tempPassword.html");

        // then
        assertDoesNotThrow(() -> resultFuture.get());

        // send 메소드가 실제로 호출되었는지 확인
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }


    @Test
    @DisplayName("이메일 전송 실패 테스트")
    void sendEmailTest_Fail() throws Exception {
        // given
        MimeMessageHelper mockMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Test HTML Content");
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // javaMailSender.send가 MailException을 던지도록 설정
        Mockito.doThrow(new MailSendException("Failed to send email")).when(javaMailSender)
                .send(any(MimeMessage.class));

        // when
        CompletableFuture<Void> resultFuture = emailService.sendEmail("test@example.com", "code", "subject",
                "tempPassword.html");

        // then
        // CompletableFuture의 완료를 기다린 후, ExecutionException이 발생하는지 확인
        ExecutionException executionException = assertThrows(ExecutionException.class, resultFuture::get);

        // ExecutionException의 원인이 MailException인지 확인
        assertTrue(executionException.getCause() instanceof MailException);
    }

}
