package com.jiwon.mylog.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotificationCountResponse {
    private long unreadCount;
}
