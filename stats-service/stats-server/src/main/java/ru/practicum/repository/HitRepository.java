package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.entity.HitEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface HitRepository extends JpaRepository<HitEntity, Long> {

    @Query("SELECT new ru.practicum.dto.GetStatsDto(hit.app, hit.uri, count(distinct hit.ip)) " +
           "FROM HitEntity hit " +
           "WHERE hit.timestamp BETWEEN :start AND :end " +
           "AND (COALESCE(:uris, null) IS NULL OR hit.uri IN :uris) " +
           "GROUP BY hit.app, hit.uri " +
           "ORDER BY count(distinct hit.ip) DESC ")
    List<GetStatsDto> findStatsBetweenTimestampUniqUri(@Param("uris") Set<String> uris,
                                                       @Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.GetStatsDto(hit.app, hit.uri, count(hit.ip)) " +
           "FROM HitEntity hit " +
           "WHERE hit.timestamp BETWEEN :start AND :end " +
           "AND (COALESCE(:uris, null) IS NULL OR hit.uri IN :uris) " +
           "GROUP BY hit.app, hit.uri " +
           "ORDER BY count(hit.ip) DESC ")
    List<GetStatsDto> findStatsBetweenTimestampNotUniqUri(@Param("uris") Set<String> uris,
                                                          @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);
}
