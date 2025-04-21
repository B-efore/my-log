package com.jiwon.mylog.security.token;

import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveToken(TokenRequest tokenRequest) {
        User user = userRepository.findById(tokenRequest.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        RefreshToken refreshToken = tokenRepository.findByUserId(user.getId())
                .map(t -> t.update(tokenRequest.getRefreshToken()))
                .orElseGet(() -> RefreshToken.builder()
                        .user(user)
                        .refreshToken(tokenRequest.getRefreshToken())
                        .build());

        tokenRepository.save(refreshToken);
    }
}
