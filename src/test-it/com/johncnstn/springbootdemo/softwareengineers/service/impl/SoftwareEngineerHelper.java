package com.johncnstn.springbootdemo.softwareengineers.service.impl;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

/*
 🧪 Test-only helper registered with Spring via @TestComponent

 ✅ Why @TestComponent?
    - Automatically registered in the Spring test context (like @Component but test-scoped)
    - No need to manually @Import or define a @Bean in test configs
    - Cleanly injects into test classes using constructor injection
    - Keeps test setup reusable, DRY, and isolated from production

 🧰 Purpose:
    - Provides utility methods for creating and clearing test entities (SoftwareEngineer)
    - Centralizes repeated test setup logic (e.g., default test engineer, tech stack)
    - Improves readability and maintainability across integration test classes

 📌 Test usage:
    - Inject with @Autowired or @TestConstructor in test classes
    - Recommended for integration tests involving service + repo layers

 🧪 Deterministic test data:
    - Fixed name + tech stack ensure stable, predictable test behavior
    - Avoids flaky tests and simplifies debugging

 🔄 Use `clear()` to reset DB state after tests
    - Call from @AfterEach or @AfterAll in integration tests
    - Prevents test data leakage and ensures isolation
    - ⚠️ Be cautious in parallel test execution—shared DBs might conflict

 ⚙️ Compatible with test DBs like H2 or PostgreSQL (Testcontainers)
    - Assumes clean schema and isolated data state
    - Designed for safe use in Spring Boot integration tests

 🚀 Performance tip:
    - Use helper only where needed; avoid unnecessary DB writes
    - Keep test data minimal and targeted per test case

 ❌ Not used in production:
    - `@TestComponent` ensures it won’t be accidentally picked up by main app context
    - Safer than @Component for test scaffolding
 */
@TestComponent
public class SoftwareEngineerHelper {

    public static final String TEST_ENGINEER_NAME = "Pawa";
    public static final List<String> TEST_TECH_STACK = List.of("Java", "Spring");

    private final SoftwareEngineerRepository softwareEngineerRepository;
    private final SoftwareEngineerMapper softwareEngineerMapper;

    public SoftwareEngineerHelper(SoftwareEngineerRepository softwareEngineerRepository,
                                  SoftwareEngineerMapper softwareEngineerMapper) {
        this.softwareEngineerRepository = softwareEngineerRepository;
        this.softwareEngineerMapper = softwareEngineerMapper;
    }

    public SoftwareEngineerDTO createDefaultSoftwareEngineer() {
        return createSoftwareEngineer(TEST_ENGINEER_NAME, TEST_TECH_STACK);
    }

    public SoftwareEngineerDTO createSoftwareEngineer(String name, List<String> techStack) {
        SoftwareEngineerToCreateDTO engineerToCreate = SoftwareEngineerToCreateDTO
                .builder()
                .name(name)
                .techStack(techStack)
                .build();
        SoftwareEngineerEntity createdSoftwareEngineer = softwareEngineerRepository.create(engineerToCreate);
        return softwareEngineerMapper.toDTO(createdSoftwareEngineer);
    }

    public void clear() {
        softwareEngineerRepository.deleteAll();
    }

}
