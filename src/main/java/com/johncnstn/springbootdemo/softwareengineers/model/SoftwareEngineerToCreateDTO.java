package com.johncnstn.springbootdemo.softwareengineers.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO used when creating a new Software Engineer entity.
 * <p>
 * ⚠️ This is part of the "input boundary" in Clean Architecture.
 * It represents user-provided input and enforces validation constraints.
 * <p>
 * ❗ Validation Annotations:
 * - @NotNull: name must be provided (cannot be null)
 * - @NotBlank: name cannot be empty or whitespace-only
 * - @Size(min=2, max=10): name must be between 2 and 10 characters
 * <p>
 * 🔧 Note:
 * techStack is optional, defaults to an empty list if not provided
 */
public record SoftwareEngineerToCreateDTO(@NotBlank @NotNull @Size(min = 2, max = 10) String name,
                                          List<String> techStack) {

    /**
     * Builder pattern for convenient manual construction (e.g. in tests).
     * Ensures immutability and safe handling of nulls.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        // default immutable empty list
        private List<String> techStack = List.of();

        private Builder(){}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder techStack(List<String> techStack) {
            // Defensive copy to prevent mutation
            this.techStack = techStack == null ? List.of() : List.copyOf(techStack);
            return this;
        }

        public SoftwareEngineerToCreateDTO build() {
            return new SoftwareEngineerToCreateDTO(name, techStack);
        }
    }

}
