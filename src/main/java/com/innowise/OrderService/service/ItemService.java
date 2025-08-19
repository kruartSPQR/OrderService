package com.innowise.OrderService.service;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.mapper.ItemMapper;
import com.innowise.OrderService.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemResponseDto createItem(ItemRequestDto requestDto) {
        Item item = itemMapper.toEntity(requestDto);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    public ItemResponseDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found with id: " + id));
        return itemMapper.toDto(item);
    }

    public List<ItemResponseDto> getItemsByIds(List<Long> ids) {
        List<Item> items = itemRepository.findAllById(ids);
        return items.stream()
                .map(item -> itemMapper.toDto(item))
                .toList();
    }

    @Transactional
    public ItemResponseDto updateItem(Long id, ItemRequestDto requestDto) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found with id: " + id));

        existingItem.setName(requestDto.getName());
        existingItem.setPrice(requestDto.getPrice());

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toDto(updatedItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found with id: " + id));

        itemRepository.delete(item);
    }
}