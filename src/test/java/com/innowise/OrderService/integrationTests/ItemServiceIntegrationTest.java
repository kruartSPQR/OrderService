package com.innowise.OrderService.integrationTests;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.service.ItemService;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemService itemService;

    private ItemRequestDto createTestItemDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setName("TestItem");
        dto.setPrice(100.0);
        return dto;
    }

    @Test
    @DirtiesContext
    void shouldCreateItem() {
        ItemRequestDto request = createTestItemDto();
        ItemResponseDto response = itemService.createItem(request);

        assertNotNull(response.getId());
        assertEquals("TestItem", response.getName());

        ItemResponseDto byId = itemService.getItemById(response.getId());
        assertEquals(response.getPrice(), byId.getPrice());
    }

    @Test
    @DirtiesContext
    void shouldUpdateItem() {
        ItemResponseDto created = itemService.createItem(createTestItemDto());

        ItemRequestDto updateRequest = createTestItemDto();
        updateRequest.setName("UpdatedItem");
        updateRequest.setPrice(150.0);

        ItemResponseDto updated = itemService.updateItem(created.getId(), updateRequest);

        assertEquals("UpdatedItem", updated.getName());
        assertEquals(150.0, updated.getPrice());
    }

    @Test
    @DirtiesContext
    void shouldDeleteItem() {
        ItemResponseDto created = itemService.createItem(createTestItemDto());
        itemService.deleteItem(created.getId());

        assertThrows(ResourceNotFoundCustomException.class,
                () -> itemService.getItemById(created.getId()));
    }

    @Test
    @DirtiesContext
    void shouldGetItems() {
        ItemRequestDto item1 = createTestItemDto();
        item1.setName("Item1");

        ItemRequestDto item2 = createTestItemDto();
        item2.setName("Item2");

        ItemResponseDto created1 = itemService.createItem(item1);
        ItemResponseDto created2 = itemService.createItem(item2);

        List<ItemResponseDto> items = itemService.getItemsByIds(List.of(created1.getId(), created2.getId()));

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(u -> u.getName().equals("Item1")));
        assertTrue(items.stream().anyMatch(u -> u.getName().equals("Item2")));
    }

    @Test
    @DirtiesContext
    void shouldGiveExceptionWhenItemNotFound() {
        assertThrows(ResourceNotFoundCustomException.class,
                () -> itemService.getItemById(999L));
    }
}