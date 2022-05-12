package com.bugpigg.auth.service;

import com.bugpigg.auth.domain.Member;
import com.bugpigg.auth.domain.OAuthAttribute;
import com.bugpigg.auth.domain.UserProfile;
import com.bugpigg.auth.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        // OAuth 서비스(github, google, naver)에서 가져온 유저 정보를 담고있음
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // OAuth 서비스의 유저 정보들
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // OAuth 서비스 이름
        String registrationId = userRequest.getClientRegistration()
            .getRegistrationId();
        // OAuth 로그인 시 키(pk)가 되는 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌
        UserProfile userProfile = OAuthAttribute.extract(registrationId,
            attributes);

        Member member = saveOrUpdate(userProfile); // DB에 저장

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
            attributes,
            userNameAttributeName);
    }

    private Member saveOrUpdate(UserProfile userProfile) {
        Member member = userRepository.findByOauthId(userProfile.getOauthId())
            .map(m -> m.update(userProfile.getName(), userProfile.getEmail(), userProfile.getImageUrl()))
            .orElse(userProfile.toMember());
        return userRepository.save(member);
    }
}
