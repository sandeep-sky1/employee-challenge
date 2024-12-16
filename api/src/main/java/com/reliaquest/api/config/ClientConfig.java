package com.reliaquest.api.config;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.exception.DownstreamException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

@Slf4j
@Configuration
public class ClientConfig {
    private static final int CONNECTION_TIMEOUT_MILLIS = 30000; // 30 seconds
    private static final int TIMEOUT_SECONDS = 120; // 2 minutes
    private static final int RESPONSE_TIMEOUT_SECONDS = 120; // 2 minutes

    @Bean
    public ApiClient apiClient(@Value("${app.server.url}") String url) {
        log.info("Creating ApiClient: {}", url);
        WebClient webClient = getBuilder(url)
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    log.info("Sending request to {}", request.url());
                    return Mono.just(ClientRequest.from(request).build());
                }))
                .build();

        webClient = configureRetry(webClient);

        return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(ApiClient.class);
    }

    private WebClient configureRetry(WebClient webClient) {
        return webClient
                .mutate()
                .filter((request, next) -> next.exchange(request)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof java.net.ConnectException
                                        || throwable instanceof java.net.SocketTimeoutException
                                        || throwable instanceof IllegalStateException)
                                .doBeforeRetry(retrySignal -> {
                                    long attempt = retrySignal.totalRetries() + 1;
                                    log.info("Retry attempt {} for request: {}", attempt, request.url());
                                })
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                    log.error(
                                            "Retry attempts exhausted for {}. Last error: {}",
                                            request.url(),
                                            retrySignal.failure().getMessage());
                                    return retrySignal.failure();
                                })))
                .build();
    }

    private static WebClient.Builder getBuilder(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, resp -> {
                    logErrorResponse(resp);
                    return resp.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new DownstreamException(resp.statusCode().value(), errorBody)));
                })
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()));
    }

    private static void logErrorResponse(ClientResponse resp) {
        if (resp.statusCode().is4xxClientError()) {
            log.error("Client error: {}", resp.statusCode());
        } else if (resp.statusCode().is5xxServerError()) {
            log.error("Server error: {}", resp.statusCode());
        }
    }

    /**
     * Creates a shared HttpClient with connection and response timeout configurations.
     *
     * @return A configured HttpClient instance.
     */
    private static HttpClient createHttpClient() {

        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS)
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT_SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_SECONDS)))
                .responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS))
                .compress(true)
                .followRedirect(true);
    }
}
