package com.example.restaurant_management.booking_test;

import com.example.restaurant_management.dto.request.BookingRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.BookingMapper;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.impl.BookingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingServiceImpl bookingService;
    private MockEmailExporter mockEmailExporter;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BookingMapper bookingMapper;

    private BookingRequest bookingRequest;
    private TableEntity table;
    private Customer customer;
    private User user;
    private Booking booking;

    private static final File EMAIL_FILE = new File("mock_email.html");

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // ensure no leftover file from previous runs
        if (EMAIL_FILE.exists()) {
            Files.delete(EMAIL_FILE.toPath());
        }

        mockEmailExporter = new MockEmailExporter(); // same instance injected into service

        bookingService = new BookingServiceImpl(
                bookingRepository,
                tableRepository,
                userRepository,
                bookingMapper,
                customerRepository,
                mockEmailExporter
        );

        table = TableEntity.builder()
                .tableNumber("T1")
                .capacity(4)
                .status("Available")
                .build();
        table.setId(1L);

        user = User.builder().username("user1").build();
        user.setId(1L);

        customer = Customer.builder()
                .fullName("Minh")
                .phoneNumber("0123456789")
                .email(null) // test cases will set email when needed
                .user(user)
                .build();
        customer.setId(1L);

        bookingRequest = BookingRequest.builder()
                .tableIds(List.of(1L))
                .customerName("Minh")
                .customerPhone("0123456789")
                .customerEmail("duonghongminhfpt@gmail.com")
                .numGuests(2)
                .bookingTime(LocalDateTime.now().plusDays(1))
                .status("Pending")
                .build();

        booking = Booking.builder()
                .customerName("Minh")
                .customerPhone("0123456789")
                .customerEmail("duonghongminhfpt@gmail.com")
                .numGuests(2)
                .bookingTime(bookingRequest.getBookingTime())
                .status("Pending")
                .tables(List.of(table))
                .customerUser(user)
                .build();
        booking.setId(1L);
    }

    @AfterEach
    void tearDown() throws IOException {
        // cleanup email file to avoid cross-test contamination
        if (EMAIL_FILE.exists()) {
            Files.delete(EMAIL_FILE.toPath());
        }
    }

    // ========== CREATE BOOKING (some tests) ==========

    @Test
    void testCreateBooking_Success_NewCustomer() {
        // setup: no existing customer -> will create new
        when(customerRepository.findByPhoneNumber("0123456789")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(anyLong(), any())).thenReturn(List.of());
        when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());

        BookingResponse response = bookingService.createBooking(bookingRequest);

        assertNotNull(response);
        // email: customer.email is null in setup -> service will not call emailService (so file not created)
        assertFalse(EMAIL_FILE.exists());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_Success_ExistingCustomer() {
        // existing customer (no new save), customer may have no email
        when(customerRepository.findByPhoneNumber("0123456789")).thenReturn(Optional.of(customer));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(anyLong(), any())).thenReturn(List.of());
        when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());

        BookingResponse response = bookingService.createBooking(bookingRequest);
        assertNotNull(response);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_TableAlreadyBooked() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, bookingRequest.getBookingTime())).thenReturn(List.of(booking));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.createBooking(bookingRequest));
        assertTrue(ex.getMessage().contains("already has a booking"));
    }

    // ====== FAILING TEST 1: Table Not Found -> expect EntityNotFoundException ======
    @Test
    void testCreateBooking_TableNotFound() {
        // ensure repository returns empty -> service should throw EntityNotFoundException
        when(tableRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingRequest));
    }

    // ====== FAILING TEST 2: Invalid Booking Time -> expect IllegalArgumentException ======
    @Test
    void testCreateBooking_InvalidBookingTime() {
        // set bookingTime null (invalid) -> validateBookingRequest should throw IllegalArgumentException
        bookingRequest.setBookingTime(null);

        // Note: because validateBookingRequest runs at start, we don't need to mock tableRepository
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    // ====== FAILING TEST 3: Email failure simulation ======
    @Test
    void testCreateBooking_EmailFailed() {
        // prepare existing customer but with email set -> service will attempt to send email
        customer.setEmail("a@b.com"); // provide email so service will call emailService
        when(customerRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(customer));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(anyLong(), any())).thenReturn(List.of());
        when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse());

        // Use the same mockEmailExporter instance injected in bookingService
        mockEmailExporter.setSimulateFailure(true); // simulate failure -> should NOT create file

        BookingResponse response = bookingService.createBooking(bookingRequest);
        assertNotNull(response);

        // Ensure file NOT created due to simulated failure
        assertFalse(EMAIL_FILE.exists(), "mock_email.html should NOT exist when simulateFailure=true");
    }

    // ========== DELETE BOOKING ==========

    @Test
    void testDeleteBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertDoesNotThrow(() -> bookingService.deleteBooking(1L));
        verify(bookingRepository).delete(booking);
        verify(tableRepository, atLeastOnce()).save(any(TableEntity.class));
    }

    @Test
    void testDeleteBooking_Completed_ThrowsException() {
        booking.setStatus("Completed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.deleteBooking(1L));
        assertTrue(ex.getMessage().contains("Cannot delete a completed booking"));
    }
}
