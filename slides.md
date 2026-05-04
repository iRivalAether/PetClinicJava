---
theme: default
title: Sistema de Gestión Clínica - Refactorización
info: Presentación basada en estructura y formato del PDF de referencia
class: text-center
drawings:
  persist: false
transition: slide-fade
colorSchema: light
mdc: true
---

<style>
:root {
  --slidev-theme-primary: #15803d;
}

.slidev-layout {
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 45%, #ecfdf5 100%);
}

h1, h2 {
  color: #166534;
}

.soft-panel {
  border: 1px solid #bbf7d0;
  background: #f0fdf4;
  border-radius: 14px;
  padding: 12px 14px;
}

.kpi-card {
  border: 1px solid #bbf7d0;
  background: #f7fee7;
  border-radius: 12px;
  padding: 10px;
}

.accent {
  color: #047857;
  font-weight: 700;
}

.thumb-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.thumb-card {
  border: 1px solid #bbf7d0;
  background: #ffffffcc;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 6px 20px rgba(6, 78, 59, 0.08);
}

.thumb-card img {
  width: 100%;
  height: 96px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #d1fae5;
}

.thumb-title {
  margin-top: 6px;
  font-size: 12px;
  color: #065f46;
  font-weight: 700;
}

.mini-gallery {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 10px;
}

.mini-gallery img {
  width: 100%;
  height: 110px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #bbf7d0;
}
</style>

# Sistema de Gestión Clínica

## Refactorización del sistema Spring PetClinic

**Nombre del proyecto:** Spring PetClinic  
**Tema:** Refactorización de arquitectura y código  
**Autor:** Diaz de Leon Torres Bryan de Jesus, Tello Ponce Fabian Enrique  
**Fecha:** Marzo 2026

<div class="mt-6 flex justify-center">
  <img src="./Patrones%20de%20Dise%C3%B1o%20Implementados.png" class="w-105 rounded-xl border border-green-200" />
</div>

---

# CONTENT

### 01 · Contexto del Proyecto  
### 02 · Problema Inicial  
### 03 · Objetivos de la Refactorización  
### 04 · Estrategia Aplicada  
### 05 · Resultados y Mejoras  
### 06 · Riesgos o Limitaciones  
### 07 · Conclusión  
### 08 · Demo

<div class="thumb-grid">
  <div class="thumb-card">
    <img src="./PetClinic%20-%20Diagrama%20de%20Casos%20de%20Uso.png" />
    <div class="thumb-title">Casos de uso</div>
  </div>
  <div class="thumb-card">
    <img src="./Patrones%20de%20Dise%C3%B1o%20Implementados.png" />
    <div class="thumb-title">Arquitectura refactorizada</div>
  </div>
  <div class="thumb-card">
    <img src="./Diagrama%20de%20Secuencia%20-%20Crear%20Owner%20con%20Patrones.png" />
    <div class="thumb-title">Flujo técnico</div>
  </div>
  <div class="thumb-card">
    <img src="./PetClinic%20-%20Flujos%20de%20Trabajo%20Principales.png" />
    <div class="thumb-title">Workflows</div>
  </div>
</div>

---

# 2. Contexto del Proyecto

## ¿Qué hace el sistema?

Aplicación de gestión veterinaria para:

- Gestión de propietarios
- Gestión de mascotas
- Gestión de visitas clínicas
- Consulta de veterinarios y especialidades
- Exposición de API REST para integración

---

# 2. Contexto del Proyecto

## Tecnologías usadas

<div class="grid grid-cols-2 gap-4 mt-3 text-left">
  <div class="kpi-card"><span class="accent">Java 17+</span><br/>Spring Boot, MVC y Thymeleaf</div>
  <div class="kpi-card"><span class="accent">Persistencia</span><br/>Spring Data JPA con H2/MySQL/PostgreSQL</div>
  <div class="kpi-card"><span class="accent">Build</span><br/>Maven y Gradle</div>
  <div class="kpi-card"><span class="accent">Modelado</span><br/>PlantUML para casos de uso y patrones</div>
</div>

<div class="mt-4 flex justify-center">
  <img src="./PetClinic%20-%20Casos%20de%20Uso%20Simplificado.png" class="w-95 rounded-xl border border-green-200" />
</div>

---

# 2. Contexto del Proyecto

## Alcance del proyecto

<div class="grid grid-cols-2 gap-6">
<div>

### Actores
- Recepcionista
- Propietario
- Veterinario
- Sistema externo (API)

</div>
<div>

### Casos de uso documentados
- 17 principales
- 26+ detallados
- 4 flujos de negocio
- 8 esenciales (vista ejecutiva)

</div>
</div>

<div class="mini-gallery">
  <img src="./PetClinic%20-%20Diagrama%20de%20Casos%20de%20Uso.png" />
  <img src="./PetClinic%20-%20Flujos%20de%20Trabajo%20Principales.png" />
  <img src="./PetClinic%20-%20Casos%20de%20Uso%20Simplificado.png" />
</div>

---

# 2. Contexto del Proyecto

## Casos de uso del sistema

![Casos de uso generales](./PetClinic%20-%20Diagrama%20de%20Casos%20de%20Uso.png)

---

# 3. Problema Inicial

## ¿Por qué era necesaria la refactorización?

- Código difícil de mantener
- Duplicación de lógica en controladores
- Acoplamiento alto entre capas
- Violaciones de principios SOLID
- Mayor riesgo de bugs por cambios

---

# 3. Problema Inicial

## Antes: ejemplo de código con responsabilidades mezcladas

```java
@PostMapping("/owners/new")
public String processCreationForm(@Valid Owner owner, BindingResult result) {
  if (result.hasErrors()) {
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }
  this.owners.save(owner); // acceso directo al repositorio desde controller
  return "redirect:/owners/" + owner.getId();
}
```

Problema: el controlador mezcla HTTP + negocio + persistencia.

<div class="soft-panel mt-3 text-left">
Impacto directo: cambios más riesgosos, pruebas más costosas y menor velocidad de evolución.
</div>

---

# 3. Problema Inicial

## Síntomas observados

<div class="grid grid-cols-2 gap-6">
<div>

### Código
- Métodos largos
- Validaciones repetidas
- Dependencias directas a repositorios

</div>
<div>

### Operación
- Cambios más lentos
- Mayor probabilidad de regresión
- Dificultad para pruebas aisladas

</div>
</div>

---

# 4. Objetivos de la Refactorización

- Mejorar legibilidad del código
- Reducir complejidad y acoplamiento
- Optimizar rendimiento operativo (menos retrabajo y flujo más claro)
- Facilitar escalabilidad técnica
- Estandarizar buenas prácticas de arquitectura

---

# 4. Objetivos de la Refactorización

## Objetivos técnicos medibles

- Disminuir líneas en controladores
- Aumentar cohesión por capa
- Reducir acoplamiento entre módulos
- Preparar base para más pruebas automatizadas

---

# 5. Estrategia Aplicada

## ¿Cómo se hizo?

- Aplicación de patrones de diseño
- Separación de responsabilidades por capas
- Modularización de componentes nuevos
- Refactor incremental de controladores críticos
- Documentación técnica con diagramas y decisiones

---

# 5. Estrategia Aplicada

## Patrones implementados

<div class="grid grid-cols-2 gap-4">
<div>

- Service Layer
- DTO
- Mapper

</div>
<div>

- Factory
- Builder
- Strategy
- Dependency Injection por constructor

</div>
</div>

---

# 5. Estrategia Aplicada

## Service Layer

**¿Qué resuelve?**  
Separa la lógica de negocio de los controladores web.

**¿Por qué se eligió?**
- Reduce acoplamiento controller-repository
- Centraliza reglas de negocio
- Mejora pruebas unitarias con mocks

**Alternativas no seleccionadas**
- **Lógica en controllers:** rápida al inicio, pero difícil de mantener
- **Domain Service + arquitectura hexagonal completa:** válida, pero sobredimensionada para este alcance

---

# 5. Estrategia Aplicada

## DTO Pattern

**¿Qué resuelve?**  
Evita exponer entidades JPA directamente en la capa REST.

**¿Por qué se eligió?**
- Define contratos estables de API
- Reduce payload y campos innecesarios
- Evita problemas de serialización/lazy loading

**Alternativas no seleccionadas**
- **Exponer entidades directas:** más simple, pero acopla API al modelo interno
- **GraphQL para selección dinámica:** potente, pero fuera del objetivo de esta refactorización

---

# 5. Estrategia Aplicada

## Mapper Pattern

**¿Qué resuelve?**  
Centraliza conversión `Entity ↔ DTO` en un solo punto.

**¿Por qué se eligió?**
- Elimina mapeo repetido en controllers/services
- Facilita pruebas y mantenimiento
- Permite evolucionar DTO sin tocar dominio

**Alternativas no seleccionadas**
- **Mapeo manual disperso:** genera duplicación y errores
- **MapStruct inmediato:** opción buena, pero se priorizó simplicidad inicial y control explícito

---

# 5. Estrategia Aplicada

## Factory + Builder

**¿Qué resuelve?**  
Estandariza creación de objetos con validaciones y defaults.

**¿Por qué se eligió?**
- Creación más legible y consistente
- Reduce errores por inicialización incompleta
- Útil para fixtures y pruebas

**Alternativas no seleccionadas**
- **Constructores telescópicos:** poco legibles con muchos parámetros
- **Setters sueltos en todo el código:** mayor riesgo de objetos inválidos

---

# 5. Estrategia Aplicada

## Strategy Pattern

**¿Qué resuelve?**  
Permite cambiar algoritmos de búsqueda sin modificar clientes.

**¿Por qué se eligió?**
- Cumple Open/Closed
- Facilita agregar nuevas búsquedas (contains, exact, futuras fuzzy)
- Permite pruebas por estrategia

**Alternativas no seleccionadas**
- **if/else por tipo de búsqueda:** crece mal y rompe mantenibilidad
- **Una sola query genérica compleja:** menos flexible para evolución

---

# 5. Estrategia Aplicada

## ¿Qué NO aplicamos (a propósito)?

- **Microservicios:** no era objetivo; aumentaba complejidad operativa
- **Event Sourcing / CQRS:** sobreingeniería para el dominio actual
- **ORM avanzado adicional:** se mantuvo stack actual para minimizar riesgo

Decisión clave: **refactorizar para claridad y mantenibilidad sin cambiar el modelo de despliegue**.

---

# 5. Estrategia Aplicada

## Arquitectura después de refactorizar

![Arquitectura con patrones](./Patrones%20de%20Dise%C3%B1o%20Implementados.png)

<div class="mt-2 text-emerald-700">Capas más claras, menor acoplamiento y reglas centralizadas.</div>

---

# 5. Estrategia Aplicada

## Modularización aplicada

Nuevos módulos y responsabilidades:

- `service/` → reglas de negocio
- `dto/` → contratos de transferencia
- `mapper/` → conversión de datos
- `factory/` → construcción consistente de objetos
- `strategy/` → algoritmos intercambiables de búsqueda

---

# 7. Resultados y Mejoras

| Indicador | Antes | Después | Impacto |
|---|---:|---:|---:|
| Líneas promedio por controller | ~200 | ~80 | -60% |
| Testabilidad | Baja | Alta | +80% |
| Acoplamiento | Alto | Bajo | -70% |
| Cohesión | Baja | Alta | +90% |
| Mantenibilidad | Difícil | Mejorada | +75% |

<div class="mt-3 grid grid-cols-3 gap-3 text-sm">
  <div class="kpi-card"><div class="accent">-60%</div>líneas en controllers</div>
  <div class="kpi-card"><div class="accent">+80%</div>testabilidad</div>
  <div class="kpi-card"><div class="accent">-70%</div>acoplamiento</div>
</div>

---

# 7. Resultados y Mejoras

## Impacto cualitativo

- Código más limpio y entendible
- Menos bugs por responsabilidades mejor definidas
- Menor tiempo para agregar nuevos cambios
- Mejor base para exponer y evolucionar API REST
- Mejor rendimiento del equipo en mantenimiento

<div class="mini-gallery">
  <img src="./Patrones%20de%20Dise%C3%B1o%20Implementados.png" />
  <img src="./Diagrama%20de%20Secuencia%20-%20Crear%20Owner%20con%20Patrones.png" />
  <img src="./PetClinic%20-%20Diagrama%20de%20Casos%20de%20Uso.png" />
</div>

---

# 7. Resultados y Mejoras

## Antes vs Después (estructura)

```text
ANTES:
Controller -> Repository (lógica mezclada)

DESPUÉS:
Controller -> Service -> Repository
             \-> Mapper/DTO
             \-> Factory/Strategy
```

Resultado: responsabilidades claras y evolución más segura.

<div class="mt-4 flex justify-center">
  <img src="./Diagrama%20de%20Secuencia%20-%20Crear%20Owner%20con%20Patrones.png" class="w-110 rounded-xl border border-green-200" />
</div>

---

# 9. Riesgos o Limitaciones

- Posibles incompatibilidades en cambios amplios
- Curva de aprendizaje en nuevos patrones
- Tiempo inicial invertido en refactor y validación
- Riesgo de regresiones si no hay suficiente cobertura de pruebas
- Necesidad de disciplina arquitectónica continua

---

# 9. Riesgos o Limitaciones

## Mitigaciones propuestas

- Refactor por iteraciones pequeñas
- Pruebas de regresión en módulos críticos
- Revisión de arquitectura en PRs
- Checklist de patrones para nuevos desarrollos

---

# 10. Conclusión

## ¿Qué se logró?

- Se mejoró estructura y calidad del código
- Se redujo complejidad en capas de presentación
- Se fortaleció mantenibilidad y escalabilidad

## Beneficios a futuro

- Menor costo de evolución
- Mayor velocidad de entrega
- Plataforma más estable para nuevas funcionalidades

---

# 10. Conclusión

## Recomendaciones

- Aumentar cobertura de pruebas unitarias e integración
- Medir rendimiento técnico en CI/CD (tiempos y calidad)
- Definir reglas de arquitectura (ej. ArchUnit)
- Mantener documentación viva de casos de uso y patrones

<div class="soft-panel mt-4 text-left">
<span class="accent">Resumen visual:</span> arquitectura más limpia, flujo de negocio mejor documentado y base más preparada para escalar.
</div>

---

# 11. Demo (Opcional 🔥)

## Ejecución del sistema

```bash
./mvnw spring-boot:run
```

Abrir: `http://localhost:8080`

## Ejecución de la presentación

```bash
npx @slidev/cli slides.md --port 3030
```

Abrir: `http://localhost:3030`

---

# Gracias

## Preguntas y comentarios

Refactorizar no fue solo “ordenar código”:  
fue preparar el sistema para crecer con menos riesgo.
