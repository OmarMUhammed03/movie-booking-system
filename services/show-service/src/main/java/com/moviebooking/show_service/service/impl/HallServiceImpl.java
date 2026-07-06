package com.moviebooking.show_service.service.impl;

import com.moviebooking.show_service.dto.request.HallRequest;
import com.moviebooking.show_service.dto.response.HallResponse;
import com.moviebooking.show_service.exception.HallNotFoundException;
import com.moviebooking.show_service.mapper.HallMapper;
import com.moviebooking.show_service.model.Hall;
import com.moviebooking.show_service.repository.HallRepository;
import com.moviebooking.show_service.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final HallMapper hallMapper;

    @Override
    @Transactional
    public HallResponse createHall(HallRequest request) {
        Hall hall = hallMapper.toEntity(request);
        Hall saved = hallRepository.save(hall);
        return hallMapper.toResponse(saved);
    }

    @Override
    public HallResponse getHallById(UUID id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new HallNotFoundException(id));
        return hallMapper.toResponse(hall);
    }

    @Override
    public List<HallResponse> getAllHalls() {
        return hallRepository.findAll()
                .stream()
                .map(hallMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public HallResponse updateHall(UUID id, HallRequest request) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new HallNotFoundException(id));

        hallMapper.updateEntityFromRequest(request, hall);
        Hall updated = hallRepository.save(hall);
        return hallMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteHall(UUID id) {
        if (!hallRepository.existsById(id)) {
            throw new HallNotFoundException(id);
        }
        hallRepository.deleteById(id);
    }
}
