package com.moviebooking.show_service.mapper;

import com.moviebooking.show_service.model.Ticket;
import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Ticket toEntity(TicketRequest request);

    TicketResponse toResponse(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(TicketRequest request, @MappingTarget Ticket ticket);
}
