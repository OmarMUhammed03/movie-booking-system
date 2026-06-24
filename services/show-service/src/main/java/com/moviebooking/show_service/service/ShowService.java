package com.moviebooking.show_service.service;

import com.moviebooking.show_service.dto.request.ShowRequest;
import com.moviebooking.show_service.dto.response.ShowResponse;

import java.util.List;
import java.util.UUID;

public interface ShowService {
    ShowResponse createShow(ShowRequest request);
    ShowResponse getShowById(UUID id);
    List<ShowResponse> getAllShows();
    ShowResponse updateShow(UUID id, ShowRequest request);
    void deleteShow(UUID id);
}
