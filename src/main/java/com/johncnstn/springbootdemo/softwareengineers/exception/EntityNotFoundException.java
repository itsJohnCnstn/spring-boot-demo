package com.johncnstn.springbootdemo.softwareengineers.exception;

/*
 🚫 Custom exception to signal a missing entity (e.g., software engineer with given ID not found)

 ✅ Why extend RuntimeException (unchecked)?
     - Doesn't require callers to explicitly handle it (`throws` not enforced)
     - Suitable for programming errors or unexpected states (e.g., DB integrity violations, bad ID input)
     - Clean integration with Spring’s `@ExceptionHandler` or `@ControllerAdvice` for global error mapping

 ✅ Why not use IllegalArgumentException or NoSuchElementException?
     - This custom type improves **semantic clarity** (you know it’s about entity lookup)
     - Allows precise targeting in exception handlers (e.g., show 404 instead of 400)
     - Enables reusable, domain-specific error handling (e.g., for REST APIs or service layers)

 ✅ Why add constructor with `(String message, Throwable cause)`?
     - 🔁 Enables **exception chaining** (preserves the original root cause):
         e.g., wrapping a `SQLException` inside your custom exception
         ```java
         try {
             repository.findById(id);
         } catch (SQLException e) {
             throw new EntityNotFoundException("DB failed to find entity id: " + id, e);
         }
         ```
     - 📜 Improves debugging: stack traces show the **root cause** alongside your domain-specific message
     - 🧰 Allows better logging and error analysis in production systems
     - 🧪 In tests, lets you simulate wrapped exceptions for advanced validation

 ✅ Design tip: always include both constructors in custom exceptions:
     - `EntityNotFoundException(String message)`
     - `EntityNotFoundException(String message, Throwable cause)`

 🔁 Typical usage:
     throw new EntityNotFoundException("No software engineer with id: " + id);

 🧪 In tests:
     assertThrows(EntityNotFoundException.class, () -> service.getById(99));
*/
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
