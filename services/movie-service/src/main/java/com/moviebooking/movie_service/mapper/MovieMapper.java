package com.moviebooking.movie_service.mapper;

import com.moviebooking.movie_service.model.Movie;
import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Movie toEntity(MovieRequest request);

    MovieResponse toResponse(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(MovieRequest request, @MappingTarget Movie movie);
}