package ru.practicum.main.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.main.entity.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.*;

@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", updatable = false)
    @ToString.Exclude
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "created_date", updatable = false, nullable = false)
    @DateTimeFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime created = LocalDateTime.now();
}
