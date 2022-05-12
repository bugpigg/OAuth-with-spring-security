package com.bugpigg.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserProfile {
    private final String oauthId;
    private final String name;
    private final String email;
    private final String imageUrl;

    public Member toMember() {
        return new Member(oauthId, name, email, imageUrl, Role.USER);
    }
}
