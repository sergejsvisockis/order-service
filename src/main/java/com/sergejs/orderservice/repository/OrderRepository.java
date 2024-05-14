package com.sergejs.orderservice.repository;

import com.sergejs.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findAllOrdersByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.orderDate " +
            "BETWEEN :fromDate AND :toDate " +
            "AND o.userId = :userId")
    BigDecimal getTotalPriceWithinRange(@Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate,
                                        @Param("userId") UUID userId);

}
