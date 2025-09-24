package com.johncnstn.springbootdemo.softwareengineers.controller;

import com.johncnstn.springbootdemo.softwareengineers.constants.ApiPaths;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerCommandService;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/*
 ✅ @RestController = @Controller + @ResponseBody → auto-serializes return values as JSON.
 ✅ @RequestMapping(ApiPaths.ENGINEERS)
     → Centralizes routing under a versioned API path (e.g., "/api/v1/software-engineers")
     → Promotes consistency and reuse across tests, docs, and clients
 ✅ @Validated enables method-level validation (e.g., @NotNull on @PathVariable)
*/
@Validated
@RestController
@RequestMapping(ApiPaths.ENGINEERS)
public class SoftwareEngineerController {

    private final SoftwareEngineerCommandService softwareEngineerCommandService;
    private final SoftwareEngineerQueryService softwareEngineerQueryService;

    public SoftwareEngineerController(SoftwareEngineerCommandService softwareEngineerCommandService,
                                      SoftwareEngineerQueryService softwareEngineerQueryService) {
        this.softwareEngineerCommandService = softwareEngineerCommandService;
        this.softwareEngineerQueryService = softwareEngineerQueryService;
    }

    /*
     ✅ @Valid on @RequestBody triggers bean validation (e.g., @NotBlank, @Size) inside the DTO.
     ✅ @NotNull ensures the request body itself is not null (extra safeguard).

     ✅ URI is built with ServletUriComponentsBuilder:
         - Context-safe (respects servlet path, version prefix)
         - RESTful (returns 201 Created + Location header pointing to the new resource)
    */
    @PostMapping
    public ResponseEntity<SoftwareEngineerDTO> createSoftwareEngineer(@NotNull
                                                                      @Valid
                                                                      @RequestBody
                                                                      SoftwareEngineerToCreateDTO softwareEngineerToCreateDTO) {
        var softwareEngineer = softwareEngineerCommandService.create(softwareEngineerToCreateDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(softwareEngineer.id())
                .toUri();
        return ResponseEntity.created(uri).body(softwareEngineer);
    }

    @GetMapping
    public ResponseEntity<List<SoftwareEngineerDTO>> getSoftwareEngineers() {
        var softwareEngineers = softwareEngineerQueryService.getAll();
        return ResponseEntity.ok(softwareEngineers);
    }

    /*
     ✅ Retrieves a single engineer by ID.
     ✅ @PathVariable + @NotNull: triggers validation if null is passed at runtime.
     ✅ Service throws EntityNotFoundException if not found → handled by global exception handler.
    */
    @GetMapping("/{id}")
    public ResponseEntity<SoftwareEngineerDTO> getSoftwareEngineer(@PathVariable @NotNull Integer id) {
        var softwareEngineer = softwareEngineerQueryService.getById(id);
        return ResponseEntity.ok(softwareEngineer);
    }

    /*
     ✅ RESTful full update using PUT:
         - Idempotent: multiple same PUTs = same result.
         - Doesn’t return a body, only status 204 No Content.
         - DTO is validated with @Valid.

     ✅ No ResponseEntity needed because:
         - No response body
         - @ResponseStatus is simpler and declarative
    */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateSoftwareEngineer(@PathVariable @NonNull Integer id,
                                       @NotNull
                                       @Valid
                                       @RequestBody
                                       SoftwareEngineerToUpdateDTO softwareEngineerToUpdateDTO) {
        softwareEngineerCommandService.update(id, softwareEngineerToUpdateDTO);
    }

    /*
     ✅ RESTful DELETE:
         - 204 No Content for successful deletion
         - Service layer throws if the entity is not found
         - Stateless: deletes the resource and nothing more
    */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSoftwareEngineer(@PathVariable @NotNull Integer id) {
        softwareEngineerCommandService.delete(id);
    }

}
