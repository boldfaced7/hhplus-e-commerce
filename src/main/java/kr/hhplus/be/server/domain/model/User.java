package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.user.UserEmail;
import kr.hhplus.be.server.domain.vo.user.UserId;
import kr.hhplus.be.server.domain.vo.user.UserName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private UserId userId;
    private UserName name;
    private UserEmail email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User create(
            UserName name,
            UserEmail email
    ) {
        return new User(
                null,
                name,
                email,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User create(
            UserId userId,
            UserName name,
            UserEmail email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new User(
                userId,
                name,
                email,
                createdAt,
                updatedAt
        );
    }
}
