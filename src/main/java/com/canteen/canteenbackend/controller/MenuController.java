package com.canteen.canteenbackend.controller;

import com.canteen.canteenbackend.dto.MenuItemRequest;
import com.canteen.canteenbackend.model.MenuItem;
import com.canteen.canteenbackend.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Public menu browsing endpoints, plus staff/admin management endpoints.
 * Base path: /api/menu
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /** GET /api/menu             -> all available items
     *  GET /api/menu?category=X  -> items in a category
     *  GET /api/menu?search=X    -> items matching a name keyword
     *  GET /api/menu?all=true    -> every item, including unavailable (staff view)
     */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") boolean all) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(menuService.search(search));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(menuService.getByCategory(category));
        }
        return ResponseEntity.ok(all ? menuService.getAllItems() : menuService.getAllAvailableItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        MenuItem created = menuService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id,
                                                     @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuService.update(id, request));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> setAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean available = Boolean.TRUE.equals(body.get("available"));
        menuService.setAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
