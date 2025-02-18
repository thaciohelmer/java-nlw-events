package br.com.nlw.events.modules.events.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nlw.events.modules.events.models.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {
    public Optional<Event> findBySlug(String slugy);
}
