package ru.practicum.main.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LocationDtoCoordinates {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
