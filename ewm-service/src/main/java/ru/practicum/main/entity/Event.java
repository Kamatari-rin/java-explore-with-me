package ru.practicum.main.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.main.entity.enums.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "event_date",  nullable = false)
    @DateTimeFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;

    @Column(name = "request_moderation",  nullable = false)
    private Boolean requestModeration;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus state;

    @Column(name = "created_on", nullable = false)
    @DateTimeFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    @DateTimeFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime publishedOn;

    @Transient
    private Long confirmedRequests = 0L;
}
