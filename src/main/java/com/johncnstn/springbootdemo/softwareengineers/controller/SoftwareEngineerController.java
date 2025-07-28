package com.johncnstn.springbootdemo.softwareengineers.controller;

import com.johncnstn.springbootdemo.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.exception.EntityNotFoundException;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(SoftwareEngineerController.SOFTWARE_ENGINEERS_PATH)
public class SoftwareEngineerController {

    protected static final String BASE_PATH = "/api/v1";
    protected static final String SOFTWARE_ENGINEERS_PATH = BASE_PATH + "/software-engineers";
    private final SoftwareEngineerRepository softwareEngineerRepository;

    public SoftwareEngineerController(SoftwareEngineerRepository softwareEngineerRepository) {
        this.softwareEngineerRepository = softwareEngineerRepository;
    }

    @PostMapping
    public ResponseEntity<SoftwareEngineerEntity> createSoftwareEngineer(@NotNull
                                                                         @Valid
                                                                         @RequestBody
                                                                         SoftwareEngineerDTO softwareEngineerDTO) {
        var createdSoftwareEngineer = softwareEngineerRepository.create(softwareEngineerDTO);
        var uri = URI.create(SOFTWARE_ENGINEERS_PATH + "/" + createdSoftwareEngineer.getId());
        return ResponseEntity.created(uri).body(createdSoftwareEngineer);
    }

    @GetMapping
    public ResponseEntity<List<SoftwareEngineerEntity>> getSoftwareEngineers() {
        var softwareEngineers = softwareEngineerRepository.getAll();
        return ResponseEntity.ok(softwareEngineers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoftwareEngineerEntity> getSoftwareEngineer(@PathVariable @NotNull Integer id) {
        var softwareEngineer = findSoftwareEngineerByIdOrThrow(id);
        return ResponseEntity.ok(softwareEngineer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoftwareEngineerEntity> updateSoftwareEngineer(@PathVariable @NonNull Integer id,
                                                                         @NotNull
                                                                         @Valid
                                                                         @RequestBody
                                                                         SoftwareEngineerDTO softwareEngineerDTO) {
        findSoftwareEngineerByIdOrThrow(id);
        var updatedSoftwareEngineer = softwareEngineerRepository.update(id, softwareEngineerDTO);
        return ResponseEntity.ok(updatedSoftwareEngineer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoftwareEngineer(@PathVariable @NotNull Integer id) {
        findSoftwareEngineerByIdOrThrow(id);
        softwareEngineerRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SoftwareEngineerEntity findSoftwareEngineerByIdOrThrow(Integer id) {
        return softwareEngineerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No engineer with id: %d".formatted(id)));
    }

}
