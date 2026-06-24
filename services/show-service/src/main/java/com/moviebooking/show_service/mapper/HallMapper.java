package com.moviebooking.show_service.mapper;

import com.moviebooking.show_service.model.Hall;
import com.moviebooking.show_service.dto.request.HallRequest;
import com.moviebooking.show_service.dto.response.HallResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HallMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Hall toEntity(HallRequest request);

    HallResponse toResponse(Hall hall);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(HallRequest request, @MappingTarget Hall hall);
}
