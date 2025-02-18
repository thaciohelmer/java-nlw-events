package br.com.nlw.events.modules.events.services;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;

import br.com.nlw.events.modules.events.models.Event;
import br.com.nlw.events.modules.events.repositories.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event create(Event event) {
        Slugify slugify = Slugify
                .builder()
                .lowerCase(true)
                .locale(Locale.ENGLISH)
                .build();

        event.setSlug(slugify.slugify(event.getTitle()));

        return eventRepository.save(event);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event getBySlug(String slug) {
        return eventRepository
                .findBySlug(slug)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                String.format("Evento com slug %s não encontrado", slug)));
    }
}
