package com.sergejs.orderservice.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.sergejs.orderservice.dto.request.OrderRequest;
import com.sergejs.orderservice.dto.response.OrderCreatedResponse;
import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.dto.response.TotalPriceResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Primary
@Service
public class CacheableOrderService implements OrderService {

    private final OrderService orderService;
    private final LoadingCache<UUID, List<OrderResponse>> allOrderCache;
    private final LoadingCache<TotalPriceCacheKey, TotalPriceResponse> totalPriceCache;

    public CacheableOrderService(OrderService orderService) {
        this.orderService = orderService;
        this.allOrderCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .refreshAfterWrite(12, TimeUnit.SECONDS)
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(orderService::getAllOrders);
        this.totalPriceCache = Caffeine.newBuilder()
                .maximumSize(3_000)
                .refreshAfterWrite(12, TimeUnit.SECONDS)
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(key -> orderService.getTotalPriceWithinRange(key.getFromDate(),
                        key.getToDate(), key.getUserId()));
    }

    @Override
    public List<OrderResponse> getAllOrders(UUID userId) {
        return allOrderCache.get(userId);
    }

    @Override
    public TotalPriceResponse getTotalPriceWithinRange(LocalDate fromDate, LocalDate toDate, UUID userId) {
        return totalPriceCache.get(new TotalPriceCacheKey(userId, fromDate, toDate));
    }

    @Override
    public OrderCreatedResponse save(UUID userId, OrderRequest request) {
        return orderService.save(userId, request);
    }

    private static final class TotalPriceCacheKey implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final UUID userId;
        private final LocalDate fromDate;
        private final LocalDate toDate;

        private TotalPriceCacheKey(UUID userId, LocalDate fromDate, LocalDate toDate) {
            this.userId = userId;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public UUID getUserId() {
            return userId;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TotalPriceCacheKey that = (TotalPriceCacheKey) o;
            return Objects.equals(userId, that.userId) &&
                   Objects.equals(fromDate, that.fromDate) &&
                   Objects.equals(toDate, that.toDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, fromDate, toDate);
        }
    }
}
