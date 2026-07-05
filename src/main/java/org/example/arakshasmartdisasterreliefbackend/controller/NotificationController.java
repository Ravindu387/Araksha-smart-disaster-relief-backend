package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.entity.Notification;
import org.example.arakshasmartdisasterreliefbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/category/{category}")
    public List<Notification> getByCategory(@PathVariable String category) {
        return notificationService.getNotificationsByCategory(category);
    }

    @PostMapping
    public Notification addNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    public org.springframework.http.ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return org.springframework.http.ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return org.springframework.http.ResponseEntity.ok().build();
    }
}