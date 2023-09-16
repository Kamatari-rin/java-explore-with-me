package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.enums.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventIdAndIdIn(Long eventId, Set<Long> requestIds);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);

    Optional<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(Long eventId, Long userId,
                                                             Collection<Long> requestsId);
}
