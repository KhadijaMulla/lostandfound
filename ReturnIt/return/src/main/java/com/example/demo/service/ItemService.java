
// package com.example.demo.service;

// import com.example.demo.entity.Item;
// import com.example.demo.repository.ItemRepository;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.web.server.ResponseStatusException;

// import java.util.List;

// @Service
// public class ItemService {

//     private final ItemRepository itemRepository;

//     public ItemService(ItemRepository itemRepository) {
//         this.itemRepository = itemRepository;
//     }

//     // READ: Get all items
//     public List<Item> getAllItems() {
//         return itemRepository.findAll();
//     }

//     // READ: Get item by ID
//     public Item getItemById(Long id) {
//         return itemRepository.findById(id)
//                 .orElseThrow(() -> new ResponseStatusException(
//                         HttpStatus.NOT_FOUND,
//                         "Item not found with ID: " + id));
//     }

//     // CREATE: Save a new item
//     public Item createItem(Item item) {
//         return itemRepository.save(item);
//     }

//     // UPDATE: Update an existing item
//     public Item updateItem(Long id, Item updatedItem) {
//         Item existingItem = getItemById(id);

//         existingItem.setTitle(updatedItem.getTitle());
//         existingItem.setDescription(updatedItem.getDescription());
//         existingItem.setLocation(updatedItem.getLocation());
//         existingItem.setStatus(updatedItem.getStatus());

//         return itemRepository.save(existingItem);
//     }

//     // DELETE: Delete an item
//     public void deleteItem(Long id) {
//         Item existingItem = getItemById(id);
//         itemRepository.delete(existingItem);
//     }

//     // SEARCH: By title, description, or location
//     public List<Item> searchItems(String keyword) {
//         return itemRepository
//                 .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(
//                         keyword, keyword, keyword);
//     }

//     // FILTER: Get items by status
//     public List<Item> filterItemsByStatus(String status) {
//         return itemRepository.findByStatusIgnoreCase(status);
//     }

//     // PAGINATION: Get items page by page
    
//     // PAGINATION + SORTING
//     public Page<Item> getItemsPaginated(
//             int page, int size, String sortBy, String direction) {

//         Sort sort = direction.equalsIgnoreCase("desc")
//                 ? Sort.by(sortBy).descending()
//                 : Sort.by(sortBy).ascending();

//         Pageable pageable = PageRequest.of(page, size, sort);

//         return itemRepository.findAll(pageable);
//     }

// }




package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // READ: Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // READ: Get item by ID
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found with ID: " + id));
    }

    // CREATE: Save a new item
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    // UPDATE: Update an existing item
    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItemById(id);

        existingItem.setTitle(updatedItem.getTitle());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setLocation(updatedItem.getLocation());
        existingItem.setStatus(updatedItem.getStatus());

        // Save image filename without accidentally clearing it
        if (updatedItem.getImageFileName() != null) {
            existingItem.setImageFileName(
                    updatedItem.getImageFileName());
        }

        return itemRepository.save(existingItem);
    }

    // DELETE: Delete an item
    public void deleteItem(Long id) {
        Item existingItem = getItemById(id);
        itemRepository.delete(existingItem);
    }

    // SEARCH: By title, description, or location
    public List<Item> searchItems(String keyword) {
        return itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(
                        keyword, keyword, keyword);
    }

    // FILTER: Get items by status
    public List<Item> filterItemsByStatus(String status) {
        return itemRepository.findByStatusIgnoreCase(status);
    }

    // PAGINATION + SORTING
    public Page<Item> getItemsPaginated(
            int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return itemRepository.findAll(pageable);
    }
}
