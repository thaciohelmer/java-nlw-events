package br.com.nlw.events.modules.events.controllers;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.modules.events.models.Event;
import br.com.nlw.events.modules.events.services.EventService;
import br.com.nlw.events.modules.shared.dtos.ErrorResponseDTO;
import br.com.nlw.events.modules.shared.utils.ResponseUtil;

@RestController
@RequestMapping("events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event createdEvent = this.eventService.create(event);
        URI location = ResponseUtil.getLocationURIForId(createdEvent.getId());
        return ResponseEntity.created(location).body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        List<Event> events = this.eventService.getAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("{slug}")
    public ResponseEntity<Object> getBySlug(@PathVariable String slug) {
        try {
            Event eventFromSlug = this.eventService.getBySlug(slug);
            return ResponseEntity.ok(eventFromSlug);
        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponseDTO(HttpStatus.NOT_FOUND, exception.getMessage()));
        }
    }

}
