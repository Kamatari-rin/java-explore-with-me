package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.entity.HitEntity;

@Mapper(componentModel = "spring")
public interface HitMapper {

    HitEntity toHitEntity(HitRequestDto hitRequestDto);

    HitResponseDto toHitResponseDto(HitEntity hitEntity);
}
