package com.johncnstn.springbootdemo.softwareengineers.service.impl;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.exception.EntityNotFoundException;
import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 ✅ This class is the **Query** side of the CQRS pattern.
    → Focuses purely on **read operations**: fetching all or fetching by ID.
    → Helps isolate logic for querying from mutation logic in CommandService.
*/
@Service
public class SoftwareEngineerQueryServiceImpl implements SoftwareEngineerQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(SoftwareEngineerQueryServiceImpl.class);

    private final SoftwareEngineerRepository softwareEngineerRepository;
    private final SoftwareEngineerMapper softwareEngineerMapper;

    public SoftwareEngineerQueryServiceImpl(SoftwareEngineerRepository softwareEngineerRepository,
                                            SoftwareEngineerMapper softwareEngineerMapper) {
        this.softwareEngineerRepository = softwareEngineerRepository;
        this.softwareEngineerMapper = softwareEngineerMapper;
    }

    /*
     ✅ Returns all engineers:
        - Logs size of result set (good practice for diagnostics).

     🔄 Can be extended with pagination or filtering in real systems.
    */
    @Override
    public List<SoftwareEngineerDTO> getAll() {
        var softwareEngineers = softwareEngineerRepository.getAll();
        var softwareEngineerDTOs = softwareEngineers.stream().map(softwareEngineerMapper::toDTO).toList();
        LOG.debug("Fetched {} engineers", softwareEngineerDTOs.size());
        return softwareEngineerDTOs;
    }

    /*
     ✅ Gets a single engineer by ID:
        - Uses shared `getByIdOrThrow()` to handle lookup and exception.

     🔁 Follows DRY principle by not duplicating lookup logic here.
    */
    @Override
    public SoftwareEngineerDTO getById(Integer id) {
        var softwareEngineer = getByIdOrThrow(id);
        var softwareEngineerDTO = softwareEngineerMapper.toDTO(softwareEngineer);
        LOG.debug("Fetched engineer: {}", softwareEngineerDTO);
        return softwareEngineerDTO;
    }

    /*
     ✅ Centralized method for "find or fail":
        - Returns entity if found.
        - Otherwise throws a custom `EntityNotFoundException` with clean message.
        - Logs missing case for observability.

     🔄 Good practice: helps avoid NPEs in upper layers and promotes fail-fast behavior.
    */
    @Override
    public SoftwareEngineerEntity getByIdOrThrow(Integer id) {
        return softwareEngineerRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warn("Engineer not found: id={}", id);
                    return new EntityNotFoundException("No engineer with id: %d".formatted(id));
                });
    }
}
