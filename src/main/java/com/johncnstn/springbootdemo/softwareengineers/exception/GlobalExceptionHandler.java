package com.johncnstn.springbootdemo.softwareengineers.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/*
 ✅ @RestControllerAdvice = global error handler for REST controllers
    - Handles exceptions thrown by any @RestController
    - Converts them to structured JSON (ProblemDetail in Spring 6+)

 ✅ Key concept: ProblemDetail (new in Spring 6)
    - Instead of returning plain strings or generic messages
    - We return JSON with `title`, `status`, `detail`, `instance`, etc.

 ✅ Why useful?
    - Makes error responses machine-readable
    - Can include custom metadata (e.g. timestamp, code, etc.)
    - Aligns with HTTP API best practices (RFC 7807)
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
    ✅ Catch-all for all other exceptions (fail-safe handler)
     - Useful to avoid exposing stack traces to users
     - Still returns a structured error message (500)

     🧠 What we return:
     {
       "title": "Internal Server Error",
       "status": 500,
       "detail": "Unexpected error occurred",
       "instance": "/api/v1/software-engineers/5",
       "timestamp": "...",
       "exception": "NullPointerException",
       "code": "INTERNAL_SERVER_ERROR"
     }

     ✅ Useful for observability + consistent client behavior
    */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGenericException(HttpServletRequest request, Exception ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Unexpected error occurred");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("exception", ex.getClass().getSimpleName());
        problem.setProperty("code", ErrorCode.INTERNAL_SERVER_ERROR);

        LOGGER.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return problem;
    }

    /*
     ✅ Handles a case when an entity is not found in the DB
     - Triggers when the controller/service throws EntityNotFoundException
     - We respond with 404 and meaningful JSON for frontend/client

     🧠 What we return:
     {
       "title": "Entity Not Found",
       "status": 404,
       "detail": "No engineer with id: 5",
       "instance": "/api/v1/software-engineers/5",
       "timestamp": "2025-07-29T11:08:00Z",
       "code": "SOFTWARE_ENGINEER_NOT_FOUND"
     }
    */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFoundException(HttpServletRequest request, Exception ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Entity Not Found");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("code", ErrorCode.SOFTWARE_ENGINEER_NOT_FOUND);

        LOGGER.warn("Entity not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return problem;
    }

}
