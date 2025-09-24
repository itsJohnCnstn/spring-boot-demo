package com.johncnstn.springbootdemo.softwareengineers.repository;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.mapper.SoftwareEngineerMapper;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SoftwareEngineerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareEngineerRepository.class);

    private Integer id;
    private final Map<Integer, SoftwareEngineerEntity> softwareEngineersMap;

    private final SoftwareEngineerMapper softwareEngineerMapper;

    public SoftwareEngineerRepository(SoftwareEngineerMapper softwareEngineerMapper) {
        this.id = 0;
        softwareEngineersMap = new HashMap<>();
        this.softwareEngineerMapper = softwareEngineerMapper;

//        id++;
//        var firstSoftwareEngineer = new SoftwareEngineerEntity(
//                id,
//                "Pawa",
//                List.of("java", "spring")
//        );
//        softwareEngineersMap.put(id, firstSoftwareEngineer);
//
//        id++;
//        var secondSoftwareEngineer = new SoftwareEngineerEntity(
//                id,
//                "Miha",
//                List.of("java", "kotlin", "spring")
//        );
//        softwareEngineersMap.put(id, secondSoftwareEngineer);
    }

    // return from map.put() returns the previous value!
    public SoftwareEngineerEntity create(SoftwareEngineerToCreateDTO softwareEngineerToCreateDTO) {
        id++;
        var engineerToCreate = softwareEngineerMapper.toEntity(id, softwareEngineerToCreateDTO);
        softwareEngineersMap.put(id, engineerToCreate);
        var createdEngineer = softwareEngineersMap.get(id);
        LOGGER.info("Created software engineer: {}", createdEngineer);
        return createdEngineer;
    }

    public Optional<SoftwareEngineerEntity> findById(Integer id) {
        var foundEngineer = softwareEngineersMap.get(id);
        LOGGER.info("Found software engineer: {} with id: {}", foundEngineer, id);
        return Optional.ofNullable(foundEngineer);
    }

    public List<SoftwareEngineerEntity> getAll() {
        var softwareEngineers = softwareEngineersMap.values().stream().toList();
        LOGGER.info("Got: {} software engineers", softwareEngineers.size());
        return softwareEngineers;
    }

    public void update(Integer id, SoftwareEngineerToUpdateDTO softwareEngineerToUpdateDTO) {
        var engineerToUpdate = SoftwareEngineerEntity.builder()
                .id(id)
                .name(softwareEngineerToUpdateDTO.name())
                .techStack(softwareEngineerToUpdateDTO.techStack())
                .build();
        softwareEngineersMap.put(id, engineerToUpdate);
        var updatedEngineer = softwareEngineersMap.get(id);
        LOGGER.info("Updated software engineer: {}", updatedEngineer);
    }

    public void delete(Integer id) {
        var deletedEngineer = softwareEngineersMap.remove(id);
        LOGGER.info("Deleted software engineer: {}", deletedEngineer);
    }

    public void deleteAll() {
        softwareEngineersMap.clear();
    }

}
