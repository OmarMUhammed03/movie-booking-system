package com.moviebooking.show_service.service.impl;

import com.moviebooking.show_service.dto.request.ShowRequest;
import com.moviebooking.show_service.dto.response.ShowResponse;
import com.moviebooking.show_service.exception.ShowNotFoundException;
import com.moviebooking.show_service.mapper.ShowMapper;
import com.moviebooking.show_service.model.Show;
import com.moviebooking.show_service.repository.ShowRepository;
import com.moviebooking.show_service.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final ShowMapper showMapper;

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        Show show = showMapper.toEntity(request);
        Show saved = showRepository.save(show);
        return showMapper.toResponse(saved);
    }

    @Override
    public ShowResponse getShowById(UUID id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ShowNotFoundException(id));
        return showMapper.toResponse(show);
    }

    @Override
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll()
                .stream()
                .map(showMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ShowResponse updateShow(UUID id, ShowRequest request) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ShowNotFoundException(id));

        showMapper.updateEntityFromRequest(request, show);
        Show updated = showRepository.save(show);
        return showMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteShow(UUID id) {
        if (!showRepository.existsById(id)) {
            throw new ShowNotFoundException(id);
        }
        showRepository.deleteById(id);
    }
}
