package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.dto.external.AnalyticsRequest;
import com.socialmedia.socialmediabackend.dto.external.ExternalNotificationRequest;
import com.socialmedia.socialmediabackend.dto.external.ModerationResponse;
import com.socialmedia.socialmediabackend.dto.external.ProfileEnrichmentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final String notificationsUrl;
    private final String analyticsUrl;
    private final String profileEnrichmentUrl;
    private final String moderationUrl;

    public ExternalApiService(RestTemplate restTemplate,
                              WebClient webClient,
                              @Value("${external.notifications.url:http://localhost:9002/notify}") String notificationsUrl,
                              @Value("${external.analytics.url:http://localhost:9003/analytics/posts}") String analyticsUrl,
                              @Value("${external.profile-enrichment.url:http://localhost:9004/profile}") String profileEnrichmentUrl,
                              @Value("${external.moderation.url:http://localhost:9005/moderate}") String moderationUrl) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
        this.notificationsUrl = notificationsUrl;
        this.analyticsUrl = analyticsUrl;
        this.profileEnrichmentUrl = profileEnrichmentUrl;
        this.moderationUrl = moderationUrl;
    }

    public void notifyExternalSystem(ExternalNotificationRequest request) {
        executeWithRetry(() -> restTemplate.exchange(notificationsUrl, HttpMethod.POST, new HttpEntity<>(request), Void.class));
    }

    public void sendPostAnalytics(AnalyticsRequest request) {
        executeWithRetry(() -> restTemplate.exchange(analyticsUrl, HttpMethod.POST, new HttpEntity<>(request), Void.class));
    }

    public ProfileEnrichmentResponse enrichProfile(String email) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(profileEnrichmentUrl).queryParam("email", email).build())
                .retrieve()
                .bodyToMono(ProfileEnrichmentResponse.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(250)))
                .onErrorResume(ex -> Mono.empty())
                .blockOptional()
                .orElse(null);
    }

    public ModerationResponse moderateContent(String content) {
        return webClient.post()
                .uri(moderationUrl)
                .bodyValue(new ModerationPayload(content))
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(250)))
                .onErrorResume(ex -> Mono.just(new ModerationResponse(true, "moderation service unavailable")))
                .block();
    }

    private void executeWithRetry(Runnable action) {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                action.run();
                return;
            } catch (RuntimeException ex) {
                lastException = ex;
                try {
                    Thread.sleep((long) (250 * Math.pow(2, attempt - 1)));
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }
        throw lastException;
    }

    private record ModerationPayload(String content) {
    }
}
