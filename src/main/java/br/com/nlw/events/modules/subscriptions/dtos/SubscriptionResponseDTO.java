package br.com.nlw.events.modules.subscriptions.dtos;

import java.util.UUID;

public record SubscriptionResponseDTO(UUID subscriptionId, String designation) {
}
