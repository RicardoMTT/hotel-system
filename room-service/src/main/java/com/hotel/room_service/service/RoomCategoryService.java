package com.hotel.room_service.service;


import com.hotel.room_service.domain.RoomCategory;
import com.hotel.room_service.dto.CreateRoomCategoryRequest;
import com.hotel.room_service.dto.RoomCategoryResponse;
import com.hotel.room_service.repository.RoomCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoomCategoryService {

    private final RoomCategoryRepository repository;

    public RoomCategoryService(RoomCategoryRepository repository) {
        this.repository = repository;
    }

    public RoomCategoryResponse create(CreateRoomCategoryRequest request) {
        RoomCategory category = new RoomCategory();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setBasePrice(request.basePrice());
        category.setMaxGuests(request.maxGuests());

        repository.save(category);

        return new RoomCategoryResponse(
                category.getId(),
                category.getName(),
                category.getBasePrice(),
                category.getMaxGuests()
        );
    }

    public List<RoomCategoryResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(c -> new RoomCategoryResponse(
                        c.getId(),
                        c.getName(),
                        c.getBasePrice(),
                        c.getMaxGuests()
                ))
                .toList();
    }
}
