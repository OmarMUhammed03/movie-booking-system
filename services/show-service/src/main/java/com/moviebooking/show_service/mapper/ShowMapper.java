package com.moviebooking.show_service.mapper;

import com.moviebooking.show_service.model.Show;
import com.moviebooking.show_service.dto.request.ShowRequest;
import com.moviebooking.show_service.dto.response.ShowResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Show toEntity(ShowRequest request);

    ShowResponse toResponse(Show show);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(ShowRequest request, @MappingTarget Show show);
}
