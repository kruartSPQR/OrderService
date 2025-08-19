package com.innowise.OrderService.unitTests;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.exception.exceptions.ResourceNotFoundCustomException;
import com.innowise.OrderService.mapper.ItemMapper;
import com.innowise.OrderService.repository.ItemRepository;
import com.innowise.OrderService.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private ItemRequestDto itemRequestDto;
    private Item itemEntity;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("TestItem");
        itemRequestDto.setPrice(100.0);

        itemEntity = new Item();
        itemEntity.setName("TestItem");
        itemEntity.setPrice(100.0);

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setName("TestItem");
        itemResponseDto.setPrice(100.0);
        itemResponseDto.setId(1L);
    }

    @Test
    void testCreateItem() {
        when(itemMapper.toEntity(itemRequestDto)).thenReturn(itemEntity);
        when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);

        ItemResponseDto result = itemService.createItem(itemRequestDto);

        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getPrice(), result.getPrice());

        verify(itemMapper).toEntity(itemRequestDto);
        verify(itemRepository).save(itemEntity);
        verify(itemMapper).toDto(itemEntity);
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemEntity));
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);

        ItemResponseDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getPrice(), result.getPrice());
    }

    @Test
    void testGetItemsByIds() {
        List<Long> ids = List.of(1L, 2L);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setPrice(200.0);

        List<Item> items = List.of(itemEntity, item2);

        ItemResponseDto itemResponseDto2 = new ItemResponseDto();
        itemResponseDto2.setId(2L);
        itemResponseDto2.setName("Item2");
        itemResponseDto2.setPrice(200.0);

        when(itemRepository.findAllById(ids)).thenReturn(items);
        when(itemMapper.toDto(itemEntity)).thenReturn(itemResponseDto);
        when(itemMapper.toDto(item2)).thenReturn(itemResponseDto2);

        List<ItemResponseDto> result = itemService.getItemsByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemResponseDto.getName(), result.get(0).getName());
        assertEquals(itemResponseDto2.getName(), result.get(1).getName());
    }

    @Test
    void testUpdateItem() {
        Long itemId = 1L;
        ItemRequestDto itemUpdate = new ItemRequestDto();
        itemUpdate.setName("UpdatedItem");
        itemUpdate.setPrice(150.0);

        ItemResponseDto updatedItemResponseDto = new ItemResponseDto();
        updatedItemResponseDto.setId(itemId);
        updatedItemResponseDto.setName(itemUpdate.getName());
        updatedItemResponseDto.setPrice(itemUpdate.getPrice());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
        when(itemMapper.toDto(itemEntity)).thenReturn(updatedItemResponseDto);

        ItemResponseDto result = itemService.updateItem(itemId, itemUpdate);

        assertEquals(itemUpdate.getName(), itemEntity.getName());
        assertEquals(itemUpdate.getPrice(), itemEntity.getPrice());

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(itemEntity);
        verify(itemMapper).toDto(itemEntity);
    }

    @Test
    void testDeleteItem() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));

        itemService.deleteItem(itemId);

        verify(itemRepository).findById(itemId);
        verify(itemRepository).delete(itemEntity);
    }

    @Test
    void testGetItemByIdWithNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundCustomException exception = assertThrows(ResourceNotFoundCustomException.class, () -> {
            itemService.getItemById(1L);
        });

        assertEquals("Item not found with id: 1", exception.getMessage());
    }
}