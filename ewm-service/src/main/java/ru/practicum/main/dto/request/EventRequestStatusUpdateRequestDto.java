package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.entity.enums.RequestUpdateStatus;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequestDto {

    private Set<Long> requestIds;

    private RequestUpdateStatus status;
}
