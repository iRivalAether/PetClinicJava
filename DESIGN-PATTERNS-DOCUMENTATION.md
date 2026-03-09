# 🎨 Refactorización con Patrones de Diseño - Spring PetClinic

## 📋 Resumen Ejecutivo

Este documento describe la refactorización completa del proyecto Spring PetClinic aplicando patrones de diseño GoF (Gang of Four) y patrones arquitectónicos empresariales. La refactorización mejora la **mantenibilidad**, **escalabilidad**, **testabilidad** y **flexibilidad** del código.

---

## 🎯 Patrones Implementados

### 1. **SERVICE LAYER PATTERN** (Patrón Arquitectónico)
### 2. **DTO PATTERN** + **MAPPER PATTERN** (Patrones de Transferencia de Datos)
### 3. **FACTORY PATTERN** + **BUILDER PATTERN** (Patrones Creacionales)
### 4. **STRATEGY PATTERN** (Patrón de Comportamiento)
### 5. **DEPENDENCY INJECTION** (Patrón de IoC - ya existente, mejorado)
### 6. **FACADE PATTERN** (Patrón Estructural - implícito en servicios)

---

## 📚 PATRÓN 1: SERVICE LAYER PATTERN

### ¿Qué es?
Una capa intermedia entre los controladores y los repositorios que encapsula la lógica de negocio.

### ¿Por qué?
**PROBLEMA ORIGINAL:**
```java
// ❌ ANTES: Controlador accediendo directamente al repositorio
@Controller
class OwnerController {
    private final OwnerRepository owners;
    
    @PostMapping("/owners/new")
    public String create(@Valid Owner owner) {
        this.owners.save(owner);  // Lógica de negocio en el controlador
        return "redirect:/owners/" + owner.getId();
    }
}
```

**PROBLEMAS:**
- ❌ Lógica de negocio mezclada con lógica de presentación
- ❌ Difícil de testear (necesitas un contexto web)
- ❌ Lógica duplicada si tienes API REST + MVC
- ❌ Violación del Principio de Responsabilidad Única
- ❌ No hay transacciones coherentes

**SOLUCIÓN CON SERVICE LAYER:**
```java
// ✅ DESPUÉS: Servicio encapsula la lógica de negocio
@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository ownerRepository;
    
    public Owner createOwner(Owner owner) {
        validateOwner(owner);  // Validación de negocio
        // Aquí podrías: enviar email, crear log de auditoría, etc.
        return ownerRepository.save(owner);
    }
}

// Controlador solo maneja HTTP
@Controller
class OwnerController {
    private final OwnerService ownerService;
    
    @PostMapping("/owners/new")
    public String create(@Valid Owner owner) {
        Owner created = ownerService.createOwner(owner);
        return "redirect:/owners/" + created.getId();
    }
}
```

**BENEFICIOS:**
- ✅ Separación de responsabilidades (SRP)
- ✅ Lógica reutilizable entre MVC y REST
- ✅ Fácil de testear con mocks
- ✅ Transacciones bien definidas
- ✅ Centralización de reglas de negocio

### Archivos Creados:
```
src/main/java/.../owner/service/
├── OwnerService.java           # Interfaz del servicio
├── OwnerServiceImpl.java       # Implementación
├── PetService.java
├── PetServiceImpl.java
├── VisitService.java
└── VisitServiceImpl.java
```

### Uso:
```java
// En cualquier parte de la aplicación
Owner owner = ownerService.findById(1).orElseThrow();
Owner updated = ownerService.updateOwner(1, ownerData);
```

---

## 📚 PATRÓN 2: DTO PATTERN + MAPPER PATTERN

### ¿Qué es?
**DTO (Data Transfer Object):** Objeto simple que transporta datos entre procesos.  
**Mapper:** Convierte entre entidades de dominio y DTOs.

### ¿Por qué?
**PROBLEMA ORIGINAL:**
```java
// ❌ ANTES: Exponiendo entidades directamente en la API
@GetMapping("/api/owners/{id}")
public Owner getOwner(@PathVariable int id) {
    return ownerRepository.findById(id).orElseThrow();
}
```

**PROBLEMAS:**
- ❌ Expone estructura interna de la base de datos
- ❌ Puede causar lazy loading exceptions en JSON
- ❌ Difícil versionar la API
- ❌ Puede exponer datos sensibles
- ❌ Carga innecesaria de datos (over-fetching)
- ❌ Dependencia directa entre API y modelo de persistencia

**SOLUCIÓN CON DTOs:**
```java
// ✅ DTO optimizado para la API
public class OwnerDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String fullName;  // Campo computado
    private Integer petCount;  // Campo agregado
    private List<PetSummaryDTO> pets;  // Solo resumen de mascotas
    // Sin exponer detalles internos de persistencia
}

// Mapper centraliza la conversión
@Component
public class OwnerMapper {
    public OwnerDTO toDTO(Owner entity) {
        OwnerDTO dto = new OwnerDTO();
        dto.setFullName(entity.getFirstName() + " " + entity.getLastName());
        dto.setPetCount(entity.getPets().size());
        return dto;
    }
}

// Controlador usa DTOs
@GetMapping("/api/owners/{id}")
public ResponseEntity<OwnerDTO> getOwner(@PathVariable int id) {
    Owner owner = ownerService.findById(id).orElseThrow();
    OwnerDTO dto = ownerMapper.toDTO(owner);
    return ResponseEntity.ok(dto);
}
```

**BENEFICIOS:**
- ✅ API desacoplada del modelo de dominio
- ✅ Versionado fácil (OwnerDTOv2 sin cambiar entidades)
- ✅ Control sobre qué datos se exponen
- ✅ Optimización de payload (campos computados)
- ✅ Previene lazy loading exceptions
- ✅ Permite diferentes representaciones para diferentes contextos

### Archivos Creados:
```
src/main/java/.../owner/
├── dto/
│   ├── OwnerDTO.java           # DTO con campos optimizados
│   └── OwnerMapper.java        # Mapper entre Entity <-> DTO
└── api/
    └── OwnerRestController.java  # REST API usando DTOs
```

### Uso:
```java
// Convertir entidad a DTO
Owner owner = ownerService.findById(1).orElseThrow();
OwnerDTO dto = ownerMapper.toDTO(owner);

// Convertir DTO a entidad
Owner owner = ownerMapper.toEntity(dto);

// API endpoint
GET /api/owners/1  -> Retorna OwnerDTO (no Owner entity)
```

---

## 📚 PATRÓN 3: FACTORY PATTERN + BUILDER PATTERN

### ¿Qué es?
**Factory Pattern:** Encapsula la lógica de creación de objetos.  
**Builder Pattern:** Proporciona una interfaz fluida para construir objetos complejos.

### ¿Por qué?
**PROBLEMA ORIGINAL:**
```java
// ❌ ANTES: Creación de objetos repetitiva y propensa a errores
Owner owner = new Owner();
owner.setFirstName("John");
owner.setLastName("Doe");
owner.setAddress("123 Main St");
owner.setCity("New York");
owner.setTelephone("1234567890");
// ¿Olvidaste algún campo requerido? ¿Validaste el teléfono?
```

**PROBLEMAS:**
- ❌ Código repetitivo
- ❌ Fácil olvidar campos requeridos
- ❌ No hay validación en tiempo de construcción
- ❌ Difícil crear objetos con configuraciones predeterminadas
- ❌ Testing tedioso (crear datos de prueba manualmente)

**SOLUCIÓN CON FACTORY + BUILDER:**
```java
// ✅ Factory para creación simple
@Component
public class OwnerFactory {
    public Owner createOwner(String firstName, String lastName, ...) {
        Owner owner = new Owner();
        // Configuración centralizada
        owner.setFirstName(firstName);
        // ... validaciones automáticas
        return owner;
    }
    
    // Builder para construcción fluida
    public OwnerBuilder builder() {
        return new OwnerBuilder();
    }
}

// Uso del Builder - API fluida y legible
Owner owner = ownerFactory.builder()
    .withFirstName("John")
    .withLastName("Doe")
    .withAddress("123 Main St")
    .withCity("New York")
    .withTelephone("1234567890")
    .build();  // Valida antes de construir

// Factory con tipos específicos
Pet dog = petFactory.createDog("Buddy", LocalDate.of(2020, 5, 15));
Pet cat = petFactory.createCat("Whiskers", LocalDate.of(2019, 3, 10));
```

**BENEFICIOS:**
- ✅ Código más legible y expresivo
- ✅ Validación en tiempo de construcción
- ✅ Fácil crear objetos para testing
- ✅ Construcción consistente
- ✅ Encapsula lógica de creación compleja
- ✅ Soporte para configuraciones predeterminadas

### Archivos Creados:
```
src/main/java/.../owner/factory/
├── OwnerFactory.java           # Factory + Builder para Owner
└── PetFactory.java             # Factory + Builder para Pet
```

### Uso:
```java
// Factory simple
Owner owner = ownerFactory.createOwner("John", "Doe", ...);
Owner defaultOwner = ownerFactory.createDefaultOwner();  // Para testing

// Builder pattern - API fluida
Pet pet = petFactory.builder()
    .withName("Max")
    .withBirthDate(LocalDate.of(2020, 1, 15))
    .withType("dog")
    .build();  // Falla si faltan campos requeridos

// Factory de tipos específicos
Pet dog = petFactory.createDog("Buddy", LocalDate.now().minusYears(2));
```

---

## 📚 PATRÓN 4: STRATEGY PATTERN

### ¿Qué es?
Define una familia de algoritmos, encapsula cada uno y los hace intercambiables.

### ¿Por qué?
**PROBLEMA ORIGINAL:**
```java
// ❌ ANTES: Lógica de búsqueda hardcodeada
public Page<Owner> searchOwners(String lastName, String searchType) {
    if ("exact".equals(searchType)) {
        return repository.findByLastName(lastName);
    } else if ("startsWith".equals(searchType)) {
        return repository.findByLastNameStartingWith(lastName);
    } else if ("contains".equals(searchType)) {
        return repository.findByLastNameContaining(lastName);
    }
    // ❌ Violación de Open/Closed Principle
    // ❌ Difícil agregar nuevos tipos de búsqueda
}
```

**PROBLEMAS:**
- ❌ Violación del principio Open/Closed
- ❌ Código con muchos if/else
- ❌ Difícil testear cada estrategia por separado
- ❌ No se puede cambiar estrategia en runtime fácilmente
- ❌ Lógica de búsqueda mezclada con lógica de negocio

**SOLUCIÓN CON STRATEGY:**
```java
// ✅ Interfaz Strategy
public interface OwnerSearchStrategy {
    Page<Owner> search(String searchTerm, Pageable pageable);
    String getStrategyName();
}

// ✅ Estrategias concretas
@Component("partialMatchStrategy")
public class PartialMatchSearchStrategy implements OwnerSearchStrategy {
    public Page<Owner> search(String searchTerm, Pageable pageable) {
        return repository.findByLastNameStartingWith(searchTerm, pageable);
    }
}

@Component("containsSearchStrategy")
public class ContainsSearchStrategy implements OwnerSearchStrategy {
    public Page<Owner> search(String searchTerm, Pageable pageable) {
        // Lógica específica de búsqueda por "contains"
    }
}

// ✅ Context que usa las estrategias
@Component
public class OwnerSearchContext {
    private OwnerSearchStrategy currentStrategy;
    
    public void setStrategy(String strategyName) {
        this.currentStrategy = strategies.get(strategyName);
    }
    
    public Page<Owner> search(String term, Pageable pageable) {
        return currentStrategy.search(term, pageable);
    }
}

// Uso
searchContext.setStrategy("partialMatchStrategy");
Page<Owner> results = searchContext.search("Smith", pageable);
```

**BENEFICIOS:**
- ✅ Open/Closed: Fácil agregar nuevas estrategias
- ✅ Cada estrategia es testeable independientemente
- ✅ Se puede cambiar estrategia en runtime
- ✅ Elimina if/else complejos
- ✅ Código más limpio y mantenible
- ✅ Permite configuración por usuario o contexto

### Archivos Creados:
```
src/main/java/.../owner/search/
├── OwnerSearchStrategy.java              # Interfaz Strategy
├── PartialMatchSearchStrategy.java       # Estrategia 1: "empieza con"
├── ContainsSearchStrategy.java           # Estrategia 2: "contiene"
└── OwnerSearchContext.java               # Context que usa strategies
```

### Uso:
```java
// Cambiar estrategia en runtime
searchContext.setStrategy("partialMatchStrategy");
Page<Owner> results1 = searchContext.search("Smi", pageable);  // Encuentra "Smith"

searchContext.setStrategy("containsSearchStrategy");
Page<Owner> results2 = searchContext.search("son", pageable);  // Encuentra "Johnson", "Anderson"

// Ver estrategias disponibles
Map<String, OwnerSearchStrategy> strategies = searchContext.getAvailableStrategies();
```

---

## 📚 Otros Patrones Aplicados

### 5. DEPENDENCY INJECTION (mejorado)
**¿Qué es?** Inyección de dependencias a través del constructor (no field injection).

**Por qué:**
```java
// ❌ ANTES: Field injection
@Autowired
private OwnerRepository repository;

// ✅ DESPUÉS: Constructor injection
private final OwnerService ownerService;

public OwnerController(OwnerService ownerService) {
    this.ownerService = ownerService;
}
```

**Beneficios:**
- ✅ Dependencias inmutables (final)
- ✅ Fácil testing con mocks
- ✅ Dependencias explícitas
- ✅ No usa reflection innecesariamente

### 6. FACADE PATTERN (implícito en servicios)
**¿Qué es?** Interfaz simplificada para un subsistema complejo.

**Ejemplo:**
```java
// ✅ Servicio actúa como Facade
public Owner addVisit(Integer ownerId, Integer petId, Visit visit) {
    // Simplifica operación compleja:
    // 1. Buscar owner
    // 2. Buscar pet
    // 3. Validar visit
    // 4. Agregar visit
    // 5. Guardar cambios
    // Cliente solo llama un método simple
}
```

**Beneficios:**
- ✅ API simple para operaciones complejas
- ✅ Oculta complejidad interna
- ✅ Reduce acoplamiento

---

## 📊 Comparación: Antes vs. Después

### ANTES (Sin Patrones)
```java
// ❌ Controlador hace TODO
@Controller
class OwnerController {
    @Autowired
    private OwnerRepository owners;
    
    @PostMapping("/owners/new")
    public String create(Owner owner) {
        // Validación manual
        if (owner.getTelephone().length() != 10) {
            throw new IllegalArgumentException("Invalid phone");
        }
        // Acceso directo a BD
        owners.save(owner);
        // Sin transacciones claras
        // Sin reutilización
        return "redirect:/owners/" + owner.getId();
    }
}
```

**Problemas:**
- ❌ Lógica de negocio en el controlador
- ❌ Difícil de testear
- ❌ Código duplicado
- ❌ Violación de SRP
- ❌ Sin separación de capas

### DESPUÉS (Con Patrones)
```java
// ✅ Service Layer
@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository repository;
    
    public Owner createOwner(Owner owner) {
        validateOwner(owner);
        return repository.save(owner);
    }
    
    private void validateOwner(Owner owner) {
        // Validación centralizada
    }
}

// ✅ Controlador MVC delgado
@Controller
class OwnerController {
    private final OwnerService ownerService;
    
    @PostMapping("/owners/new")
    public String create(Owner owner) {
        Owner created = ownerService.createOwner(owner);
        return "redirect:/owners/" + created.getId();
    }
}

// ✅ REST API reutiliza la misma lógica
@RestController
class OwnerRestController {
    private final OwnerService ownerService;
    private final OwnerMapper mapper;
    
    @PostMapping("/api/owners")
    public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO dto) {
        Owner owner = mapper.toEntity(dto);
        Owner created = ownerService.createOwner(owner);
        return ResponseEntity.ok(mapper.toDTO(created));
    }
}
```

**Beneficios:**
- ✅ Separación clara de responsabilidades
- ✅ Lógica reutilizable
- ✅ Fácil de testear
- ✅ Código más mantenible
- ✅ Arquitectura escalable

---

## 🧪 Testing Mejorado

### ANTES (Difícil de testear)
```java
// ❌ Necesitas contexto web completo
@SpringBootTest
@AutoConfigureMockMvc
class OwnerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreate() throws Exception {
        mockMvc.perform(post("/owners/new")...)
            .andExpect(status().isOk());
        // Testing complejo, lento
    }
}
```

### DESPUÉS (Fácil de testear)
```java
// ✅ Test unitario simple del servicio
class OwnerServiceTest {
    @Mock
    private OwnerRepository repository;
    
    @InjectMocks
    private OwnerServiceImpl service;
    
    @Test
    void testCreateOwner() {
        // Given
        Owner owner = ownerFactory.createDefaultOwner();
        when(repository.save(any())).thenReturn(owner);
        
        // When
        Owner created = service.createOwner(owner);
        
        // Then
        assertNotNull(created);
        verify(repository).save(owner);
    }
}

// ✅ Test del controlador (solo HTTP concern)
class OwnerControllerTest {
    @Mock
    private OwnerService service;
    
    @InjectMocks
    private OwnerController controller;
    
    // Testing simple y rápido
}
```

---

## 📁 Estructura Final del Proyecto

```
src/main/java/.../owner/
├── Owner.java                      # Entity (sin cambios)
├── OwnerRepository.java            # Repository (sin cambios)
├── OwnerController.java            # MVC Controller (refactorizado)
│
├── service/                        # 🆕 SERVICE LAYER PATTERN
│   ├── OwnerService.java
│   ├── OwnerServiceImpl.java
│   ├── PetService.java
│   ├── PetServiceImpl.java
│   ├── VisitService.java
│   └── VisitServiceImpl.java
│
├── dto/                            # 🆕 DTO PATTERN
│   ├── OwnerDTO.java
│   └── OwnerMapper.java
│
├── api/                            # 🆕 REST API (usa DTOs)
│   └── OwnerRestController.java
│
├── factory/                        # 🆕 FACTORY + BUILDER PATTERN
│   ├── OwnerFactory.java
│   └── PetFactory.java
│
└── search/                         # 🆕 STRATEGY PATTERN
    ├── OwnerSearchStrategy.java
    ├── PartialMatchSearchStrategy.java
    ├── ContainsSearchStrategy.java
    └── OwnerSearchContext.java
```

---

## 🎓 Principios SOLID Aplicados

### 1. **S**ingle Responsibility Principle
- ✅ Servicios: Solo lógica de negocio
- ✅ Controladores: Solo HTTP concerns
- ✅ Repositorios: Solo acceso a datos
- ✅ DTOs: Solo transferencia de datos
- ✅ Factories: Solo creación de objetos

### 2. **O**pen/Closed Principle
- ✅ Strategy Pattern: Abierto para extensión (nuevas estrategias), cerrado para modificación
- ✅ Service interfaces: Nuevas implementaciones sin cambiar clientes

### 3. **L**iskov Substitution Principle  
- ✅ Cualquier OwnerSearchStrategy puede reemplazar a otra
- ✅ Cualquier implementación de OwnerService puede usarse

### 4. **I**nterface Segregation Principle
- ✅ Interfaces pequeñas y focalizadas
- ✅ Clientes no dependen de métodos que no usan

### 5. **D**ependency Inversion Principle
- ✅ Controladores dependen de OwnerService (abstracción), no OwnerServiceImpl
- ✅ Dependencias inyectadas, no creadas

---

## 🚀 Cómo Usar los Nuevos Patrones

### 1. Crear un Owner con Factory
```java
@Autowired
private OwnerFactory ownerFactory;

// Forma simple
Owner owner = ownerFactory.createOwner("John", "Doe", "123 Main", "NYC", "1234567890");

// Forma fluida con Builder
Owner owner = ownerFactory.builder()
    .withFirstName("John")
    .withLastName("Doe")
    .withAddress("123 Main Street")
    .withCity("New York")
    .withTelephone("1234567890")
    .build();
```

### 2. Usar Servicios
```java
@Autowired
private OwnerService ownerService;

// Crear
Owner created = ownerService.createOwner(owner);

// Buscar
Page<Owner> owners = ownerService.findByLastName("Smith", pageable);

// Actualizar
Owner updated = ownerService.updateOwner(id, ownerData);
```

### 3. Usar DTOs en API REST
```java
// GET /api/owners/1
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "fullName": "John Doe",         // Campo computado
  "petCount": 2,                   // Campo agregado
  "pets": [
    {
      "id": 1,
      "name": "Buddy",
      "type": "dog",
      "visitCount": 5              // Sin cargar todas las visitas
    }
  ]
}
```

### 4. Cambiar Estrategia de Búsqueda
```java
@Autowired
private OwnerSearchContext searchContext;

// Búsqueda parcial (por defecto)
searchContext.setStrategy("partialMatchStrategy");
Page<Owner> results = searchContext.search("Smi", pageable);  // Encuentra "Smith"

// Búsqueda completa
searchContext.setStrategy("containsSearchStrategy");
results = searchContext.search("son", pageable);  // Encuentra "Johnson", "Anderson"
```

---

## 📈 Mejoras Logradas

### Mantenibilidad
- ✅ Código organizado en capas
- ✅ Responsabilidades claras
- ✅ Fácil localizar qué cambiar

### Escalabilidad
- ✅ Fácil agregar nuevos endpoints REST
- ✅ Nuevas estrategias de búsqueda sin tocar código existente
- ✅ Servicios reutilizables

### Testabilidad
- ✅ Tests unitarios simples
- ✅ Mocking fácil
- ✅ Tests rápidos

### Flexibilidad
- ✅ Cambiar comportamiento en runtime (Strategy)
- ✅ Versionado de API fácil (DTOs)
- ✅ Configuraciones alternativas (Factory)

---

## 🔄 Próximas Mejoras Posibles

### Patrones Adicionales que Podrían Aplicarse:

1. **Observer Pattern** - Para notificaciones cuando se crea/actualiza un owner
2. **Decorator Pattern** - Para agregar funcionalidades a servicios (logging, caching)
3. **Repository Pattern con Specification** - Para queries dinámicas complejas
4. **Command Pattern** - Para operaciones deshacer/rehacer
5. **Chain of Responsibility** - Para validaciones complejas en pipeline
6. **Template Method Pattern** - Para flujos de operación comunes

---

## 📚 Referencias

- **Design Patterns:** Gang of Four (GoF)
- **Enterprise Application Architecture:** Martin Fowler
- **SOLID Principles:** Robert C. Martin
- **Spring Framework Best Practices:** Official Spring Documentation

---

## ✅ Checklist de Implementación

- [x] Service Layer Pattern implementado
- [x] DTO Pattern + Mapper implementado
- [x] Factory Pattern + Builder implementado
- [x] Strategy Pattern implementado
- [x] Dependency Injection mejorada
- [x] REST API con DTOs
- [x] Documentación completa
- [ ] Tests unitarios para servicios
- [ ] Tests de integración
- [ ] Documentación API (Swagger/OpenAPI)

---

**Refactorización completada:** Marzo 2026  
**Versión:** 1.0  
**Autor:** Refactorización con Patrones de Diseño
