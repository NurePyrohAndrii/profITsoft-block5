package com.profITsoft.gateway.controller;

import com.profITsoft.gateway.auth.dto.UserInfo;
import com.profITsoft.gateway.data.UserSession;
import com.profITsoft.gateway.service.SessionService;
import com.profITsoft.gateway.service.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final SessionService sessionService;

    @GetMapping("/profile")
    public Mono<UserInfo> profile(ServerWebExchange exchange) {
        return sessionService.checkSession(exchange)
                .flatMap(this::toUserInfo)
                .onErrorResume(UnauthorizedException.class, e -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
                });
    }

    private Mono<UserInfo> toUserInfo(UserSession session) {
        return Mono.just(UserInfo.builder()
                .email(session.getEmail())
                .name(session.getName())
                .build());
    }

}
