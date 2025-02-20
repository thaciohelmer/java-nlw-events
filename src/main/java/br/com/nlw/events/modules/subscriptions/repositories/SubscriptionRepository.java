package br.com.nlw.events.modules.subscriptions.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.nlw.events.modules.subscriptions.models.Subscription;
import br.com.nlw.events.modules.subscriptions.models.SubscriptionRankingItem;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    public Optional<Subscription> findByEventIdAndSubscriberId(UUID eventId, UUID subscriberId);

    @Query(value = """
                SELECT
                    COUNT(subscriptions.id) AS count,
                    subscriptions.indicator_id,
                    users.name
                FROM subscriptions
                INNER JOIN users ON subscriptions.indicator_id = users.id
                WHERE
                    subscriptions.indicator_id IS NOT NULL
                    AND subscriptions.event_id = :eventId
                GROUP BY
                    subscriptions.indicator_id,
                    users.name
                ORDER BY
                    count DESC
            """, nativeQuery = true)
    public List<SubscriptionRankingItem> generateRaking(@Param("eventId") UUID eventId);
}
