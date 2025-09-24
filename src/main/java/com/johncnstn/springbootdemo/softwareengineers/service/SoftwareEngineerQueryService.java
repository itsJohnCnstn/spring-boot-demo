package com.johncnstn.springbootdemo.softwareengineers.service;

import com.johncnstn.springbootdemo.softwareengineers.entity.SoftwareEngineerEntity;
import com.johncnstn.springbootdemo.softwareengineers.model.SoftwareEngineerDTO;

import java.util.List;

/*
 ⛳ PURPOSE:
     - Defines **read-only operations** on Software Engineers (retrieval, lookup).
     - Keeps reads and writes separate (CQRS), increasing flexibility.

 🤖 WHY AN INTERFACE IN SPRING?

 ✅ 1. Clean Separation of Concerns
     → HOW? This interface contains only read methods.
     → WHY? Avoids accidental mutations or coupling query logic with state changes.

 ✅ 2. Enables Lightweight JDK Proxying for AOP
     → HOW? With this interface, Spring can use dynamic proxying to wrap query methods with:
         - @Cacheable (caching)
         - @Transactional(readOnly = true)
         - Metrics (e.g., execution timing)
     → WHY? JDK proxies don’t require subclassing the implementation class, avoiding CGLIB's quirks like:
         - Failing on final classes/methods
         - Slower proxy creation
         - Risky bytecode manipulation

 ✅ 3. Better Unit Testing
     → HOW? Interfaces are mock-friendly — no Spring context needed.
     → WHY? Isolated tests are faster, safer, and don’t require mocking internal beans (like mappers or repositories).
        → You don’t need to load Spring context, mock internal beans, or deal with AOP proxies
        → You can `mock(SoftwareEngineerQueryService.class)` in a unit test
        → @Mock from Mockito instead of @MockitoBean from Spring

 ✅ 4. Future Scalability
     → HOW? Easy to plug in:
         - A caching layer (e.g., `CachingQueryService`)
         - A remote query implementation (e.g., via HTTP or GraphQL)
         - Read replicas or distributed stores (e.g., Elasticsearch, ClickHouse)
     → WHY? Consumers don’t need to change because the interface remains stable.

 ✅ 5. Decorator Pattern for Cross-Cutting Concerns
     → HOW? We can chain implementations like:
         `new LoggingQueryService(new CachingQueryService(new QueryServiceImpl(...)))`
     → WHY? No need to pollute core logic with concerns like metrics, logging, or authorization.

*/
public interface SoftwareEngineerQueryService {
    List<SoftwareEngineerDTO> getAll();

    SoftwareEngineerDTO getById(Integer id);

    SoftwareEngineerEntity getByIdOrThrow(Integer id);
}
