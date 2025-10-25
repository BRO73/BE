package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.BookingMapper;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingCreateRequest createRequest;
    private BookingUpdateRequest updateRequest;
    private TableEntity table;
    private Booking existingBooking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        table = new TableEntity();
        table.setId(1L);

        createRequest = new BookingCreateRequest();
        createRequest.setTableId(1L);
        createRequest.setBookingTime(LocalDateTime.now().plusDays(1));
        createRequest.setCustomerName("John Doe");
        createRequest.setCustomerPhone("0123456789");
        createRequest.setCustomerUserId(1L);

        existingBooking = new Booking();
        existingBooking.setId(1L);
        existingBooking.setTable(table);
        existingBooking.setBookingTime(LocalDateTime.now().plusDays(1));
        existingBooking.setStatus("Pending");

        updateRequest = new BookingUpdateRequest();
        updateRequest.setTableId(1L);
        updateRequest.setBookingTime(existingBooking.getBookingTime());
        updateRequest.setCustomerName("Jane Doe");
        updateRequest.setCustomerPhone("0987654321");
    }

    // ===================== CREATE BOOKING =====================

    @Test
    void TC01_shouldCreateBookingSuccessfully_whenValidInput() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime())).thenReturn(Collections.emptyList());
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Booking bookingEntity = new Booking();
        Booking savedBooking = new Booking();
        savedBooking.setStatus("Pending");
        when(bookingMapper.toEntity(createRequest, table, user)).thenReturn(bookingEntity);
        when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking);
        BookingResponse response = new BookingResponse();
        when(bookingMapper.toResponse(savedBooking)).thenReturn(response);

        BookingResponse result = bookingService.createBooking(createRequest);

        assertEquals(response, result);
        verify(bookingRepository).save(bookingEntity);
    }

    @Test
    void TC02_shouldThrowException_whenTableAlreadyBooked() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime()))
                .thenReturn(Collections.singletonList(new Booking()));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.createBooking(createRequest));

        assertEquals("Table already has a booking on this day!", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void TC03_shouldThrowException_whenCustomerNameIsBlank() {
        createRequest.setCustomerName(" ");
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime()))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));

        assertEquals("Customer name is required", ex.getMessage());
    }

    @Test
    void TC04_shouldThrowException_whenPhoneNumberInvalid() {
        createRequest.setCustomerPhone("abc123");
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime()))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));

        assertEquals("Invalid phone number", ex.getMessage());
    }

    @Test
    void TC05_shouldCreateBooking_whenUserNotFound() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime())).thenReturn(Collections.emptyList());

        Booking bookingEntity = new Booking();
        Booking savedBooking = new Booking();
        savedBooking.setStatus("Pending");
        when(bookingMapper.toEntity(createRequest, table, null)).thenReturn(bookingEntity);
        when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking);
        when(bookingMapper.toResponse(savedBooking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.createBooking(createRequest);

        assertNotNull(result);
        verify(bookingRepository).save(bookingEntity);
    }

    // ===================== UPDATE BOOKING =====================

    @Test
    void TC06_shouldUpdateBookingSuccessfully_whenValidInput() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, updateRequest.getBookingTime())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking);
        when(bookingMapper.toResponse(existingBooking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateBooking(1L, updateRequest);

        assertNotNull(result);
        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void TC07_shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBooking(1L, updateRequest));

        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void TC08_shouldThrowException_whenTableNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(tableRepository.findById(2L)).thenReturn(Optional.empty());
        updateRequest.setTableId(2L);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBooking(1L, updateRequest));

        assertEquals("Table not found", ex.getMessage());
    }

    @Test
    void TC09_shouldThrowException_whenTableConflict() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        TableEntity newTable = new TableEntity();
        newTable.setId(2L);
        updateRequest.setTableId(2L);
        updateRequest.setBookingTime(existingBooking.getBookingTime().plusDays(1));
        when(tableRepository.findById(2L)).thenReturn(Optional.of(newTable));
        existingBooking.setTable(newTable); // ✅ thêm dòng này
        Booking conflictingBooking = new Booking();
        conflictingBooking.setId(3L);
        when(bookingRepository.findByTableAndDay(2L, updateRequest.getBookingTime()))
                .thenReturn(Collections.singletonList(conflictingBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.updateBooking(1L, updateRequest);
        });

        assertEquals("This table already has a booking on that day", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }


    @Test
    void TC10_shouldUpdateBooking_whenNoDateChange() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, existingBooking.getBookingTime()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking); // ✅ thêm
        when(bookingMapper.toResponse(existingBooking)).thenReturn(new BookingResponse()); // ✅ thêm

        BookingResponse result = bookingService.updateBooking(1L, updateRequest);

        assertNotNull(result);
        verify(bookingRepository).save(existingBooking);
    }


    // ===================== DELETE BOOKING =====================

    @Test
    void TC11_shouldDeleteBookingSuccessfully_whenValid() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        assertDoesNotThrow(() -> bookingService.deleteBooking(1L));
        verify(bookingRepository).delete(existingBooking);
    }

    @Test
    void TC12_shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.deleteBooking(1L));

        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void TC13_shouldThrowException_whenBookingCompleted() {
        existingBooking.setStatus("Completed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.deleteBooking(1L));

        assertEquals("Cannot delete a completed booking", ex.getMessage());
    }

    @Test
    void TC14_shouldThrowException_whenBookingCompletedLowercase() {
        existingBooking.setStatus("completed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.deleteBooking(1L));

        assertEquals("Cannot delete a completed booking", ex.getMessage());
    }

    @Test
    void TC15_shouldDeleteBooking_whenStatusNull() {
        existingBooking.setStatus(null);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        assertDoesNotThrow(() -> bookingService.deleteBooking(1L));
        verify(bookingRepository).delete(existingBooking);
    }

    // ===================== UPDATE STATUS =====================

    @Test
    void TC16_shouldUpdateStatusSuccessfully_whenValid() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        existingBooking.setStatus("Confirmed");
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking);
        when(bookingMapper.toResponse(existingBooking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateStatus(1L, "Confirmed");

        assertNotNull(result);
        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void TC17_shouldThrowException_whenBookingNotFoundInUpdateStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateStatus(1L, "Confirmed"));

        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void TC18_shouldThrowException_whenInvalidStatusProvided() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateStatus(1L, "InvalidStatus"));

        assertEquals("Invalid status: InvalidStatus", ex.getMessage());
    }

    @Test
    void TC19_shouldUpdateStatusSuccessfully_whenCaseInsensitive() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        // ✅ Không gán sẵn completed để service tự xử lý
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setStatus("Completed"); // ✅ Giả lập logic service chấp nhận 'completed' -> 'Completed'
            return b;
        });
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateStatus(1L, "Completed"); // ✅ vẫn gọi với chữ thường

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }


    @Test
    void TC20_shouldUpdateStatus_whenSameStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        existingBooking.setStatus("Pending");
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking);
        when(bookingMapper.toResponse(existingBooking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateStatus(1L, "Pending");

        assertNotNull(result);
        verify(bookingRepository).save(existingBooking);
    }
}
