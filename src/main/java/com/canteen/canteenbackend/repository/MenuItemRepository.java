package com.canteen.canteenbackend.repository;

import com.canteen.canteenbackend.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByCategoryIgnoreCase(String category);

    List<MenuItem> findByNameContainingIgnoreCase(String name);
}
