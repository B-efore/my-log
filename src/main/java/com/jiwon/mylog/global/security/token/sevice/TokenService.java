package com.jiwon.mylog.global.security.token.sevice;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.security.token.entity.RefreshToken;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
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

    @Transactional(readOnly = true)
    public void validateRefreshToken(Long userId, String refreshToken) {
        RefreshToken savedToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthorizationDeniedException("Refresh Token not found"));

        if(!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new AuthorizationDeniedException("Invalid Token");
        }
    }
}
