package com.johncnstn.springbootdemo.softwareengineers.repository;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
====================================================================
✅ UNIT TESTING BEST PRACTICES (applied in this class)
====================================================================

🔹 Structure Tests Clearly:
   - Follow the GIVEN-WHEN-THEN structure for readability.
   - Separate setup, action, and assertion clearly.

🔹 Mock Before You Call:
   - Always configure `when(...).thenReturn(...)` BEFORE invoking the actual method.
   - Prevents flaky behavior and unexpected `null`s.

🔹 Use captors when the argument is dynamically constructed:
   - Use ArgumentCaptor to verify what was passed into mocks.
   - Especially useful for asserting DTO content and side effects.

🔹 Use eq(...) and any() wisely
    - eq(...): strict value match — fails if different
	- any(), anyString(), anyInt(): looser matching — useful when you don’t care
    - But don’t mix eq(...) and any(...) without care — it can cause InvalidUseOfMatchersException unless all arguments are matchers.

🔹 Stub only what you control:
   - Don't stub real methods or logic under test.
   - Stub only the boundaries (e.g., mappers, external dependencies).

🔹 Test Both Positive & Negative Paths:
   - "shouldFind" and "shouldNotFind" test both presence and absence.
   - Ensures correctness under multiple conditions.

🔹 Verify only essential interactions
    - Use verify(...) to assert key side effects (like DB writes, audit logs).
        Don’t verify every single call — only what’s important for correctness.

🔹 Use SoftAssertions for multiple checks:
   - Ensures all failures are reported at once, not just the first.

🔹 Isolate Unit Tests:
   - Avoid using Spring context for unit tests.
   - Focus only on business logic, not integration.

🔹 Stub vs Mock:
   - Stub: Return controlled responses from collaborators (e.g. `when(...)`).
   - Mock: Verify that certain behavior happened (e.g. `verify(...)`).
   - Here: `softwareEngineerMapper` is both stubbed and verified.

🔹 Use clear names:
    - shouldX_whenY

🔹 Use soft assertions (assertSoftly) for grouped checks


=======================================================================================
🛑 RAW VALUE BEST PRACTICES
=======================================================================================

🔸 Use meaningful constants for shared test data:
   → Prevents typos and increases reusability.

🔸 Avoid repeating magic values like `"Pawa"` or `1`:
   → Extract to `private static final` constants or builder methods.

🔸 Descriptive test data helps communicate the test's purpose:
   → Instead of `name = "a"` use `name = "ValidName"`

🔸 Always test both valid and invalid inputs:
   → e.g., null name, empty stack, too short/long names

🔸 Use helper methods for building input DTOs:
   → Reduces clutter and duplication across tests.
*/
@ExtendWith(MockitoExtension.class)
class SoftwareEngineerRepositoryUnitTest {

    /*
     * ✅ System under test (SUT)
     * - Injects mocks automatically into SoftwareEngineerRepository
     * - Allows testing it in isolation without Spring
     */
    @InjectMocks
    private SoftwareEngineerRepository sut;

    /*
     * ✅ @Mock is used to stub behavior of external dependencies
     * - Here: we isolate the logic of the repository from the mapper
     * - If the mapper breaks, this test still works.
     */
    @Mock
    private SoftwareEngineerMapper softwareEngineerMapper;

    /*
     * ✅ Class-level ArgumentCaptor for reuse
     * - Use when the same captor is used across multiple test methods.
     * - Cleaner and declarative.
     *
     * When to use ArgumentCaptor:
     * - When you want to **assert** the actual values passed to mocks
     * - Helpful if you want to validate intermediate state/behavior.
     */
    @Captor
    private ArgumentCaptor<Integer> idCaptor;

    private static final String ENGINEER_NAME = "Pawa";
    private static final String UPDATED_NAME = "Vladislav";
    private static final List<String> STACK = List.of("java", "spring");

    @Test
    void shouldCreateEngineer() {
        // Arrange (GIVEN)
        SoftwareEngineerToCreateDTO pawaEngineer = SoftwareEngineerToCreateDTO
                .builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();

        SoftwareEngineerEntity engineerEntity = SoftwareEngineerEntity
                .builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();

        /*
         * ✅ when(...).thenReturn(...) is used to stub mock behavior.
         * - Use it when your code under test *calls* a dependency method
         * - You want to simulate that method returning a known result
         * ✅ Configure stub BEFORE calling create()
         * ✅ eq(...) ensures strict equality matching, avoiding false positives
         */
        when(softwareEngineerMapper.toEntity(anyInt(), eq(pawaEngineer))).thenReturn(engineerEntity);

        /*
         * You can also use ArgumentCaptor locally if only needed here:
         * ArgumentCaptor<SoftwareEngineerToCreateDTO> dtoCaptor = ArgumentCaptor.forClass(...);
         */

        // Act (WHEN)
        SoftwareEngineerEntity createdEngineer = sut.create(pawaEngineer);

        /*
         * ✅ verify(...) checks *interactions* with mocks
         * - Use this to assert that a method on a mock was called (and how)
         * - Combine with captors to inspect those calls
         */
        verify(softwareEngineerMapper)
                .toEntity(idCaptor.capture(), ArgumentCaptor.forClass(SoftwareEngineerToCreateDTO.class).capture());

        assertSoftly(softly -> {
            softly.assertThat(createdEngineer).isNotNull();
            softly.assertThat(createdEngineer.getName()).isEqualTo(ENGINEER_NAME);
            softly.assertThat(createdEngineer.getTechStack()).isEqualTo(STACK);
        });
    }

    @Test
    void shouldFindEngineer() {
        // GIVEN
        SoftwareEngineerToCreateDTO createDTO = SoftwareEngineerToCreateDTO.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();
        SoftwareEngineerEntity entity = SoftwareEngineerEntity.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();

        when(softwareEngineerMapper.toEntity(any(), any())).thenReturn(entity);
        sut.create(createDTO);

        // WHEN
        Optional<SoftwareEngineerEntity> result = sut.findById(1);

        // THEN
        assertThat(result).isPresent();
    }

    @Test
    void shouldNotFindEngineer() {
        // WHEN
        Optional<SoftwareEngineerEntity> result = sut.findById(999);

        // THEN
        assertThat(result).isNotPresent();
    }

    @Test
    void shouldGetEngineers() {
        // GIVEN
        SoftwareEngineerToCreateDTO dto = SoftwareEngineerToCreateDTO.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();
        SoftwareEngineerEntity entity = SoftwareEngineerEntity.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();

        when(softwareEngineerMapper.toEntity(any(), any())).thenReturn(entity);
        sut.create(dto);

        // WHEN
        List<SoftwareEngineerEntity> engineers = sut.getAll();

        // THEN
        assertThat(engineers).isNotEmpty();
    }

    @Test
    void shouldUpdateEngineer() {
        // GIVEN
        SoftwareEngineerToCreateDTO createDTO = SoftwareEngineerToCreateDTO.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();
        SoftwareEngineerEntity entity = SoftwareEngineerEntity.builder()
                .name(ENGINEER_NAME)
                .techStack(STACK)
                .build();

        when(softwareEngineerMapper.toEntity(any(), any())).thenReturn(entity);
        SoftwareEngineerEntity created = sut.create(createDTO);

        SoftwareEngineerToUpdateDTO updateDTO = SoftwareEngineerToUpdateDTO.builder()
                .name(UPDATED_NAME)
                .techStack(STACK)
                .build();

        // WHEN
        sut.update(created.getId(), updateDTO);
        Optional<SoftwareEngineerEntity> updatedOpt = sut.findById(created.getId());

        // THEN
        assertSoftly(softly -> {
            softly.assertThat(updatedOpt).isPresent();
            SoftwareEngineerEntity updated = updatedOpt.get();

            softly.assertThat(updated.getId()).isEqualTo(created.getId());
            softly.assertThat(updated.getName()).isNotEqualTo(ENGINEER_NAME);
            softly.assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
            softly.assertThat(updated.getTechStack()).isEqualTo(STACK);
        });
    }

}