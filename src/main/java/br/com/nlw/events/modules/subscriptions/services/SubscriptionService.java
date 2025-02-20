package br.com.nlw.events.modules.subscriptions.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.modules.events.models.Event;
import br.com.nlw.events.modules.events.repositories.EventRepository;
import br.com.nlw.events.modules.subscriptions.dtos.SubscriptionResponseDTO;
import br.com.nlw.events.modules.subscriptions.models.Subscription;
import br.com.nlw.events.modules.subscriptions.models.SubscriptionRankingItem;
import br.com.nlw.events.modules.subscriptions.models.SubscriptionRankingItemByUser;
import br.com.nlw.events.modules.subscriptions.repositories.SubscriptionRepository;
import br.com.nlw.events.modules.users.models.User;
import br.com.nlw.events.modules.users.repositories.UserRepository;

@Service
public class SubscriptionService {

        @Autowired
        private SubscriptionRepository subscriptionRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private EventRepository eventRepository;

        public SubscriptionResponseDTO create(String eventSlug, User user, String indicatorId) {
                Event eventFromSlug = this.eventRepository
                                .findBySlug(eventSlug)
                                .orElseThrow(() -> new NoSuchElementException(
                                                String.format("Evento com slug %s não encontrado", eventSlug)));

                User userFromIndicatorId = (indicatorId != null && !indicatorId.isBlank())
                                ? userRepository.findById(UUID.fromString(indicatorId))
                                                .orElseThrow(() -> new NoSuchElementException(
                                                                String.format("Usuário com id %s que indicou não encontrado",
                                                                                indicatorId)))
                                : null;

                User userFromDb = this.userRepository.findByEmail(user.getEmail())
                                .orElseGet(() -> this.userRepository.save(user));

                this.subscriptionRepository.findByEventIdAndSubscriberId(eventFromSlug.getId(), userFromDb.getId())
                                .ifPresent(subscription -> {
                                        throw new IllegalStateException(
                                                        String.format(
                                                                        "O usuário %s já está cadastrado no evento %s",
                                                                        userFromDb.getEmail(),
                                                                        eventSlug));
                                });

                Subscription subscription = this.subscriptionRepository.save(
                                Subscription
                                                .builder()
                                                .event(eventFromSlug)
                                                .subscriber(userFromDb)
                                                .indicator(userFromIndicatorId)
                                                .build());

                return new SubscriptionResponseDTO(
                                subscription.getId(),
                                String.format("https://devstage.com/%s/%s", eventFromSlug.getSlug(),
                                                userFromDb.getId()));
        }

        public List<SubscriptionRankingItem> getEventSubscriptionIndicationRaking(String eventSlug) {
                Event eventFromSlug = this.eventRepository
                                .findBySlug(eventSlug)
                                .orElseThrow(() -> new NoSuchElementException(
                                                String.format("Evento com slug %s não encontrado", eventSlug)));

                return this.subscriptionRepository.generateRaking(eventFromSlug.getId());
        }

        public SubscriptionRankingItemByUser getEventSubscriptionIndicationRakingByUser(String eventSlug,
                        String userId) {
                List<SubscriptionRankingItem> ranking = this.getEventSubscriptionIndicationRaking(eventSlug);

                SubscriptionRankingItem item = ranking
                                .stream()
                                .filter(i -> i.userId().equals(UUID.fromString(userId)))
                                .findFirst()
                                .orElseThrow(() -> new NoSuchElementException(
                                                String.format("Usuário com id %s não encontrado",
                                                                userId)));

                int position = ranking.indexOf(item);

                return new SubscriptionRankingItemByUser(item, position + 1);
        }
}
