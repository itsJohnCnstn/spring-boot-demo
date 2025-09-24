package com.johncnstn.springbootdemo.softwareengineers;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.johncnstn.springbootdemo.softwareengineers.constants.ApiPaths.ENGINEERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/*
 ✅ Full Spring context bootstrapping with an embedded web server (Tomcat/Jetty).
 🔥 Use this for true end-to-end tests where you want:
    - Actual HTTP requests through `TestRestTemplate`
    - Real controllers, filters, error handlers, serialization, etc.
    - No need to mock anything
 🚫 Avoid `WebEnvironment.MOCK` here because it bypasses HTTP transport — good for unit/controller tests only.
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*
 ✅ Isolates config for tests: e.g., use in-memory H2, disable schedulers, override secrets, etc.
 📌 This makes tests safe to run in CI and on local machines without side effects.
*/
@ActiveProfiles("e2e")
class SoftwareEngineerControllerE2EIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareEngineerControllerE2EIT.class);

    /*
     ✅ Real HTTP client that hits the embedded server, unlike `MockMvc` which simulates requests.
     🔍 Use `TestRestTemplate` when:
         - You want to test serialization (e.g., DTO mapping errors)
         - You want to validate HTTP headers, status codes, and request/response structure
         - You want to test authentication, filters, and error handling
    */
    @Autowired
    private TestRestTemplate testRestTemplate;

    /*
     ✅ Direct DB access for assertions and setup.
     📌 In E2E tests, mixing HTTP calls with direct repo reads is valid:
         - Use HTTP to test public API (inputs/outputs)
         - Use repo to verify side effects (what’s stored in DB)
    */
    @Autowired
    private SoftwareEngineerRepository softwareEngineerRepository;

    /*
     🔁 Ensures isolation between tests to prevent flaky failures.
     🧪 Tests should be stateless and repeatable regardless of execution order.
    */
    @AfterEach
    void tearDown() {
        softwareEngineerRepository.deleteAll();
    }

    @Test
    void shouldCreateSoftwareEngineer_happyPath() {
        // given
        String name = "Vlad";
        List<String> stack = List.of("JS", "React");
        SoftwareEngineerToCreateDTO engineerToCreate = SoftwareEngineerToCreateDTO
                .builder()
                .name(name)
                .techStack(stack)
                .build();

        // when
        /*
         ⚠️ This goes through the entire Spring MVC pipeline:
             - @RestController
             - @Valid if present
             - @ExceptionHandler
             - HttpMessageConverters for JSON
             - @ResponseStatus
        */
        ResponseEntity<SoftwareEngineerDTO> response = testRestTemplate
                .postForEntity(ENGINEERS, engineerToCreate, SoftwareEngineerDTO.class);
        LOGGER.debug("Created software engineer: {}", response);

        // then
        assertSoftly(softAssertion -> {
            // ⚠️ charset-safe
            softAssertion.assertThat(response.getHeaders().getContentType()
                    .isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
            softAssertion.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            softAssertion.assertThat(response.getBody()).isNotNull();

            SoftwareEngineerDTO createdSoftwareEngineer = response.getBody();
            softAssertion.assertThat(createdSoftwareEngineer).isNotNull();
            softAssertion.assertThat(createdSoftwareEngineer.name()).isEqualTo(name);
            softAssertion.assertThat(createdSoftwareEngineer.techStack()).isEqualTo(stack);
        });

    }

    @Test
    void shouldReturnAllSoftwareEngineers_happyPath() {
        // given
        SoftwareEngineerToCreateDTO pawaEngineer = SoftwareEngineerToCreateDTO
                .builder()
                .name("Pawa")
                .techStack(List.of("java", "spring"))
                .build();
        SoftwareEngineerToCreateDTO mihaEngineer = SoftwareEngineerToCreateDTO
                .builder()
                .name("Miha")
                .techStack(List.of("java", "kotlin", "spring"))
                .build();
        softwareEngineerRepository.create(pawaEngineer);
        softwareEngineerRepository.create(mihaEngineer);

        // when
        // ⚠️ Arrays are needed here instead of List because `RestTemplate` doesn't deserialise generic types properly.
        ResponseEntity<SoftwareEngineerDTO[]> response = testRestTemplate
                .getForEntity(ENGINEERS, SoftwareEngineerDTO[].class);
        LOGGER.debug("Received engineers: {}", response);

        // then
        assertSoftly(softAssertion -> {
            softAssertion.assertThat(response.getHeaders().getContentType()
                    .isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
            softAssertion.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            softAssertion.assertThat(response.getBody()).isNotNull();

            List<SoftwareEngineerDTO> softwareEngineers = List.of(response.getBody());
            softAssertion.assertThat(softwareEngineers).isNotEmpty();
            softAssertion.assertThat(softwareEngineers.size()).isEqualTo(2);
        });
    }

    @Test
    void shouldReturnSoftwareEngineer_happyPath() {
        // given
        SoftwareEngineerEntity givenEngineer = createDefaultEngineer();

        // when
        String url = ENGINEERS + "/" + givenEngineer.getId();
        ResponseEntity<SoftwareEngineerDTO> response = testRestTemplate.getForEntity(url, SoftwareEngineerDTO.class);
        SoftwareEngineerDTO softwareEngineer = response.getBody();
        LOGGER.debug("Received engineer: {}", softwareEngineer);

        // then
        assertSoftly(softAssertion -> {
            softAssertion.assertThat(response.getHeaders().getContentType().toString()).contains(MediaType.APPLICATION_JSON_VALUE);
            softAssertion.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            softAssertion.assertThat(softwareEngineer.name()).isEqualTo(givenEngineer.getName());
            softAssertion.assertThat(softwareEngineer.techStack()).isEqualTo(givenEngineer.getTechStack());
        });
    }

    /*
     Why use `TestRestTemplate.exchange()` instead of `.put()` or `.delete()`?

     ✅ exchange() is the only method that gives you full control over:
        - HTTP method (PUT, DELETE, PATCH, etc.)
        - Request body
        - Custom headers
        - Response status code and headers
        - Response body (if any)

     💡 Rule of thumb:
        → Always use exchange() for PUT, DELETE, PATCH, or any custom header/body combination
        → Use getForEntity() and postForEntity() only when no special headers or method control is needed
    */
    @Test
    void shouldUpdateSoftwareEngineer_happyPath() {
        // given
        SoftwareEngineerEntity givenEngineer = createDefaultEngineer();
        String nameToUpdate = "Miha";
        List<String> stackToUpdate = Stream.concat(
                        createDefaultEngineer().getTechStack().stream(), Stream.of("Kotlin"))
                .toList();

        SoftwareEngineerDTO softwareEngineerToUpdate = new SoftwareEngineerDTO(givenEngineer.getId(), nameToUpdate, stackToUpdate);

        // when
        String url = ENGINEERS + "/" + softwareEngineerToUpdate.id();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SoftwareEngineerDTO> request = new HttpEntity<>(softwareEngineerToUpdate, headers);
        /*
         🧱 PUT case:
            - testRestTemplate.put(url, body) only sends the request
            - ❌ It doesn’t return a ResponseEntity
                → You can’t assert the status code (e.g. 200, 204)
                → You can’t inspect response headers or error messages
            - ✅ Use exchange() to inspect everything about the response
        */
        ResponseEntity<Void> response = testRestTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );


        assertSoftly(softAssertion -> {
            softAssertion.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            softAssertion.assertThat(response.getBody()).isNull();

            /*
             ⚠️ Response body is intentionally empty here (Void.class)
             ✅ REST best practice: PUT returns 200 or 204 and no body unless there's an extra payload to return
            */
            Optional<SoftwareEngineerEntity> engineerOpt = softwareEngineerRepository.findById(softwareEngineerToUpdate.id());
            softAssertion.assertThat(engineerOpt).isPresent();
            SoftwareEngineerEntity updatedEngineer = engineerOpt.get();
            softAssertion.assertThat(updatedEngineer.getId()).isEqualTo(givenEngineer.getId());

            String updatedName = updatedEngineer.getName();
            softAssertion.assertThat(updatedName).isNotEqualTo(givenEngineer.getName());
            softAssertion.assertThat(updatedName).isEqualTo(nameToUpdate);

            List<String> updatedTechStack = updatedEngineer.getTechStack();
            softAssertion.assertThat(updatedTechStack).isNotEqualTo(givenEngineer.getTechStack());
            softAssertion.assertThat(updatedTechStack).isEqualTo(stackToUpdate);
        });
    }

    @Test
    void shouldDeleteSoftwareEngineerById_happyPath() {
        // given
        SoftwareEngineerEntity givenEngineer = createDefaultEngineer();
        Integer id = givenEngineer.getId();

        // when
        String url = ENGINEERS + "/" + id;
        ResponseEntity<Void> response = testRestTemplate.exchange(
                url, HttpMethod.DELETE, null, Void.class
        );

        //then
        assertSoftly(softAssertion -> {
            softAssertion.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            softAssertion.assertThat(response.getBody()).isNull();

            Optional<SoftwareEngineerEntity> engineerOpt = softwareEngineerRepository.findById(id);
            softAssertion.assertThat(engineerOpt).isEmpty();
        });
    }

    /*
     ✅ This verifies your controller or exception handler properly maps a missing entity to 404.
        Must be more tests like that for other calls
    */
    @Test
    void shouldReturnNotFound_whenDeletingNonExistingEngineer() {
        // when
        String url = ENGINEERS + "/1";
        /*
         🧱 DELETE case:
            - testRestTemplate.delete(url) executes the delete
            - ❌ It doesn’t give you the response
                → You can’t tell if it was 204 No Content or 404 Not Found
            - ✅ Use exchange() to verify the HTTP status and headers
         */
        ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private SoftwareEngineerEntity createDefaultEngineer() {
        String name = "Pawa";
        List<String> stack = List.of("java", "spring");
        SoftwareEngineerToCreateDTO pawaEngineer = SoftwareEngineerToCreateDTO
                .builder()
                .name(name)
                .techStack(stack)
                .build();
        return softwareEngineerRepository.create(pawaEngineer);
    }

}
