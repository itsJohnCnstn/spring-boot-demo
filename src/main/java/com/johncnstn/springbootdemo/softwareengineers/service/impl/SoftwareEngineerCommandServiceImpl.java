package com.johncnstn.springbootdemo.softwareengineers.service.impl;

import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;
import com.johncnstn.springbootdemo.softwareengineers.repository.SoftwareEngineerRepository;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerCommandService;
import com.johncnstn.springbootdemo.softwareengineers.service.SoftwareEngineerQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 ✅ This class implements the Command part of the CQRS (Command Query Responsibility Segregation) pattern.
    → It handles all **write operations**: create, update, delete.
    → Separation from the QueryService allows clearer responsibilities and testability (no modifications
        in query).
*/
@Service
public class SoftwareEngineerCommandServiceImpl implements SoftwareEngineerCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(SoftwareEngineerCommandServiceImpl.class);

    private final SoftwareEngineerQueryService softwareEngineerQueryService;
    private final SoftwareEngineerRepository softwareEngineerRepository;
    private final SoftwareEngineerMapper softwareEngineerMapper;

    public SoftwareEngineerCommandServiceImpl(SoftwareEngineerQueryService softwareEngineerQueryService,
                                              SoftwareEngineerRepository softwareEngineerRepository,
                                              SoftwareEngineerMapper softwareEngineerMapper) {
        this.softwareEngineerQueryService = softwareEngineerQueryService;
        this.softwareEngineerRepository = softwareEngineerRepository;
        this.softwareEngineerMapper = softwareEngineerMapper;
    }

    /*
     ✅ Handles engineer creation:
        - Takes a "to-create" DTO (no ID) to keep input safe.

     🔄 Good practice: Log only after success to avoid noise and include key fields like ID or name.
    */
    @Override
    public SoftwareEngineerDTO create(SoftwareEngineerToCreateDTO softwareEngineerToCreateDTO) {
        var createdSoftwareEngineer = softwareEngineerRepository.create(softwareEngineerToCreateDTO);
        var softwareEngineerDTO = softwareEngineerMapper.toDTO(createdSoftwareEngineer);
        LOG.info("Created SoftwareEngineer: {}", softwareEngineerDTO);
        return softwareEngineerDTO;
    }

    /*
     ✅ Handles full update:
        - No return type → consistent with RESTful `PUT` behavior.

     ❗ Tip: Always validate presence before update to avoid silent failures or unintended creations.
    */
    @Override
    public void update(Integer id, SoftwareEngineerToUpdateDTO softwareEngineerToUpdateDTO) {
        softwareEngineerQueryService.getByIdOrThrow(id);
        softwareEngineerRepository.update(id, softwareEngineerToUpdateDTO);
        LOG.info("Updated SoftwareEngineer: id={}", id);
    }

    /*
     ✅ Minimalist and safe:
        - Only deletes if the entity is confirmed to exist.
    */
    @Override
    public void delete(Integer id) {
        softwareEngineerQueryService.getByIdOrThrow(id);
        softwareEngineerRepository.delete(id);
        LOG.info("Deleted SoftwareEngineer: id={}", id);
    }
}