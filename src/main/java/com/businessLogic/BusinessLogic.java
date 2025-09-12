package com.businessLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.topics.Notifications;

@Service
public class BusinessLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);
    private int notificationCount = 0;

    // Simple method that updates the notificaition count, moreso of a placeholder
    public void processNotification(String topicName) {
        notificationCount++;
        LOG.info("Processed notification for topic: " + topicName + ". Total notifications: " + notificationCount);
    }

    public Notifications createNotifcations() {
        Notifications notification = new Notifications();
        notification.setNotificationCount(notificationCount);
        return notification;
    }
}
