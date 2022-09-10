package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ItemControllerTest {

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private ItemController itemController = new ItemController(itemRepository);

    //getItemByName
    @Test
    public void testGetItemByName(){
        List<Item> itemList = createItemList();
        // failure - name does not exist
        when(itemRepository.findByName("empty")).thenReturn(null);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("empty");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // success
        when(itemRepository.findByName("apple")).thenReturn(itemList);
        responseEntity = itemController.getItemsByName("apple");

        List<Item> itemListRetrieved = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("apple", itemListRetrieved.get(0).getName());
    }

    private List<Item> createItemList(){
        Item item = new Item();
        item.setName("apple");

        return Collections.singletonList(item);
    }
}
