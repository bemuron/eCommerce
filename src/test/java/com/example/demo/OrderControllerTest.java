package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class OrderControllerTest {
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private OrderController orderController = new OrderController(userRepository, orderRepository);

    //test submit order
    @Test
    public void testSubmitOrder(){
        // user not found
        when(userRepository.findByUsername("empty")).thenReturn(null);
        ResponseEntity<UserOrder> responseEntity = orderController.submit("empty");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // submit success
        // setup
        Item item = new Item();
        item.setName("apple");
        item.setPrice(new BigDecimal(10));

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);
        user.setCart(cart);

        cart.setUser(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setItems(user.getCart().getItems());
        order.setTotal(user.getCart().getTotal());

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        responseEntity = orderController.submit("testUser");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        UserOrder userOrderCreated = responseEntity.getBody();
        assertEquals(user.getUsername(), userOrderCreated.getUser().getUsername());
        assertEquals(1, userOrderCreated.getItems().size());
        assertEquals("apple", userOrderCreated.getItems().get(0).getName());
    }

    //test get orders for user
    @Test
    public void testGetOrdersForUser(){
        // setup
        Item item = new Item();
        item.setId(1L);
        item.setName("apple");
        item.setPrice(new BigDecimal(10));

        Cart cart = new Cart();
        cart.setId(2L);
        cart.addItem(item);
        cart.setTotal(item.getPrice());

        User user = new User();
        user.setUsername("testUser");
        user.setId(3L);
        user.setCart(cart);
        cart.setUser(user);

        UserOrder order = new UserOrder();
        order.setId(4L);
        order.setUser(user);
        order.setItems(user.getCart().getItems());
        order.setTotal(user.getCart().getTotal());

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(order));

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<UserOrder> orderHistory = responseEntity.getBody();
        assertEquals(1, orderHistory.size());
        assertEquals(order, orderHistory.get(0));
        assertEquals(user.getUsername(), orderHistory.get(0).getUser().getUsername());
        assertEquals(item, orderHistory.get(0).getItems().get(0));
    }
}
