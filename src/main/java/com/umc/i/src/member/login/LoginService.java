package com.umc.i.src.member.login;

import com.umc.i.src.member.login.model.PostLoginMemberReq;
import com.umc.i.utils.UserSha256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginDao loginRepository;

    public PostLoginMemberReq login(String loginEmail, String password) {
        return loginRepository.findByLoginEmail(loginEmail)
                .filter(m -> m.getPassword().equals(passwordCrypto(password)))
                .orElse(null);
    }

    public String passwordCrypto(String password) {
        return UserSha256.encrypt(password);
    }
}
