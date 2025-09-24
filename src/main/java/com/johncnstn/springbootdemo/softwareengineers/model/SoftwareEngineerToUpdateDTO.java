package com.johncnstn.springbootdemo.softwareengineers.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/*
 ✅ This DTO (Data Transfer Object) is used to **fully update** an existing Software Engineer entity.
    → Used in `PUT` requests (unlike PATCH, which is partial).

 ✅ Why use a dedicated DTO instead of reusing the entity?
    - Keeps input validation and domain logic separate.
    - Prevents exposing internal fields (e.g., database IDs, timestamps).
    - Enables stricter control over what clients are allowed to modify.

 ✅ Validation annotations:
    - `@NotBlank`, `@NotNull`, `@Size(min = 2, max = 10)` on `name`:
        → Ensures non-null, non-empty, and length-constrained values at runtime.
        → Helps fail fast before service/repository layers are invoked.
    - No validation on `techStack`: it's optional and can be empty.

 📌 Why a `record`?
    - Immutable by design: fields can't be changed once created.
    - Reduces boilerplate (no need for getters, constructors, `equals`, etc.).
    - Works well with the builder pattern to handle more complex creation scenarios.

 ✅ Builder pattern for manual instantiation (optional, but helpful):
    - Supports fluent DSL-style creation, e.g.:
          SoftwareEngineerToUpdateDTO.builder().name("Anna").techStack(List.of("Java")).build();
    - `List.copyOf(...)` ensures defensive immutability — caller can’t modify internal state.
    - Defaults to `List.of()` if null → avoids null pointer exceptions and simplifies logic.

 📈 Design Tradeoffs:
    - ❌ No ID: by REST convention, resource ID comes from the URL path (e.g., `/engineers/{id}`).
    - ✅ `record + builder` strikes a balance between immutability and flexibility.

 🔄 When used?
    - In the controller: maps to `@RequestBody SoftwareEngineerToUpdateDTO`
    - In the service: passed to update logic to mutate an existing entity
    - Always represents the complete state to **replace** the current resource (vs PATCH, which only contains changed fields)
*/
public record SoftwareEngineerToUpdateDTO(@NotBlank @NotNull @Size(min = 2, max = 10) String name,
                                          List<String> techStack) {

    // ✅ Static factory method for starting a fluent, readable, and safe object construction process.
    // 🔹 Why not just use `new SoftwareEngineerToUpdateDTO(...)`?
    //    → When records or constructors have multiple fields, the builder pattern improves readability.
    //    → Makes it easy to construct objects from partially available data (e.g., user input, form).
    //    → Especially useful if the class grows or becomes conditional.
    public static Builder builder() {
        return new Builder();
    }

    /*
     ✅ Builder class is a creational helper to incrementally and clearly construct a SoftwareEngineerToUpdateDTO.

     🔍 Why use it even for a record?
        - Records are concise and immutable, but they expose a canonical constructor,
          which can become hard to use when there are many fields or validations.
        - The builder avoids long argument lists, enforces defaults, and lets you use named methods instead of relying on parameter order.

     🔐 Encapsulates input safety:
        - Defaults `techStack` to `List.of()` to avoid null pointer issues (defensive programming).
        - Applies `List.copyOf(...)` to create an **immutable copy**: callers can't mutate internal state after the DTO is built.
          → This aligns with record immutability.

     ⚠️ Why not Lombok @Builder?
        - Manual builder gives you full control (e.g., null-safe defaults, copy strategies).
        - Avoids IDE/autowiring issues with Lombok in some environments.
        - Easier to understand for learning purposes.

     📦 Output:
        - `build()` assembles and returns the actual DTO.
        - It allows a controlled, validated way to produce the immutable object.

     ✅ 'public' makes the Builder usable outside this class (e.g., from controllers, tests).
        SoftwareEngineerToUpdateDTO dto = SoftwareEngineerToUpdateDTO.builder()
            .name("Alice")
            .techStack(List.of("Java", "Spring"))
            .build();
        If you made Builder private or package-private (default),
        the above usage would not compile from other packages (e.g., from controllers, services, tests).
     ✅ 'static' allows using the Builder without requiring an instance of the outer DTO class.
        Without static, you would need an instance of SoftwareEngineerToUpdateDTO to create a builder,
        which defeats the purpose
    */
    public static class Builder {
        private String name;
        // Defensive default to avoid nulls
        private List<String> techStack = List.of();

        private Builder(){}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        // Fluent setter for the tech stack
        // ✅ Null-safe: replaces null with an empty list
        // ✅ Immutable: wraps input with `List.copyOf(...)`
        public Builder techStack(List<String> techStack) {
            this.techStack = techStack == null ? List.of() : List.copyOf(techStack);
            return this;
        }

        // 🎯 Builds the immutable DTO using the captured state
        public SoftwareEngineerToUpdateDTO build() {
            return new SoftwareEngineerToUpdateDTO(name, techStack);
        }
    }

}
