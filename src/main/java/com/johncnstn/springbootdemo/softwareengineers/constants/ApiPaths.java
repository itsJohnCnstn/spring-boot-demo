package com.johncnstn.springbootdemo.softwareengineers.constants;

/*
 ✅ Utility class for defining centralized REST API paths.
 ✅ Declared abstract with a private constructor to prevent instantiation and inheritance.

 🧠 Why static constants here:
    - Promotes consistency across controllers, tests, docs
    - Avoids scattering hardcoded strings
    - Makes global path refactors (e.g., version upgrades) trivial
*/
public abstract class ApiPaths {
    private ApiPaths() {
    }

    public static final String BASE = "/api/v1";
    public static final String ENGINEERS = BASE + "/software-engineers";
}
