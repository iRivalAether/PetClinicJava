# Diagramas de Casos de Uso - Spring PetClinic

Este documento describe los diagramas de casos de uso generados para el sistema Spring PetClinic.

## 📋 Diagramas Disponibles

Se han creado **4 diagramas PlantUML** que representan los casos de uso del sistema desde diferentes perspectivas:

### 1. **use-cases-diagram.puml** - Diagrama General
Vista general del sistema organizada por paquetes funcionales.

**Contenido:**
- Gestión de Propietarios (5 casos de uso)
- Gestión de Mascotas (4 casos de uso)
- Gestión de Visitas (3 casos de uso)
- Gestión de Veterinarios (3 casos de uso)
- Sistema General (2 casos de uso)

**Actores:**
- Recepcionista
- Propietario de Mascota
- Veterinario

**Características:**
- Relaciones include/extend entre casos de uso
- Notas explicativas
- Agrupación por módulos funcionales

---

### 2. **use-cases-detailed.puml** - Diagrama Detallado por Módulo
Vista técnica detallada con todos los casos de uso específicos por cada módulo del sistema.

**Contenido:**
- **Módulo Owners:** 8 casos de uso detallados
- **Módulo Pets:** 7 casos de uso detallados
- **Módulo Visits:** 6 casos de uso detallados
- **Módulo Vets:** 5 casos de uso detallados
- **Módulo System:** 3 casos de uso

**Información adicional:**
- Nombres de controladores
- Endpoints HTTP principales
- Relaciones de dependencia entre módulos
- Validaciones específicas

**Ideal para:** Desarrolladores que necesitan entender la arquitectura técnica

---

### 3. **use-cases-workflow.puml** - Flujos de Trabajo
Vista orientada a procesos de negocio con flujos de trabajo principales.

**Flujos principales:**
1. **Flujo 1: Gestión de Clientes** (4 casos de uso)
   - Registro y búsqueda de propietarios
   - Actualización de datos de contacto
   
2. **Flujo 2: Gestión de Mascotas** (4 casos de uso)
   - Registro y actualización de mascotas
   - Consulta de fichas médicas
   
3. **Flujo 3: Programación de Visitas** (5 casos de uso)
   - Agendamiento de citas
   - Registro de diagnósticos y tratamientos
   
4. **Flujo 4: Directorio de Veterinarios** (4 casos de uso)
   - Consulta de veterinarios y especialidades
   - API REST para sistemas externos

**Servicios comunes:** Validación, gestión de errores, mensajes, paginación

**Ideal para:** Analistas de negocio y gerentes de proyecto

---

### 4. **use-cases-simple.puml** - Diagrama Simplificado
Vista compacta con los casos de uso esenciales del sistema.

**Casos de uso principales:**
- Propietarios: Registrar, Buscar, Actualizar, Ver
- Mascotas: Registrar, Actualizar, Ver
- Visitas: Agendar, Registrar, Ver Historial
- Veterinarios: Consultar, Ver Especialidades, API

**Características:**
- Relaciones claras entre casos de uso
- Leyenda explicativa
- Notas con información técnica relevante

**Ideal para:** Presentaciones ejecutivas y documentación de alto nivel

---

## 🛠️ Cómo Visualizar los Diagramas

### Opción 1: Visual Studio Code (Recomendado)
1. Instalar la extensión **PlantUML** de jebbs
2. Instalar Java JDK (requerido por PlantUML)
3. Abrir cualquier archivo `.puml`
4. Presionar `Alt + D` para previsualizar

### Opción 2: PlantUML Online
1. Visitar https://www.plantuml.com/plantuml/uml/
2. Copiar el contenido de cualquier archivo `.puml`
3. Pegar en el editor online

### Opción 3: Línea de Comandos
```bash
# Instalar PlantUML
java -jar plantuml.jar use-cases-diagram.puml

# Genera una imagen PNG
java -jar plantuml.jar -tpng use-cases-diagram.puml

# Generar todos los diagramas
java -jar plantuml.jar *.puml
```

### Opción 4: Herramientas de Diagramación
- **IntelliJ IDEA:** Plugin PlantUML Integration
- **Eclipse:** Plugin PlantUML
- **Notion, Confluence, GitLab:** Soporte nativo de PlantUML

---

## 📊 Resumen de Casos de Uso Identificados

### Módulo de Propietarios (OwnerController)
| Caso de Uso | Endpoint | Método | Descripción |
|-------------|----------|--------|-------------|
| Buscar Propietario | `/owners/find` | GET | Formulario de búsqueda |
| Listar Propietarios | `/owners` | GET | Búsqueda con paginación |
| Crear Propietario | `/owners/new` | POST | Registro de nuevo propietario |
| Ver Propietario | `/owners/{id}` | GET | Detalles completos |
| Actualizar Propietario | `/owners/{id}/edit` | POST | Modificar datos |

### Módulo de Mascotas (PetController)
| Caso de Uso | Endpoint | Método | Descripción |
|-------------|----------|--------|-------------|
| Registrar Mascota | `/owners/{ownerId}/pets/new` | POST | Nueva mascota |
| Actualizar Mascota | `/owners/{ownerId}/pets/{petId}/edit` | POST | Modificar datos |

### Módulo de Visitas (VisitController)
| Caso de Uso | Endpoint | Método | Descripción |
|-------------|----------|--------|-------------|
| Programar Visita | `/owners/{ownerId}/pets/{petId}/visits/new` | GET | Formulario |
| Registrar Visita | `/owners/{ownerId}/pets/{petId}/visits/new` | POST | Guardar visita |

### Módulo de Veterinarios (VetController)
| Caso de Uso | Endpoint | Método | Descripción |
|-------------|----------|--------|-------------|
| Ver Veterinarios HTML | `/vets.html` | GET | Lista paginada |
| API Veterinarios | `/vets` | GET | JSON REST API |

---

## 🎯 Reglas de Negocio Identificadas

### Propietarios
- ✅ Campos obligatorios: firstName, lastName, address, city, telephone
- ✅ Teléfono: exactamente 10 dígitos
- ✅ Búsqueda por apellido con comodín (%)
- ✅ Paginación: 5 registros por página
- ✅ Redirección automática si solo hay 1 resultado

### Mascotas
- ✅ Nombre único por propietario
- ✅ Fecha de nacimiento no puede ser futura
- ✅ Tipo de mascota obligatorio (cat, dog, bird, etc.)
- ✅ Relación obligatoria con propietario

### Visitas
- ✅ Descripción obligatoria
- ✅ Asociación a una mascota específica
- ✅ Fecha registrada automáticamente
- ✅ Historial visible en detalles del propietario

### Veterinarios
- ✅ Múltiples especialidades por veterinario
- ✅ Ordenamiento alfabético de especialidades
- ✅ Endpoint REST para integración externa

---

## 📁 Estructura de Archivos

```
spring-petclinic/
├── use-cases-diagram.puml          # Diagrama general
├── use-cases-detailed.puml         # Diagrama técnico detallado
├── use-cases-workflow.puml         # Flujos de trabajo
├── use-cases-simple.puml           # Diagrama simplificado
└── USE-CASES-README.md            # Este documento
```

---

## 🔍 Actores del Sistema

### 1. Recepcionista (Actor Principal)
**Permisos:**
- ✅ Crear, modificar y consultar propietarios
- ✅ Registrar y actualizar mascotas
- ✅ Programar y registrar visitas
- ✅ Consultar veterinarios y especialidades

### 2. Propietario de Mascota (Actor Secundario)
**Permisos:**
- ✅ Ver información de propietario
- ✅ Consultar datos de mascotas
- ✅ Ver historial de visitas
- ✅ Consultar veterinarios disponibles

### 3. Veterinario (Actor Especializado)
**Permisos:**
- ✅ Ver historial de visitas
- ✅ Registrar diagnósticos y tratamientos
- ✅ Consultar directorio de veterinarios
- ✅ Acceder a API REST

### 4. Sistema Externo (Actor Técnico)
**Permisos:**
- ✅ Acceder a API REST de veterinarios
- ✅ Integración programática

---

## 📝 Notas Adicionales

- Los diagramas están basados en el análisis del código fuente en `src/main/java/org/springframework/samples/petclinic/`
- Se han identificado **17 casos de uso principales** y más de **30 casos de uso detallados**
- Todos los diagramas incluyen relaciones `<<include>>` y `<<extend>>` según corresponda
- La documentación técnica incluye los endpoints HTTP reales del sistema

---

## 🔄 Actualización de Diagramas

Si se modifica el sistema, actualizar:
1. Revisar cambios en los controladores
2. Actualizar archivos `.puml` correspondientes
3. Regenerar imágenes si es necesario
4. Actualizar este README con nuevos casos de uso

---

## 📚 Referencias

- **Repositorio:** Spring PetClinic
- **Framework:** Spring Boot 3.x
- **Tecnología de diagramas:** PlantUML
- **Documentación PlantUML:** https://plantuml.com/use-case-diagram
- **Spring PetClinic:** https://github.com/spring-projects/spring-petclinic

---

**Generado:** Marzo 2026  
**Versión:** 1.0  
**Autor:** Análisis del código fuente de Spring PetClinic
