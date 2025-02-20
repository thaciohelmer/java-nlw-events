package br.com.nlw.events.modules.subscriptions.controllers;

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

import br.com.nlw.events.modules.shared.dtos.ErrorResponseDTO;
import br.com.nlw.events.modules.shared.utils.ResponseUtil;
import br.com.nlw.events.modules.subscriptions.dtos.SubscriptionResponseDTO;
import br.com.nlw.events.modules.subscriptions.models.SubscriptionRankingItem;
import br.com.nlw.events.modules.subscriptions.models.SubscriptionRankingItemByUser;
import br.com.nlw.events.modules.subscriptions.services.SubscriptionService;
import br.com.nlw.events.modules.users.models.User;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({ "/{slug}", "/{slug}/{indicatorId}" })
    public ResponseEntity<Object> create(
            @PathVariable String slug,
            @PathVariable(required = false) String indicatorId,
            @RequestBody User user) {
        try {
            SubscriptionResponseDTO createdSubscription = this.subscriptionService.create(slug, user, indicatorId);
            URI location = ResponseUtil.getLocationURIForId(createdSubscription.subscriptionId());
            return ResponseEntity.created(location).body(createdSubscription);
        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponseDTO(HttpStatus.NOT_FOUND, exception.getMessage()));
        } catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(HttpStatus.CONFLICT, exception.getMessage()));
        }
    }

    @GetMapping("/indications/{slug}/ranking")
    public ResponseEntity<Object> getEventSubscriptionIndicationRaking(@PathVariable() String slug) {
        try {
            List<SubscriptionRankingItem> subscriptionsRanking = this.subscriptionService
                    .getEventSubscriptionIndicationRaking(slug);

            return ResponseEntity.ok(subscriptionsRanking.subList(0, 3));
        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponseDTO(HttpStatus.NOT_FOUND, exception.getMessage()));
        }
    }

    @GetMapping("/indications/{slug}/ranking/{userId}")
    public ResponseEntity<Object> getEventSubscriptionIndicationRakingByUser(@PathVariable() String slug,
            @PathVariable() String userId) {
        try {
            SubscriptionRankingItemByUser subscriptionsRanking = this.subscriptionService
                    .getEventSubscriptionIndicationRakingByUser(slug, userId);

            return ResponseEntity.ok(subscriptionsRanking);
        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponseDTO(HttpStatus.NOT_FOUND, exception.getMessage()));
        }
    }
}
