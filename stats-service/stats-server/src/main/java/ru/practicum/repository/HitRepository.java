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

    @Query("select new ru.practicum.dto.GetStatsDto(eh.app, eh.uri," +
            " case when :unique = true " +
            " then count(distinct (eh.ip))" +
            " else count (eh.ip) " +
            " end " +
            ") " +
            "from HitEntity eh " +
            "where eh.timestamp between :start and :end" +
            "   and (coalesce(:uris, null) is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by 3 desc")
    List<GetStatsDto> getStats(@Param("uris") Set<String> uris,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("unique") Boolean unique);
}
