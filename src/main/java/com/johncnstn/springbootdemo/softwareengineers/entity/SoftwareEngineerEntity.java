package com.johncnstn.springbootdemo.softwareengineers.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

public class SoftwareEngineerEntity {

    private final Integer id;

    @NotBlank
    @NotNull
    @Size(min = 2, max = 10)
    private final String name;
    private final List<String> techStack;

    public SoftwareEngineerEntity(Integer id,
                                  String name,
                                  List<String> techStack) {
        this.id = id;
        this.name = name;
        this.techStack = techStack;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public static Builder builder() {
        return new Builder();
    }

    /*
        If private ->
            Calling SoftwareEngineerEntity.builder() will result in a compiler error,
                because the builder class is not visible.
     */
    public static class Builder {
        private Integer id;
        private String name;
        private List<String> techStack;

        // ❌ This won't compile because the constructor is private
        // SoftwareEngineerEntity.Builder builder = new SoftwareEngineerEntity.Builder();
        private Builder(){}

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        //  Builder can throw NPE silently if techStack is null. Fix:
        public Builder techStack(List<String> techStack) {
            this.techStack = techStack == null ? List.of() : List.copyOf(techStack);
            return this;
        }

        public SoftwareEngineerEntity build() {
            return new SoftwareEngineerEntity(id, name, techStack);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SoftwareEngineerEntity that = (SoftwareEngineerEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
                Objects.equals(techStack, that.techStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, techStack);
    }

    @Override
    public String toString() {
        return "SoftwareEngineerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", techStack=" + techStack +
                '}';
    }

}
