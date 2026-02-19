package backend.onmoim.domain.event.converter;

import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.entity.Event;

public class EventConverter {
    public static EventResDTO toResDTO(Event event){
        return EventResDTO.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .streetAddress(event.getStreetAddress())
                .lotNumberAddress(event.getLotNumberAddress())
                .price(event.getPrice())
                .introduction(event.getIntroduction())
                .status(event.getStatus())
                .build();
    }
}
