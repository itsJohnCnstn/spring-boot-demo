package com.johncnstn.springbootdemo.softwareengineers.mapper;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import org.springframework.stereotype.Component;

/*
 🔄 This class serves as a manual mapping layer between internal domain models (entities) and external-facing DTOs.

 ✅ Why use a mapper class at all?
    - **Separation of concerns**: isolates transformation logic away from service/controller layers.
    - Keeps your domain model clean from serialization details.
    - Supports **flexibility** in the future (e.g., if DTO shape diverges from entity).
    - Central point to add transformation logic (e.g., date formatting, enum conversion, derived fields).

 ✅ Why not use libraries like MapStruct or ModelMapper?
    - Manual mapping is:
        • More **explicit and predictable**
        • Easier to debug
        • Safer for refactors (compile-time checked)
    - Ideal for small/medium projects where automatic mappers are overkill

 ✅ Why annotate with @Component?
    - Makes the mapper available as a Spring bean for **dependency injection**
    - Promotes **testability** and **reuse** in service layers

 🧠 Notes:
    - You typically map:
        1. DTO → Entity (for create/update persistence)
        2. Entity → DTO (for API responses)

 📦 `toDTO(...)`
    - Converts internal entity into a DTO to be returned via API or service call

 📦 `toEntity(...)`
    - Combines external input (e.g., `SoftwareEngineerToCreateDTO`) with backend-generated `id`
    - Constructs a clean domain entity ready to persist or process internally

 💡 Design tip:
    - If mappings get complex, consider separate mappers for each direction.
    - Avoid using DTOs directly inside your domain logic – treat them as boundaries between layers.
*/
@Component
public class SoftwareEngineerMapper {
    public SoftwareEngineerDTO toDTO(SoftwareEngineerEntity softwareEngineer) {
        return SoftwareEngineerDTO
                .builder()
                .id(softwareEngineer.getId())
                .name(softwareEngineer.getName())
                .techStack(softwareEngineer.getTechStack())
                .build();
    }

    public SoftwareEngineerEntity toEntity(Integer id, SoftwareEngineerToCreateDTO softwareEngineerToCreateDTO) {
        return SoftwareEngineerEntity.builder()
                .id(id)
                .name(softwareEngineerToCreateDTO.name())
                .techStack(softwareEngineerToCreateDTO.techStack())
                .build();
    }
}
