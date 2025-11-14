package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.FloorElementRequest;
import com.example.restaurant_management.dto.response.FloorElementResponse;
import com.example.restaurant_management.entity.FloorElement;
import com.example.restaurant_management.entity.Location;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.FloorElementRepository;
import com.example.restaurant_management.repository.LocationRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.FloorElementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FloorElementServiceImpl implements FloorElementService {
private final FloorElementRepository repository;
private final TableRepository tableRepository;
private final LocationRepository locationRepository;

    private FloorElementResponse toResponse(FloorElement e) {
        return FloorElementResponse.builder()
                .id(e.getId().toString())
                .type(e.getType())
                .x(e.getX())
                .y(e.getY())
                .width(e.getWidth())
                .height(e.getHeight())
                .rotation(e.getRotation())
                .color(e.getColor())
                .label(e.getLabel())
                // check null cho table
                .tableId(e.getTable() != null ? e.getTable().getId() : null)
                // check null cho location
                .floor(e.getLocation() != null ? e.getLocation().getName() : null)
                .build();
    }



    private FloorElement toEntity(FloorElementRequest r) {
        System.out.println(r.toString());
        // Lấy table (nếu có)
        TableEntity table = null;
        if (r.getTableId() != null) {
            table = tableRepository.findById(r.getTableId()).orElse(null);
        }

        // Lấy location (nếu có)
        Location location = null;
        if (r.getFloor() != null) {
            location = locationRepository.findByName(r.getFloor()).orElse(null);
        }
        FloorElement e;
        if(r.getId() != null) {
             e = repository.findById(r.getId()).get();
        }else{
            e = new FloorElement();
        }

        e.setY(r.getY());
        e.setX(r.getX());
        e.setRotation(r.getRotation());
        e.setColor(r.getColor());
        e.setLabel(r.getLabel());
        e.setTable(table);
        e.setLocation(location);
        e.setWidth(r.getWidth());
        e.setHeight(r.getHeight());
        e.setType(r.getType());
        return e;
    }



@Override
public List<FloorElementResponse> getAll() {
return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
}


@Override
public FloorElementResponse getById(String id) {
return repository.findById(id).map(this::toResponse).orElse(null);
}


@Override
public FloorElementResponse create(FloorElementRequest request) {
FloorElement entity = toEntity(request);
return toResponse(repository.save(entity));
}

    @Override
    public FloorElementResponse update(String id, FloorElementRequest request) {
        System.out.println(toEntity(request).toString());
        return toResponse(repository.save(toEntity(request)));
    }


    @Override
public void delete(String id) {
    repository.deleteById(id);
}
}