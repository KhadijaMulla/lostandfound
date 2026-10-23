
// package com.example.demo.controller;

// import com.example.demo.entity.Item;
// import com.example.demo.service.ItemService;
// import jakarta.validation.Valid;

// import org.springframework.data.domain.Page;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/items")
// public class ItemController {

//     private final ItemService itemService;

//     public ItemController(ItemService itemService) {
//         this.itemService = itemService;
//     }

//     // CREATE
//     @PostMapping
//     public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
//         Item savedItem = itemService.createItem(item);
//         return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
//     }

//     // READ: All items
//     @GetMapping
//     public List<Item> getAllItems() {
//         return itemService.getAllItems();
//     }

//     // READ: One item
//     @GetMapping("/{id}")
//     public Item getItemById(@PathVariable Long id) {
//         return itemService.getItemById(id);
//     }

//     // UPDATE
//     @PutMapping("/{id}")
//     public Item updateItem(
//             @PathVariable Long id,
//             @Valid @RequestBody Item item) {
//         return itemService.updateItem(id, item);
//     }

//     // DELETE
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
//         itemService.deleteItem(id);
//         return ResponseEntity.noContent().build();
//     }
    
//     // SEARCH: By title, description, or location
//     @GetMapping("/search")
//     public List<Item> searchItems(@RequestParam String keyword) {
//         return itemService.searchItems(keyword);
//     }
    
//     // FILTER: Get items by status
//     @GetMapping("/filter")
//     public List<Item> filterItemsByStatus(@RequestParam String status) {
//         return itemService.filterItemsByStatus(status);
//     }

   
    
//     // PAGINATION + SORTING
//     @GetMapping("/page")
//     public Page<Item> getItemsPaginated(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "5") int size,
//             @RequestParam(defaultValue = "id") String sortBy,
//             @RequestParam(defaultValue = "asc") String direction) {

//         return itemService.getItemsPaginated(
//                 page, size, sortBy, direction);
//     }

    
    
    


// }





package com.example.demo.controller;

import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import com.example.demo.service.ImageStorageService;

import jakarta.validation.Valid;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ImageStorageService imageStorageService;

    public ItemController(
            ItemService itemService,
            ImageStorageService imageStorageService) {
        this.itemService = itemService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(
            @Valid @RequestBody Item item) {
        Item savedItem = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedItem);
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PutMapping("/{id}")
    public Item updateItem(
            @PathVariable Long id,
            @Valid @RequestBody Item item) {
        return itemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Item> searchItems(
            @RequestParam String keyword) {
        return itemService.searchItems(keyword);
    }

    @GetMapping("/filter")
    public List<Item> filterItemsByStatus(
            @RequestParam String status) {
        return itemService.filterItemsByStatus(status);
    }

    @GetMapping("/page")
    public Page<Item> getItemsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return itemService.getItemsPaginated(
                page, size, sortBy, direction);
    }

    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Item> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image)
            throws IOException {

        Item item = itemService.getItemById(id);

        String filename = imageStorageService.store(image);

        item.setImageFileName(filename);

        Item savedItem = itemService.updateItem(id, item);

        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(
            @PathVariable Long id) throws IOException {

        Item item = itemService.getItemById(id);

        if (item.getImageFileName() == null ||
                item.getImageFileName().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path imagePath = imageStorageService.getImagePath(
                item.getImageFileName());

        if (!Files.isRegularFile(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        String filename = item.getImageFileName();
        MediaType mediaType;

        if (filename.endsWith(".jpg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".webp")) {
            mediaType = MediaType.parseMediaType("image/webp");
        } else {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new FileSystemResource(imagePath);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(resource);
    }
}
