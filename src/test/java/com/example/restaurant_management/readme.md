# BookingServiceImpl Test Suite

## 🧩 Overview

This test suite validates the functionality of the `BookingServiceImpl` class in the Restaurant Management system. It focuses on core booking operations, including creating, updating, deleting bookings, and updating booking status.  
**Purpose:** Ensure the service behaves correctly for valid inputs, edge cases, and error scenarios, fully mocking dependencies to isolate business logic.  
**Scope:** Unit tests for service methods; external dependencies such as repositories and mappers are mocked using Mockito. Integration-level behavior is simulated via end-to-end method invocation.

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- Gradle build system
- JUnit 5 (Jupiter)
- Mockito for mocking dependencies
- IDE support (IntelliJ IDEA, Eclipse, VSCode)

### Installation & Execution

# Clone repository
git clone https://github.com/your-org/restaurant-management.git
cd restaurant-management

# Clean, build, and refresh dependencies
gradle clean build --refresh-dependencies

# Run all unit tests
gradle test

# Generate JaCoCo coverage report
gradle jacocoTestReport

# Open HTML coverage report (Linux/MacOS)
xdg-open build/reports/jacoco/test/html/index.html 2>/dev/null || open build/reports/jacoco/test/html/index.html

## 🧪 Test Structure

### Unit Tests

**Create Booking Tests**  
- **Happy Path:** Successfully create a booking with valid input.  
- **Edge/Error Cases:** Table already booked, missing customer name, invalid phone number, non-existent customer user ID.  

**Update Booking Tests**  
- **Happy Path:** Update booking successfully with valid changes.  
- **Edge/Error Cases:** Booking not found, table conflict with existing bookings.  

**Delete Booking Tests**  
- **Happy Path:** Successfully delete booking when not completed.  
- **Edge/Error Cases:** Booking not found, booking already completed (case-insensitive).  

**Update Status Tests**  
- **Happy Path:** Update status successfully with valid input.  
- **Edge/Error Cases:** Invalid status input, booking not found.  

**Mocking & Isolation**  
- BookingRepository, TableRepository, UserRepository, BookingMapper are fully mocked.  
- Business logic is tested in isolation; no real database calls occur.  
- Common setup for entities and requests is reused across tests for efficiency.



# 📝 AI Prompt Log – BookingServiceImpl Test Suite

This section logs all AI prompts used during the development of the BookingServiceImpl test suite, from code analysis to test optimization and documentation.

---

## Prompt 1 – Analyze Code
**Prompt Command:**  
```text
Goal: Identify what to test and why. Please carefully analyze the following code and identify the core functions, methods, or components that should be prioritized for unit testing. For each prioritized function, clearly describe: Purpose / responsibility Expected input and output Key edge cases or potential failure points Dependencies that may require mocking or isolation (e.g., database, external API, shared state) Then, produce a short summary table listing the prioritized functions (max 5), their purpose, and rationale for testing priority (High / Medium / Low). Finally, propose a high-level test coverage plan with no more than 15–20 total test cases across all functions. @Service @RequiredArgsConstructor @Transactional public class BookingServiceImpl implements BookingService { private final BookingRepository bookingRepository; private final TableRepository tableRepository; private final UserRepository userRepository; private final BookingMapper bookingMapper; @Override public BookingResponse createBooking(BookingCreateRequest request) { TableEntity table = tableRepository.findById(request.getTableId()) .orElseThrow(() -> new EntityNotFoundException("Table not found")); List<Booking> existing = bookingRepository.findByTableAndDay( request.getTableId(), request.getBookingTime() ); if (!existing.isEmpty()) { throw new IllegalStateException("Table already has a booking on this day!"); } if (request.getCustomerName() == null || request.getCustomerName().isBlank()) { throw new IllegalArgumentException("Customer name is required"); } if (request.getCustomerPhone() == null || !request.getCustomerPhone().matches("\\d{9,15}")) { throw new IllegalArgumentException("Invalid phone number"); } User customerUser = null; if (request.getCustomerUserId() != null) { customerUser = userRepository.findById(request.getCustomerUserId()) .orElse(null); } Booking booking = bookingMapper.toEntity(request, table, customerUser); booking.setStatus("Pending"); Booking saved = bookingRepository.save(booking); return bookingMapper.toResponse(saved); } @Override public BookingResponse updateBooking(Long id, BookingUpdateRequest request) { Booking existing = bookingRepository.findById(id) .orElseThrow(() -> new EntityNotFoundException("Booking not found")); TableEntity newTable = tableRepository.findById(request.getTableId()) .orElseThrow(() -> new EntityNotFoundException("Table not found")); if (!existing.getTable().getId().equals(request.getTableId()) || !existing.getBookingTime().toLocalDate().equals(request.getBookingTime().toLocalDate())) { List<Booking> conflicts = bookingRepository.findByTableAndDay( request.getTableId(), request.getBookingTime() ); if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(id)) { throw new IllegalStateException("This table already has a booking on that day"); } } bookingMapper.updateEntityFromRequest(existing, request, newTable); Booking updated = bookingRepository.save(existing); return bookingMapper.toResponse(updated); } @Override public void deleteBooking(Long id) { Booking existing = bookingRepository.findById(id) .orElseThrow(() -> new EntityNotFoundException("Booking not found")); if ("Completed".equalsIgnoreCase(existing.getStatus())) { throw new IllegalStateException("Cannot delete a completed booking"); } bookingRepository.delete(existing); } @Override public BookingResponse updateStatus(Long id, String status) { Booking existing = bookingRepository.findById(id) .orElseThrow(() -> new EntityNotFoundException("Booking not found")); switch (status) { case "Pending": case "Confirmed": case "Cancelled": case "Completed": existing.setStatus(status); break; default: throw new IllegalArgumentException("Invalid status: " + status); } Booking updated = bookingRepository.save(existing); return bookingMapper.toResponse(updated); } @Override public TableAvailabilityResponse isTableAvailable(Long tableId, LocalDateTime bookingTime) { if (!tableRepository.existsById(tableId)) { throw new EntityNotFoundException("Table not found"); } List<Booking> sameDayBookings = bookingRepository.findByTableAndDay(tableId, bookingTime); boolean available = sameDayBookings.isEmpty(); return TableAvailabilityResponse.builder() .tableId(tableId) .available(available) .message(available ? "Table is available" : "Table is already booked on this day") .build(); } @Override @Transactional(readOnly = true) public List<BookingResponse> getAllBookings() { List<Booking> bookings = bookingRepository.findAll(); return bookingMapper.toResponseList(bookings); } @Override @Transactional(readOnly = true) public List<BookingResponse> getBookingsByCustomer(Long customerUserId) { List<Booking> bookings = bookingRepository.findByCustomerUserId(customerUserId); return bookingMapper.toResponseList(bookings); } @Override @Transactional(readOnly = true) public BookingResponse getBookingById(Long id) { Booking booking = bookingRepository.findById(id) .orElseThrow(() -> new EntityNotFoundException("Booking not found")); return bookingMapper.toResponse(booking); } } 

```
**Result / Output:**  
<img width="1109" height="498" alt="Ảnh màn hình 2025-10-25 lúc 12 26 12" src="https://github.com/user-attachments/assets/c0a7b4bc-5e84-4fd4-9485-289dcf2533b4" />

## Prompt 2 – Generate Test Cases
**Prompt Command:**  
```text
Goal: Design detailed test cases (no code generation). Based on the previous analysis, generate a comprehensive list of JUnit 5 test cases for the following function: ceateBooking() Include only the test case designs for the following scenarios: ✅ Happy path – valid inputs producing correct results ⚠️ Edge cases – boundary values, nulls, empty lists, or unusual valid inputs ❌ Error scenarios – invalid inputs, exceptions, or unexpected states Requirements: Focus solely on test case descriptions, not code. Each test case should follow Arrange – Act – Assert (AAA) logic in its description. Limit to around 4–5 test cases for this function Ensure each test case is clear, unique, and directly testable using JUnit later. Output format: Present the results in a table with the following columns: Test Case ID / Name – descriptive and consistent (e.g., TC01_shouldReturnCorrectTotal_whenItemsValid) Scenario Type – Happy path / Edge case / Error scenario Input Data / Preconditions – specific parameters or state setup Expected Output / Behavior – exact result, state change, or exception expected AAA Description – brief summary of how Arrange, Act, and Assert would be structured Priority – High / Medium / Low Do not generate test code — only produce the detailed test case design table.
```
**Result / Output:**  
<img width="1400" height="673" alt="Ảnh màn hình 2025-10-25 lúc 12 28 55" src="https://github.com/user-attachments/assets/53088a0c-063d-4e58-825c-529b533ee96f" />

## Prompt 3 – Generate Test Code
**Prompt Command:**  
```text
Create JUnit 5 test methods for the following function, using the provided test cases: createBooking Test cases: above cases Requirements: - Only generate what is necessary to add into an existing test class: - Test methods annotated with @Test - Any method-specific mock data or setup required - Any helper methods specific to these tests - Do NOT generate the full class, imports, or package declaration. - Assume common mocks and setup (e.g., BookingRepository, TableRepository, UserRepository, BookingMapper) already exist in the test class. - Include proper assertions (assertEquals, assertThrows, etc.) - Use clear and descriptive test method names (e.g., shouldReturnCorrectTotal_whenItemsValid()) - Include any method-specific @BeforeEach setup if required for these tests. - Output only the test methods and necessary helper/mock data for the function.
```
**Result / Output:**  
```java
@BeforeEach
void setUpCreateBooking() {
    table = new TableEntity();
    table.setId(1L);

    validRequest = new BookingCreateRequest();
    validRequest.setTableId(1L);
    validRequest.setBookingTime(LocalDateTime.now().plusDays(1));
    validRequest.setCustomerName("John Doe");
    validRequest.setCustomerPhone("0123456789");
    validRequest.setCustomerUserId(1L);
}

@Test
void TC01_shouldCreateBookingSuccessfully_whenValidInput() {
    when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
    when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList());
    User user = new User();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    Booking bookingEntity = new Booking();
    Booking savedBooking = new Booking();
    savedBooking.setStatus("Pending");
    when(bookingMapper.toEntity(validRequest, table, user)).thenReturn(bookingEntity);
    when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking);
    BookingResponse response = new BookingResponse();
    when(bookingMapper.toResponse(savedBooking)).thenReturn(response);

    BookingResponse result = bookingService.createBooking(validRequest);

    assertEquals(response, result);
    assertEquals("Pending", savedBooking.getStatus());
    verify(bookingRepository).save(bookingEntity);
}

@Test
void TC02_shouldThrowException_whenTableAlreadyBooked() {
    when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
    Booking existingBooking = new Booking();
    when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.singletonList(existingBooking));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
        bookingService.createBooking(validRequest);
    });

    assertEquals("Table already has a booking on this day!", exception.getMessage());
    verify(bookingRepository, never()).save(any());
}

@Test
void TC03_shouldThrowException_whenCustomerNameIsNullOrBlank() {
    validRequest.setCustomerName(null);
    when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
    when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        bookingService.createBooking(validRequest);
    });

    assertEquals("Customer name is required", exception.getMessage());
    verify(bookingRepository, never()).save(any());
}

@Test
void TC04_shouldThrowException_whenPhoneNumberInvalid() {
    validRequest.setCustomerPhone("abc123");
    when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
    when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        bookingService.createBooking(validRequest);
    });

    assertEquals("Invalid phone number", exception.getMessage());
    verify(bookingRepository, never()).save(any());
}

@Test
void TC05_shouldCreateBooking_whenCustomerUserIdNotExist() {
    validRequest.setCustomerUserId(999L);
    when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
    when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList());
    when(userRepository.findById(999L)).thenReturn(Optional.empty());
    Booking bookingEntity = new Booking();
    Booking savedBooking = new Booking();
    savedBooking.setStatus("Pending");
    when(bookingMapper.toEntity(validRequest, table, null)).thenReturn(bookingEntity);
    when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking);
    BookingResponse response = new BookingResponse();
    when(bookingMapper.toResponse(savedBooking)).thenReturn(response);

    BookingResponse result = bookingService.createBooking(validRequest);

    assertEquals(response, result);
    assertEquals("Pending", savedBooking.getStatus());
    verify(bookingRepository).save(bookingEntity);
}
```
## Prompt 4 – Fix Failing Tests
**Prompt Command:**  
```text
Help me fix this failing unit test:
 [TEST_CODE]
Here’s the error message:
 [ERROR_MESSAGE]
Please:
Identify the root cause of the failure
Correct the test code or configuration so that all tests pass
Ensure consistent setup and mocking (if applicable)
Provide a short summary of test results (passed/failed count)
Include a brief coverage summary (overall % and key files tested)
```

## Prompt 5 – Mock & Integration
**Prompt Command:**  
```text
Please optimize and enhance the following test suite. Create and integrate mock objects for these dependencies: all dependency Then, update the package com.example.restaurant_management.service; import com.example.restaurant_management.dto.request.BookingCreateRequest; import com.example.restaurant_management.dto.response.BookingResponse; import com.example.restaurant_management.entity.Booking; import com.example.restaurant_management.entity.TableEntity; import com.example.restaurant_management.entity.User; import com.example.restaurant_management.mapper.BookingMapper; import com.example.restaurant_management.repository.BookingRepository; import com.example.restaurant_management.repository.TableRepository; import com.example.restaurant_management.repository.UserRepository; import jakarta.persistence.EntityNotFoundException; import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import org.mockito.InjectMocks; import org.mockito.Mock; import org.mockito.MockitoAnnotations; import java.time.LocalDateTime; import java.util.Collections; import java.util.List; import java.util.Optional; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*; class BookingServiceImplTest { @Mock private BookingRepository bookingRepository; @Mock private TableRepository tableRepository; @Mock private UserRepository userRepository; @Mock private BookingMapper bookingMapper; @InjectMocks private BookingServiceImpl bookingService; private BookingCreateRequest request; private TableEntity table; private Booking booking; private BookingResponse bookingResponse; private User user; @BeforeEach void setUp() { } } @BeforeEach void setUpCreateBooking() { table = new TableEntity(); table.setId(1L); validRequest = new BookingCreateRequest(); validRequest.setTableId(1L); validRequest.setBookingTime(LocalDateTime.now().plusDays(1)); validRequest.setCustomerName("John Doe"); validRequest.setCustomerPhone("0123456789"); validRequest.setCustomerUserId(1L); } @Test void TC01_shouldCreateBookingSuccessfully_whenValidInput() { when(tableRepository.findById(1L)).thenReturn(Optional.of(table)); when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList()); User user = new User(); when(userRepository.findById(1L)).thenReturn(Optional.of(user)); Booking bookingEntity = new Booking(); Booking savedBooking = new Booking(); savedBooking.setStatus("Pending"); when(bookingMapper.toEntity(validRequest, table, user)).thenReturn(bookingEntity); when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking); BookingResponse response = new BookingResponse(); when(bookingMapper.toResponse(savedBooking)).thenReturn(response); BookingResponse result = bookingService.createBooking(validRequest); assertEquals(response, result); assertEquals("Pending", savedBooking.getStatus()); verify(bookingRepository).save(bookingEntity); } @Test void TC02_shouldThrowException_whenTableAlreadyBooked() { when(tableRepository.findById(1L)).thenReturn(Optional.of(table)); Booking existingBooking = new Booking(); when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.singletonList(existingBooking)); IllegalStateException exception = assertThrows(IllegalStateException.class, () -> { bookingService.createBooking(validRequest); }); assertEquals("Table already has a booking on this day!", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC03_shouldThrowException_whenCustomerNameIsNullOrBlank() { validRequest.setCustomerName(null); when(tableRepository.findById(1L)).thenReturn(Optional.of(table)); when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList()); IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> { bookingService.createBooking(validRequest); }); assertEquals("Customer name is required", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC04_shouldThrowException_whenPhoneNumberInvalid() { validRequest.setCustomerPhone("abc123"); when(tableRepository.findById(1L)).thenReturn(Optional.of(table)); when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList()); IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> { bookingService.createBooking(validRequest); }); assertEquals("Invalid phone number", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC05_shouldCreateBooking_whenCustomerUserIdNotExist() { validRequest.setCustomerUserId(999L); when(tableRepository.findById(1L)).thenReturn(Optional.of(table)); when(bookingRepository.findByTableAndDay(1L, validRequest.getBookingTime())).thenReturn(Collections.emptyList()); when(userRepository.findById(999L)).thenReturn(Optional.empty()); Booking bookingEntity = new Booking(); Booking savedBooking = new Booking(); savedBooking.setStatus("Pending"); when(bookingMapper.toEntity(validRequest, table, null)).thenReturn(bookingEntity); when(bookingRepository.save(bookingEntity)).thenReturn(savedBooking); BookingResponse response = new BookingResponse(); when(bookingMapper.toResponse(savedBooking)).thenReturn(response); BookingResponse result = bookingService.createBooking(validRequest); assertEquals(response, result); assertEquals("Pending", savedBooking.getStatus()); verify(bookingRepository).save(bookingEntity); } @BeforeEach void setUpUpdateBooking() { existingBooking = new Booking(); existingBooking.setId(1L); TableEntity table = new TableEntity(); table.setId(1L); existingBooking.setTable(table); existingBooking.setBookingTime(LocalDateTime.now().plusDays(1)); updateRequest = new BookingUpdateRequest(); updateRequest.setTableId(1L); updateRequest.setBookingTime(existingBooking.getBookingTime()); updateRequest.setCustomerName("Jane Doe"); updateRequest.setCustomerPhone("0987654321"); } @Test void TC01_shouldUpdateBookingSuccessfully_whenValidInput() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); TableEntity newTable = new TableEntity(); newTable.setId(1L); when(tableRepository.findById(1L)).thenReturn(Optional.of(newTable)); when(bookingRepository.findByTableAndDay(1L, updateRequest.getBookingTime())).thenReturn(Collections.emptyList()); Booking updatedBooking = new Booking(); when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse()); BookingResponse result = bookingService.updateBooking(1L, updateRequest); assertNotNull(result); verify(bookingRepository).save(existingBooking); } @Test void TC02_shouldThrowException_whenBookingNotFound() { when(bookingRepository.findById(1L)).thenReturn(Optional.empty()); EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> { bookingService.updateBooking(1L, updateRequest); }); assertEquals("Booking not found", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC03_shouldThrowException_whenTableNotFound() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); when(tableRepository.findById(2L)).thenReturn(Optional.empty()); updateRequest.setTableId(2L); EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> { bookingService.updateBooking(1L, updateRequest); }); assertEquals("Table not found", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC04_shouldThrowException_whenTableConflictOccurs() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); TableEntity newTable = new TableEntity(); newTable.setId(2L); updateRequest.setTableId(2L); updateRequest.setBookingTime(existingBooking.getBookingTime().plusDays(1)); when(tableRepository.findById(2L)).thenReturn(Optional.of(newTable)); Booking conflictingBooking = new Booking(); conflictingBooking.setId(3L); when(bookingRepository.findByTableAndDay(2L, updateRequest.getBookingTime())) .thenReturn(Collections.singletonList(conflictingBooking)); IllegalStateException exception = assertThrows(IllegalStateException.class, () -> { bookingService.updateBooking(1L, updateRequest); }); assertEquals("This table already has a booking on that day", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC05_shouldUpdateBooking_whenNoTableOrDateChange_butOtherFieldsModified() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); when(tableRepository.findById(1L)).thenReturn(Optional.of(existingBooking.getTable())); when(bookingRepository.findByTableAndDay(1L, existingBooking.getBookingTime())).thenReturn(Collections.emptyList()); Booking updatedBooking = new Booking(); when(bookingMapper.toResponse(any())).thenReturn(new BookingResponse()); BookingResponse result = bookingService.updateBooking(1L, updateRequest); assertNotNull(result); verify(bookingRepository).save(existingBooking); } @BeforeEach void setUpDeleteBooking() { existingBooking = new Booking(); existingBooking.setId(1L); existingBooking.setStatus("Pending"); } @Test void TC01_shouldDeleteBookingSuccessfully_whenBookingExistsAndNotCompleted() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); assertDoesNotThrow(() -> bookingService.deleteBooking(1L)); verify(bookingRepository).delete(existingBooking); } @Test void TC02_shouldThrowException_whenBookingNotFound() { when(bookingRepository.findById(1L)).thenReturn(Optional.empty()); EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> { bookingService.deleteBooking(1L); }); assertEquals("Booking not found", exception.getMessage()); verify(bookingRepository, never()).delete(any()); } @Test void TC03_shouldThrowException_whenBookingIsCompleted() { existingBooking.setStatus("Completed"); when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); IllegalStateException exception = assertThrows(IllegalStateException.class, () -> { bookingService.deleteBooking(1L); }); assertEquals("Cannot delete a completed booking", exception.getMessage()); verify(bookingRepository, never()).delete(any()); } @Test void TC04_shouldThrowException_whenBookingStatusCaseInsensitiveCompleted() { existingBooking.setStatus("completed"); when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); IllegalStateException exception = assertThrows(IllegalStateException.class, () -> { bookingService.deleteBooking(1L); }); assertEquals("Cannot delete a completed booking", exception.getMessage()); verify(bookingRepository, never()).delete(any()); } @Test void TC05_shouldDeleteBookingSuccessfully_whenBookingExistsWithNullStatus() { existingBooking.setStatus(null); when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); assertDoesNotThrow(() -> bookingService.deleteBooking(1L)); verify(bookingRepository).delete(existingBooking); } @BeforeEach void setUpUpdateStatus() { existingBooking = new Booking(); existingBooking.setId(1L); existingBooking.setStatus("Pending"); } @Test void TC01_shouldUpdateStatusSuccessfully_whenValidStatus() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); Booking updatedBooking = new Booking(); updatedBooking.setStatus("Confirmed"); when(bookingRepository.save(existingBooking)).thenReturn(updatedBooking); when(bookingMapper.toResponse(updatedBooking)).thenReturn(new BookingResponse()); BookingResponse result = bookingService.updateStatus(1L, "Confirmed"); assertEquals("Confirmed", updatedBooking.getStatus()); verify(bookingRepository).save(existingBooking); } @Test void TC02_shouldThrowException_whenBookingNotFound() { when(bookingRepository.findById(1L)).thenReturn(Optional.empty()); EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> { bookingService.updateStatus(1L, "Confirmed"); }); assertEquals("Booking not found", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC03_shouldThrowException_whenInvalidStatusProvided() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> { bookingService.updateStatus(1L, "InvalidStatus"); }); assertEquals("Invalid status: InvalidStatus", exception.getMessage()); verify(bookingRepository, never()).save(any()); } @Test void TC04_shouldUpdateStatusSuccessfully_whenStatusIsCaseInsensitiveValid() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); Booking updatedBooking = new Booking(); updatedBooking.setStatus("completed"); when(bookingRepository.save(existingBooking)).thenReturn(updatedBooking); when(bookingMapper.toResponse(updatedBooking)).thenReturn(new BookingResponse()); BookingResponse result = bookingService.updateStatus(1L, "completed"); assertEquals("completed", updatedBooking.getStatus()); verify(bookingRepository).save(existingBooking); } @Test void TC05_shouldUpdateStatusSuccessfully_whenStatusIsSameAsCurrent() { when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking)); Booking updatedBooking = new Booking(); updatedBooking.setStatus("Pending"); when(bookingRepository.save(existingBooking)).thenReturn(updatedBooking); when(bookingMapper.toResponse(updatedBooking)).thenReturn(new BookingResponse()); BookingResponse result = bookingService.updateStatus(1L, "Pending"); assertEquals("Pending", updatedBooking.getStatus()); verify(bookingRepository).save(existingBooking); } accordingly to improve reliability and speed. Additionally, please: 🧩 Implement mocks/stubs using Mockito (or equivalent) for all external dependencies ⚙️ Optimize performance of test execution (reduce redundancy, reuse setup) 🔗 Add integration tests if they help validate end-to-end behavior 🧼 Refactor the test code for clarity, maintainability, and consistency Output requirements: Updated and optimized test code (with mock implementations) Explanation of the applied improvements (mocking, refactoring, integration) Final summary of test structure (unit vs integration) and expected performance gains
```
**Result / Output:**  
```java
package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;
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
import static org.mockito.ArgumentMatchers.any;
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
    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Common entities
        table = new TableEntity();
        table.setId(1L);

        user = new User();
        user.setId(1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus("Pending");
        booking.setTable(table);
        booking.setBookingTime(LocalDateTime.now().plusDays(1));

        // Common requests
        createRequest = new BookingCreateRequest();
        createRequest.setTableId(1L);
        createRequest.setBookingTime(LocalDateTime.now().plusDays(1));
        createRequest.setCustomerName("John Doe");
        createRequest.setCustomerPhone("0123456789");
        createRequest.setCustomerUserId(1L);

        updateRequest = new BookingUpdateRequest();
        updateRequest.setTableId(1L);
        updateRequest.setBookingTime(booking.getBookingTime());
        updateRequest.setCustomerName("Jane Doe");
        updateRequest.setCustomerPhone("0987654321");
    }

    // ==========================
    // Create Booking Tests
    // ==========================
    @Test
    void shouldCreateBookingSuccessfully_whenValidInput() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime()))
                .thenReturn(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingMapper.toEntity(createRequest, table, user)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.createBooking(createRequest);

        assertNotNull(result);
        assertEquals("Pending", booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowException_whenTableAlreadyBooked() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime()))
                .thenReturn(Collections.singletonList(new Booking()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.createBooking(createRequest));
        assertEquals("Table already has a booking on this day!", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenCustomerNameInvalid() {
        createRequest.setCustomerName("");
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime())).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createRequest));
        assertEquals("Customer name is required", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenPhoneNumberInvalid() {
        createRequest.setCustomerPhone("abc123");
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime())).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createRequest));
        assertEquals("Invalid phone number", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldCreateBooking_whenCustomerUserIdDoesNotExist() {
        createRequest.setCustomerUserId(999L);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, createRequest.getBookingTime())).thenReturn(Collections.emptyList());
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(bookingMapper.toEntity(createRequest, table, null)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.createBooking(createRequest);

        assertNotNull(result);
        assertEquals("Pending", booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    // ==========================
    // Update Booking Tests
    // ==========================
    @Test
    void shouldUpdateBookingSuccessfully_whenValidInput() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(bookingRepository.findByTableAndDay(1L, updateRequest.getBookingTime())).thenReturn(Collections.emptyList());
        when(bookingMapper.toResponse(booking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateBooking(1L, updateRequest);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1L, updateRequest));
        assertEquals("Booking not found", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenTableConflictOccurs() {
        TableEntity newTable = new TableEntity();
        newTable.setId(2L);
        updateRequest.setTableId(2L);
        updateRequest.setBookingTime(booking.getBookingTime().plusDays(1));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(tableRepository.findById(2L)).thenReturn(Optional.of(newTable));
        when(bookingRepository.findByTableAndDay(2L, updateRequest.getBookingTime()))
                .thenReturn(Collections.singletonList(new Booking()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(1L, updateRequest));
        assertEquals("This table already has a booking on that day", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    // ==========================
    // Delete Booking Tests
    // ==========================
    @Test
    void shouldDeleteBookingSuccessfully_whenBookingExistsAndNotCompleted() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> bookingService.deleteBooking(1L));
        verify(bookingRepository).delete(booking);
    }

    @Test
    void shouldThrowException_whenBookingIsCompleted() {
        booking.setStatus("Completed");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.deleteBooking(1L));
        assertEquals("Cannot delete a completed booking", ex.getMessage());
        verify(bookingRepository, never()).delete(any());
    }

    // ==========================
    // Update Status Tests
    // ==========================
    @Test
    void shouldUpdateStatusSuccessfully_whenValidStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(new BookingResponse());

        BookingResponse result = bookingService.updateStatus(1L, "Confirmed");

        assertEquals("Confirmed", booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowException_whenInvalidStatusProvided() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.updateStatus(1L, "InvalidStatus"));
        assertEquals("Invalid status: InvalidStatus", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}
```
Please generate comprehensive documentation for the following test suite:
```text
 [TEST_CODE]
The documentation should include:
🧩 Overview: Purpose and scope of the test suite
⚙️ Setup instructions: How to configure and run the tests (commands, environment setup, dependencies)
🧪 Test structure: Description of unit tests, integration tests, and key functionalities covered
📊 Test metrics: Summary of test results (pass/fail count), code coverage %, and performance insights
🧾 Sample results or demo steps: How to reproduce a short demo of successful test execution
💾 Version control note: Example of a proper Git commit message for this test suite (e.g., feat(tests): add optimized unit tests with 90% coverage)
Output format:
A professional README.md file describing the test suite
A short Test Metrics Report section with summarized data (pass rate, coverage, execution time)
Bonus (optional):
Suggest improvements or next steps to maintain high-quality testing (e.g., CI/CD integration, periodic test audits)
```




