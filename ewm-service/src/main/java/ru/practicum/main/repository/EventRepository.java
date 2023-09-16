package ru.practicum.main.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.util.Pagination;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(Long categoryId);

    List<Event> findAllByInitiator_Id(Long id, Pageable pageable);

    List<Event> findAllByIdIn(Collection<Long> eventsId);

    Optional<Event> findByIdAndInitiatorId(Long initiatorId, Long eventId);

    @Query("SELECT event " +
           "FROM Event AS event " +
           "JOIN FETCH event.initiator " +
           "JOIN FETCH event.category " +
           "WHERE event.eventDate > :rangeStart " +
           "AND (event.category.id IN :categories OR :categories IS NULL) " +
           "AND (event.initiator.id IN :users OR :users IS NULL) " +
           "AND (event.state IN :states OR :states IS NULL)")
    List<Event> findAllForAdmin(Set<Long> users, Set<Long> categories, List<EventStatus> states,
                                LocalDateTime rangeStart, PageRequest pageable);

    @Query("SELECT event " +
           "FROM Event AS event " +
           "JOIN FETCH event.initiator i " +
           "JOIN FETCH event.category c " +
           "WHERE event.state = :state " +
           "AND (event.participantLimit = 0 OR event.participantLimit > (" +
                    "SELECT COUNT(*) " +
                    "FROM Request AS request " +
                    "WHERE request.event.id = event.id AND request.status = 'CONFIRMED')) " +
                    "AND (event.category.id IN :categories OR :categories IS NULL) " +
                    "AND event.eventDate > :rangeStart " +
                    "AND (event.paid = :paid OR :paid IS NULL) " +
                    "AND (:text IS NULL OR " +
                    "(UPPER(event.annotation) LIKE UPPER(CONCAT('%', :text, '%'))) " +
                    "OR (UPPER(event.description) LIKE UPPER(CONCAT('%', :text, '%'))) " +
                    "OR (UPPER(event.title) LIKE UPPER(CONCAT('%', :text, '%'))))")
    List<Event> findAllPublishStateAvailable(EventStatus state, LocalDateTime rangeStart, Set<Long> categories,
                                             Boolean paid, String text, Pagination pageable);

    @Query("SELECT MIN(e.publishedOn) FROM Event e WHERE e.id IN :eventsId")
    Optional<LocalDateTime> getStart(List<Long> eventsId);
}
