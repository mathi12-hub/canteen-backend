package com.canteen.canteenbackend.service;

import com.canteen.canteenbackend.dto.MenuItemRequest;
import com.canteen.canteenbackend.exception.ResourceNotFoundException;
import com.canteen.canteenbackend.model.MenuItem;
import com.canteen.canteenbackend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public List<MenuItem> getAllAvailableItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getByCategory(String category) {
        return menuItemRepository.findByCategoryIgnoreCase(category);
    }

    @Transactional(readOnly = true)
    public List<MenuItem> search(String keyword) {
        return menuItemRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    @Transactional
    public MenuItem create(MenuItemRequest request) {
        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .available(request.getAvailable() == null || request.getAvailable())
                .imageUrl(request.getImageUrl())
                .build();
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem update(Long id, MenuItemRequest request) {
        MenuItem item = getById(id);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
        item.setImageUrl(request.getImageUrl());
        return menuItemRepository.save(item);
    }

    @Transactional
    public void setAvailability(Long id, boolean available) {
        MenuItem item = getById(id);
        item.setAvailable(available);
        menuItemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        MenuItem item = getById(id);
        menuItemRepository.delete(item);
    }
}
