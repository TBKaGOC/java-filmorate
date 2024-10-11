package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.FeedEventType;
import ru.yandex.practicum.filmorate.dto.FeedOperationType;
import ru.yandex.practicum.filmorate.model.Feed;

@Component
public class FeedMapper {
    public FeedDto mapToFeedDto(Feed feed) {
        return FeedDto.builder()
                .entityId(feed.getEntityId())
                .timestamp(feed.getTimestamp())
                .userId(feed.getUserId())
                .eventType(FeedEventType.valueOf(feed.getEventType()))
                .operation(FeedOperationType.valueOf(feed.getOperation()))
                .entityId(feed.getEntityId())
                .build();
    }

    public Feed mapToFeed(FeedDto feed) {
        return Feed.builder()
                .entityId(feed.getEntityId())
                .timestamp(feed.getTimestamp())
                .userId(feed.getUserId())
                .eventType(feed.getEventType().name())
                .operation(feed.getOperation().name())
                .entityId(feed.getEntityId())
                .build();
    }
}
