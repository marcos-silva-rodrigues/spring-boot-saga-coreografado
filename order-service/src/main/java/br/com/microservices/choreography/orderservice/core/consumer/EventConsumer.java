package br.com.microservices.choreography.orderservice.core.consumer;

import br.com.microservices.choreography.orderservice.core.service.EventService;
import br.com.microservices.choreography.orderservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

  private final JsonUtil jsonUtil;
  private final EventService eventService;

  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.topic.notify-ending}"
  )
  public void consumerNotifyEndingEvent(String payload) {
    log.info("Receiving ending notification event {} from notify-ending topic", payload);
    var event = jsonUtil.toEvent(payload);
    eventService.notifyEnding(event);
  }
}
