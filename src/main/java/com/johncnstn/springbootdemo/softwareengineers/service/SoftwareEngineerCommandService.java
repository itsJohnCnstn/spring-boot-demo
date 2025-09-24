package com.johncnstn.springbootdemo.softwareengineers.service;

import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToCreateDTO;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerToUpdateDTO;

/*
 ⛳ PURPOSE:
     - Defines the contract for all **mutating operations** (create, update, delete) on Software Engineers.
     - Used by the controller to abstract persistence and business logic.

 🤖 WHY AN INTERFACE IN SPRING?

 ✅ 1. Decoupling via Dependency Inversion (SOLID: DIP)
     → Controllers depend on abstractions, not implementations.
     → HOW? By injecting this interface, we can swap out its implementation (e.g., with KafkaCommandServiceImpl) without changing the controller.
     → WHY? Promotes loose coupling, easier testing, and flexible architecture.

 ✅ 2. Makes JDK Dynamic Proxies Possible
     → HOW? Spring AOP can generate proxies around interfaces using Java's built-in dynamic proxy mechanism (`Proxy.newProxyInstance()`).
     → WHY? JDK proxies are lighter and safer because:
         - They don't require subclassing
         - Work even if the implementation class is `final`
         - Don't break `equals`, `hashCode`, or `toString` like CGLIB sometimes can
     → Alternative (without interface): Spring falls back to CGLIB, which generates subclasses — less efficient and more error-prone.

 ✅ 3. Testability without Spring Context
     → HOW? We can pass mocks (e.g., Mockito mocks) into controller tests:
         `given(service.create(...)).willReturn(...);`
     → WHY? This avoids loading the full Spring context (with `@SpringBootTest`), so tests are:
         - 10–100x faster
         - More isolated
         - More deterministic (no bean lifecycle or auto-wiring surprises)

 ✅ 4. Future-Proofing with Decorators and Middleware
     → HOW? Want to log execution time? Wrap the interface in a `LoggingCommandServiceDecorator`.
     → Want retries? Use a `RetryingCommandServiceDecorator`.
     → WHY? All this becomes trivial with an interface — we can compose behaviour dynamically
        (e.g., via Spring @Bean method returning a decorator chain),
        instead of being locked into a monolithic implementation.

 ✅ 5. Scales Well With CQRS
     → HOW? Split reads (QueryService) from writes (CommandService).
     → WHY? Enables eventual consistency, event sourcing, read replicas, and performance optimisations —
        none of which require redesigning your core controller logic.

 ✅ 6. Decorator Pattern for Cross-Cutting Concerns
     → HOW? We can chain implementations like:
         `new LoggingQueryService(new CachingQueryService(new QueryServiceImpl(...)))`
     → WHY? There is no need to pollute core logic with concerns such as metrics, logging, or authorisation.

*/
public interface SoftwareEngineerCommandService {

    SoftwareEngineerDTO create(SoftwareEngineerToCreateDTO softwareEngineerToCreateDTO);

    void update(Integer id, SoftwareEngineerToUpdateDTO softwareEngineerToUpdateDTO);

    void delete(Integer id);
}