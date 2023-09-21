package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    private final WebClient client;

    public StatsClient(@Value("${stats.server.url}") String baseUrl) {
        this.client = WebClient.create(baseUrl);
    }

    public ResponseEntity<List<GetStatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(GetStatsDto.class)
                .block();
    }

    public void saveStats(String app, String uri, String ip, LocalDateTime timestamp) {
        this.client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new HitRequestDto(app, uri, ip, timestamp))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}