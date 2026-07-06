package com.moviebooking.auth_service.service;

import com.moviebooking.shared.event.UserRegisteredEvent;

public interface MessagingService {
    void sendCreatUserEvent(UserRegisteredEvent user);
}
