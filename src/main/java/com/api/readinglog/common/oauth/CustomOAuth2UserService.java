package com.api.readinglog.common.oauth;

import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.domain.token.entity.AccessToken;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.token.repository.AccessTokenRepository;
import com.api.readinglog.domain.member.repository.MemberRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final AccessTokenRepository accessTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        String socialAccessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes(), socialAccessToken);

        // 각 플랫폼의 유저 정보를 공통화 처리해주는 부분
        Map<String, Object> memberAttribute = oAuth2Attribute.convertToMap();

        String email = (String) memberAttribute.get("email");
        MemberRole memberRole = MemberRole.of(registrationId);

        String name = (String) memberAttribute.get("name");
        String picture = (String) memberAttribute.get("picture");

        Member member = memberRepository.findByEmailAndRole(email, memberRole)
                .map(existingMember -> {
                    existingMember.updateProfile(name, picture);

                    // AccessToken 엔티티 업데이트 또는 생성 로직 수정
                    accessTokenRepository.findByMember(existingMember).ifPresentOrElse(
                            existingToken -> existingToken.updateSocialAccessToken(socialAccessToken),
                            () -> accessTokenRepository.save(AccessToken.of(socialAccessToken, existingMember))
                    );
                    return existingMember;

                }).orElseGet(() -> {
                    Member newMember = memberRepository.save(Member.of(email, name, picture, memberRole));
                    accessTokenRepository.save(AccessToken.of(socialAccessToken, newMember)); // 새로운 Member에 대한 AccessToken 저장
                    return newMember;
                });

        return new CustomUserDetail(
                member,
                Collections.singleton(new SimpleGrantedAuthority(MemberRole.of(registrationId).name())),
                memberAttribute);
    }

}
