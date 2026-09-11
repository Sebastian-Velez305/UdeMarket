# UdeMarket - Marketplace Universitario (Comunidad SENA)

UdeMarket es una aplicación nativa para Android diseñada exclusivamente para la comunidad del SENA. La plataforma integra un ecosistema de servicios para aprendices, incluyendo gestión de alimentación, comercio C2C y herramientas de productividad financiera.

---

## Arquitectura y Tecnologías

El proyecto sigue los principios de Clean Architecture y una organización MVVM, garantizando escalabilidad y modularidad.

- Backend: Google Firebase (Auth, Firestore, Storage).
- Patrón de Arquitectura: Repository Pattern con Kotlin Coroutines y Flow.
- Gestión de Estado: StateFlow para flujos asíncronos y mutableStateListOf para reactividad local.
- Diseño: Estética Premium Ultra Dark & Neon Purple con Material Design 3.

---

## Checklists de Implementación (Estado Actual)

### Bloque 1: Autenticación y Perfil
- [x] Dominio Restringido: Validación obligatoria de correos @misena.edu.co.
- [x] Registro Atómico: Guardado simultáneo en colecciones users y profiles mediante Firestore Batch.
- [x] Persistencia de Sesión: Detección de usuario activo mediante FirebaseAuth al arrancar la app.
- [x] Traducción de Errores: Manejo de excepciones de Firebase (contraseña incorrecta, usuario existente) en español.
- [x] Perfil Real-time: Carga de datos de usuario (nombre, carrera, reputación) desde la nube.

### Bloque 2: Navegación y Estructura
- [x] Navegación Centralizada: Uso de Screen.kt como única fuente de verdad para rutas.
- [x] Gestión de Backstack: Limpieza de historial al iniciar/cerrar sesión para evitar regresos no deseados.
- [x] BottomBar Inteligente: Navegación inferior con persistencia de estado (saveState/restoreState) visible solo para usuarios autenticados.
- [x] Rutas Dinámicas: Paso de parámetros (como itemId) entre pantallas del Marketplace.

### Bloque 3: Marketplace (Ventas)
- [x] Sincronización en Vivo: Uso de addSnapshotListener para actualizar la lista de productos sin refrescar.
- [x] CRUD Operativo: Funcionalidad para listar, crear, editar y eliminar artículos.
- [x] UX de Seguridad: Diálogos de confirmación antes de eliminar productos de la base de datos.
- [x] Optimización Visual: Imágenes gestionadas con Coil y placeholders dinámicos por categoría.

### Bloque 4: Sistema de Chat
- [x] Mensajería Instantánea: Infraestructura callbackFlow para recibir mensajes en tiempo real.
- [x] Chat Contextual: Cada conversación está vinculada a un producto específico del Marketplace.
- [x] Lógica Anti-Duplicados: Verificación automática de hilos existentes entre comprador y vendedor.
- [x] Interfaz de Mensajes: Burbujas de chat diferenciadas por remitente y autoscroll automático al final.

### Utilidad: Calculadora de Gastos SENA
- [x] Independencia de Red: Módulo 100% funcional en modo offline.
- [x] Eficiencia de Memoria: Uso de mutableStateListOf para minimizar recomposiciones de la UI.
- [x] Cálculo Financiero: Sumatoria automática en tiempo real y formateo de moneda local ($ CO).

---

## Próximos Pasos
- [ ] Multimedia: Integración de Firebase Storage para fotos reales de productos.
- [ ] Búsqueda Avanzada: Implementación de filtros por categoría y rango de precios.
- [ ] Campus Food: Directorio de locales con sistema de pedidos y estados (Abierto/Cerrado).
- [ ] Notificaciones: Avisos push para nuevos mensajes de chat.

---
*Este proyecto es mantenido bajo estándares Senior de desarrollo en Kotlin y Jetpack Compose.*
