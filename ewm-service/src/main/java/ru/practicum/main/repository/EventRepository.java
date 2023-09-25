package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.util.Pagination;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiator_Id(Long id, Pageable pageable);

    List<Event> findAllByIdIn(Collection<Long> eventsId);

//    @Query("SELECT e " +
//            "FROM Event AS e " +
//            "JOIN FETCH e.initiator " +
//            "JOIN FETCH e.category " +
//            "WHERE e.eventDate > :rangeStart " +
//            "AND (e.category.id IN :categories OR :categories IS NULL) " +
//            "AND (e.initiator.id IN :users OR :users IS NULL) " +
//            "AND (e.state IN :states OR :states IS NULL)"
//    )
//    List<Event> findAllForAdmin(@Param("users") List<Long> users,
//                                @Param("states") List<EventStatus> states,
//                                @Param("categories") List<Long> categories,
//                                @Param("rangeStart") LocalDateTime rangeStart,
//                                Pagination pageable);

    @Query("select e from Event e " +
            "where (coalesce(:userIds, null) is null or e.initiator.id in :userIds) " +
            "and (coalesce(:states, null) is null or e.state in :states) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) ")
    List<Event> findAllForAdmin(@Param("userIds") Collection<Long> userIds,
                            @Param("states") Collection<EventStatus> states,
                            @Param("categoryIds") Collection<Long> categoryIds,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            Pageable pageable);

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
    List<Event> findAllPublishStateAvailable(@Param("state") EventStatus state,
                                             @Param("rangeStart") LocalDateTime rangeStart,
                                             @Param("categories") Set<Long> categories,
                                             @Param("paid") Boolean paid,
                                             @Param("text") String text,
                                             Pagination pageable);

    @Query("SELECT MIN(e.publishedOn) FROM Event e WHERE e.id IN :eventsId")
    Optional<LocalDateTime> getStart(@Param("eventsId") List<Long> eventsId);
}
