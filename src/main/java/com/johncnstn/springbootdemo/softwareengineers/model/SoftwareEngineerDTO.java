package com.johncnstn.springbootdemo.softwareengineers.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/*
 ✅ Read-only Data Transfer Object (DTO) for returning software engineer data via REST API.

 🧠 Why use a `record`:
    - Concise syntax for immutable value objects
    - Auto-generates `equals()`, `hashCode()`, `toString()`
    - Great fit for read-only responses (as opposed to mutable command objects)

 ✅ Validation:
    - @NotNull on `id` ensures it's never null when sending data to clients
    - @NotBlank @NotNull @Size(min = 2, max = 10) on `name` make it consistent when reading after saving
    - Avoids NPEs in consumers like frontend or other services
    - Deep validation (e.g., `@Size`, `@NotBlank`) should be on create/update DTOs
*/
public record SoftwareEngineerDTO(@NotNull Integer id,
                                  @NotBlank @NotNull @Size(min = 2, max = 10) String name,
                                  List<String> techStack) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private List<String> techStack = List.of();

        private Builder(){}

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder techStack(List<String> techStack) {
            this.techStack = techStack == null ? List.of() : List.copyOf(techStack);
            return this;
        }

        public SoftwareEngineerDTO build() {
            return new SoftwareEngineerDTO(id, name, techStack);
        }
    }

}
