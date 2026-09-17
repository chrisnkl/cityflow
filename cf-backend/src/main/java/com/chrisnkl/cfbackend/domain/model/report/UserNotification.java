package com.chrisnkl.cfbackend.domain.model.report;

import com.chrisnkl.cfbackend.domain.model.auth.User;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class UserNotification {

    private UUID id;
    private Notification notification;
    private User user;
    private boolean read;

    private Instant createdAt;
    private Instant updatedAt;

}
