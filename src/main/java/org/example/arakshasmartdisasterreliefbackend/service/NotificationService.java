package org.example.arakshasmartdisasterreliefbackend.service;
import org.example.arakshasmartdisasterreliefbackend.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByCategory(String category);

    Notification saveNotification(Notification notification);

    Notification markAsRead(Long id);

    void markAllAsRead();

    void deleteNotification(Long id);

}