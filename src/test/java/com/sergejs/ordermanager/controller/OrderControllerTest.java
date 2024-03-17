package com.sergejs.ordermanager.controller;

import com.sergejs.ordermanager.OrderManagerApplication;
import com.sergejs.ordermanager.model.Order;
import com.sergejs.ordermanager.model.OrderItem;
import com.sergejs.ordermanager.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        OrderManagerApplication.class
})
@AutoConfigureMockMvc
public class OrderControllerTest {

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllOrders() throws Exception {
        UUID userId = UUID.fromString("5379d0bf-5572-4ee3-b0c7-64b6aab43240");
        LocalDate orderDate = LocalDate.now();

        Order order = new Order();
        order.setId(3L);
        order.setOrderDate(orderDate);
        order.setUserId(userId);

        UUID itemId = UUID.randomUUID();
        BigDecimal unitPrice = BigDecimal.valueOf(123);
        int quantity = 3;
        String itemName = "some item";

        OrderItem item = new OrderItem();
        item.setItemId(itemId);
        item.setUnitPrice(unitPrice);
        item.setOrder(order);
        item.setQuantity(quantity);
        item.setItemName(itemName);

        order.setOrderItems(List.of(item));
        when(orderRepository.findAllOrdersByUserId(any())).thenReturn(List.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
                        .header("user-id", userId))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].orderDate", is(orderDate.toString())))
                .andExpect(jsonPath("$.[0].orderItems[0].itemId", is(itemId.toString())))
                .andExpect(jsonPath("$.[0].orderItems[0].unitPrice", is(unitPrice.intValue())))
                .andExpect(jsonPath("$.[0].orderItems[0].quantity", is(quantity)))
                .andExpect(jsonPath("$.[0].orderItems[0].itemName", is(itemName)));
    }

    @Test
    void shouldGetTotalPriceWithRange() throws Exception {
        when(orderRepository.getTotalPriceWithinRange(any(), any(), any())).thenReturn(BigDecimal.valueOf(30_000));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/get-total-price")
                        .header("user-id", UUID.randomUUID())
                        .param("fromDate", "2024-01-16")
                        .param("toDate", "2024-03-16"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.totalPrice", is(30000)));
    }

    @Test
    void shouldSaveAnOrder() throws Exception {
        Order order = new Order();
        order.setId(3L);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        String requestBody = """
                {
                  "orderItems": [
                    {
                      "itemId": "3fa83f64-5717-4562-b3fc-2c963f66afa6",
                      "itemName": "someITem",
                      "unitPrice": 3789.9,
                      "quantity": 3
                    },
                    {
                      "itemId": "3fa83f64-5717-4512-b3fc-2c963f66afa6",
                      "itemName": "someItem",
                      "unitPrice": 3719.9,
                      "quantity": 9
                    },
                    {
                      "itemId": "1fa83f64-5717-4562-b3fc-2c963f66afa6",
                      "itemName": "someeITem",
                      "unitPrice": 3789.0,
                      "quantity": 1
                    }
                  ]
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                        .header("user-id", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(3)));
    }
}
