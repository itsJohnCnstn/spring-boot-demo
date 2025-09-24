package com.johncnstn.springbootdemo.softwareengineers.service.impl;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.exception.EntityNotFoundException;
import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerCommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.johncnstn.springbootdemo.softwareengineers.service.impl.SoftwareEngineerHelper.TEST_ENGINEER_NAME;
import static com.johncnstn.springbootdemo.softwareengineers.service.impl.SoftwareEngineerHelper.TEST_TECH_STACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 ✅ This is a true integration test:
    - Loads only selected beans (via @Import), not the full Spring Boot app context.
    - Fast, focused, and closer to the business logic layer (service + repo).
    - Allows real dependency injection and DB writes/reads without needing REST or HTTP.

 🔍 Use this pattern for testing:
    - Service-to-repository integration
    - Transactions, validation, mappers, etc.

 🔁 Active profile used to switch to test DB config (H2 recommended).
*/
@ExtendWith(SpringExtension.class)
@Import({SoftwareEngineerCommandServiceImpl.class,
        SoftwareEngineerQueryServiceImpl.class,
        SoftwareEngineerRepository.class,
        SoftwareEngineerMapper.class,
        SoftwareEngineerHelper.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class SoftwareEngineerCommandServiceIT {

    private static final int NON_EXISTENT_ID = -1;

    // @Autowired if not using @TestConstructor
    private final SoftwareEngineerCommandService sut;
    private final SoftwareEngineerRepository softwareEngineerRepository;

    private final SoftwareEngineerHelper softwareEngineerHelper;

    // ✅ Constructor injection works with @TestConstructor
    public SoftwareEngineerCommandServiceIT(SoftwareEngineerCommandService softwareEngineerCommandService,
                                            SoftwareEngineerRepository softwareEngineerRepository,
                                            SoftwareEngineerHelper softwareEngineerHelper) {
        this.sut = softwareEngineerCommandService;
        this.softwareEngineerRepository = softwareEngineerRepository;
        this.softwareEngineerHelper = softwareEngineerHelper;
    }

    /*
       ✅ Inject only the needed dependencies via method parameters.
          Spring injects these automatically into the test lifecycle method.

       🔍 Avoids constructor and field injection.

        private SoftwareEngineerCommandService sut;
        private SoftwareEngineerRepository softwareEngineerRepository;
        @BeforeEach
        void setUp(SoftwareEngineerCommandService sut,
                   SoftwareEngineerRepository softwareEngineerRepository) {
            this.sut = sut;
            this.softwareEngineerRepository = softwareEngineerRepository;
        }
     */

    /*
     🔄 Ensures test isolation by clearing DB state between runs.
     🔒 Required when using stateful components like in-memory DBs.
    */
    @AfterEach
    void tearDown() {
        softwareEngineerHelper.clear();
    }

    @DisplayName("Should create a software engineer successfully")
    @Test
    void shouldCreateSoftwareEngineer_whenValidDataProvided() {
        // GIVEN
        SoftwareEngineerToCreateDTO engineerToCreate = SoftwareEngineerToCreateDTO
                .builder()
                .name(TEST_ENGINEER_NAME)
                .techStack(TEST_TECH_STACK)
                .build();

        // WHEN
        SoftwareEngineerDTO createdEngineer = sut.create(engineerToCreate);

        // THEN
        assertSoftly(softly -> {
            softly.assertThat(createdEngineer.id()).isNotNull();
            softly.assertThat(createdEngineer.name()).isEqualTo(TEST_ENGINEER_NAME);
            softly.assertThat(createdEngineer.techStack()).isEqualTo(TEST_TECH_STACK);
        });
    }

    @Nested
    @DisplayName("Update tests")
    class UpdateTests {

        @Test
        void shouldUpdateSoftwareEngineer_whenExistingIdAndValidPayload() {
            // GIVEN
            Integer id = softwareEngineerHelper.createDefaultSoftwareEngineer().id();
            String nameToUpdate = "Miha";
            List<String> stackToUpdate = List.of("Java", "Kotlin", "Spring");
            SoftwareEngineerToUpdateDTO engineerToUpdate = SoftwareEngineerToUpdateDTO
                    .builder()
                    .name(nameToUpdate)
                    .techStack(stackToUpdate)
                    .build();

            // WHEN
            sut.update(id, engineerToUpdate);

            // THEN
            assertSoftly(softly -> {
                Optional<SoftwareEngineerEntity> updatedEngineerOpt = softwareEngineerRepository.findById(id);
                softly.assertThat(updatedEngineerOpt).isPresent();
                SoftwareEngineerEntity updatedEngineer = updatedEngineerOpt.get();

                softly.assertThat(updatedEngineer.getId()).isEqualTo(id);

                softly.assertThat(updatedEngineer.getName()).isNotEqualTo(TEST_ENGINEER_NAME);
                softly.assertThat(updatedEngineer.getName()).isEqualTo(nameToUpdate);

                softly.assertThat(updatedEngineer.getTechStack()).isNotEqualTo(TEST_TECH_STACK);
                softly.assertThat(updatedEngineer.getTechStack()).isEqualTo(stackToUpdate);
            });
        }

        @Test
        void shouldThrowEntityNotFoundException_whenUpdatingSoftwareEngineerWithNonexistentId() {
            // GIVEN / WHEN / THEN
            SoftwareEngineerToUpdateDTO dummyDTO = SoftwareEngineerToUpdateDTO.builder()
                    .name("Dummy")
                    .techStack(List.of())
                    .build();
            EntityNotFoundException entityNotFoundException = assertThrows(
                    EntityNotFoundException.class,
                    () -> sut.update(NON_EXISTENT_ID, dummyDTO)
            );
            assertThat(entityNotFoundException.getMessage()).contains("id");
        }

    }

    @Test
    void shouldDeleteSoftwareEngineer_whenExistingIdProvided() {
        // GIVEN
        Integer id = softwareEngineerHelper.createDefaultSoftwareEngineer().id();

        // WHEN
        sut.delete(id);

        // THEN
        assertSoftly(softly -> {
            Optional<SoftwareEngineerEntity> updatedEngineerOpt = softwareEngineerRepository.findById(id);
            softly.assertThat(updatedEngineerOpt).isNotPresent();
        });
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDeletingSoftwareEngineerWithNonexistentId() {
        // GIVEN / WHEN / THEN
        assertThatThrownBy(() -> sut.delete(NON_EXISTENT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("id");
    }

}