package com.moviebooking.show_service.service;

import com.moviebooking.show_service.dto.request.HallRequest;
import com.moviebooking.show_service.dto.response.HallResponse;

import java.util.List;
import java.util.UUID;

public interface HallService {
    HallResponse createHall(HallRequest request);
    HallResponse getHallById(UUID id);
    List<HallResponse> getAllHalls();
    HallResponse updateHall(UUID id, HallRequest request);
    void deleteHall(UUID id);
}
