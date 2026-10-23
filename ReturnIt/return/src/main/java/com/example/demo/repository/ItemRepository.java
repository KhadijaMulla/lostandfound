
package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Search by title, description, or location
    List<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title,
            String description,
            String location
    );

    // Filter by status
    List<Item> findByStatusIgnoreCase(String status);

}
