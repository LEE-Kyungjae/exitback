package com.personal.imp.model.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAccount {
    String email;
    String gender;
    String phone_number;
    Profile profile;
}
