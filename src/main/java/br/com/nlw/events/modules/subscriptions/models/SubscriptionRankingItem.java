package br.com.nlw.events.modules.subscriptions.models;

import java.util.UUID;

public record SubscriptionRankingItem(Long indications, UUID userId, String name) {

}
