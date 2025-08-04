package com.example.Order_Service.service;

import com.example.Order_Service.dto.InventoryResponse;
import com.example.Order_Service.dto.OrderLineItemsDto;
import com.example.Order_Service.dto.OrderRequest;
import com.example.Order_Service.model.Order;
import com.example.Order_Service.model.OrderLineItems;
import com.example.Order_Service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToEntity).toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodes = orderLineItemsList.stream().map(OrderLineItems::getSkuCode).toList();
        InventoryResponse[] inventoryResponses = webClient.get().uri("http://localhost:8082/api/inventory/", UriBuilder -> UriBuilder.queryParam("skuCode", skuCodes).build()).retrieve().bodyToMono(InventoryResponse[].class).block();
        boolean allMatch = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if(allMatch){
            orderRepository.save(order);
        }
        else{
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
