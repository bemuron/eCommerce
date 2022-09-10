package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private CartController cartController = new CartController(userRepository, cartRepository, itemRepository);

    // test add to cart failure
    @Test
    public void testAddToCartFailure(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        // user not found
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // item not found
        when(userRepository.findByUsername("testUser")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    // test add to cart Success
    @Test
    public void testAddToCartSuccess(){
        User user = new User();
        user.setUsername("testUser");
        user.setCart(new Cart());

        Item item = new Item();
        item.setName("Pixel 4a");
        item.setId(1L);
        item.setPrice(new BigDecimal(50));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(4);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Cart cartRetrieved = responseEntity.getBody();
        assertEquals(BigDecimal.valueOf(200), cartRetrieved.getTotal());
    }

    // test remove from cart
    @Test
    public void testRemoveFromCartSuccess(){
        Cart cart = new Cart();
        User user = new User();
        user.setUsername("testUser");
        user.setCart(cart);

        Item item1 = new Item();
        item1.setName("Pixel 4a");
        item1.setId(1L);
        item1.setPrice(new BigDecimal(10));

        Item item2 = new Item();
        item2.setName("Galaxy Fold");
        item2.setId(2L);
        item2.setPrice(new BigDecimal(10));

        cart.addItem(item1);
        cart.addItem(item2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Cart cartRetrieved = responseEntity.getBody();
        assertEquals(1, cartRetrieved.getItems().size());
        assertEquals(Arrays.asList(item2), cartRetrieved.getItems());

    }

}