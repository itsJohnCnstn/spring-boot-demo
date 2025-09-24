package com.johncnstn.springbootdemo.softwareengineers.exception;

/*
 🔥 Enum used to **categorize and structure application errors** across layers (controller → service → repository).

 ✅ Why define a custom ErrorCode enum?
    - Promotes **consistency** in error handling and logging.
    - Makes it easier to build structured API responses (e.g., with fields like `code`, `title`, `detail`).
    - Useful for:
        • Clients (e.g., frontend) to interpret error types programmatically.
        • Internal dashboards / logs for filtering by known issues.
    - Safer and more maintainable than raw string error codes.
    - Enum values are **compile-time safe**, refactorable, and auto-completable.

 ✅ Common usage:
    - Wrap inside a custom `ErrorResponse` object alongside a human-readable `message`
    - Attach to exceptions via custom fields (e.g., `EntityNotFoundException extends RuntimeException { ErrorCode code; }`)
    - Serialize into JSON for frontend or external system clients

 📌 Recommended conventions:
    - Use **UPPER_SNAKE_CASE** for error codes (matches HTTP status conventions and JSON best practices).
    - Separate internal vs. external messages (e.g., `INTERNAL_SERVER_ERROR` shown to user vs. stack trace for logs).
    - Group by domain (e.g., `SOFTWARE_ENGINEER_NOT_FOUND`, `SOFTWARE_ENGINEER_VALIDATION_FAILED`)

 📈 Benefits:
    - Improves **observability** (e.g., dashboards filtering on `SOFTWARE_ENGINEER_NOT_FOUND`)
    - Aids **internationalization** (clients can map `ErrorCode` to localized messages)
    - Helps enforce **domain boundaries** in service layer

 ❗️In production apps, this enum can grow large. Consider grouping by domain using inner enums or prefixing.
*/
public enum ErrorCode {
    INTERNAL_SERVER_ERROR,
    SOFTWARE_ENGINEER_NOT_FOUND
}
