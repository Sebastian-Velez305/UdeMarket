# UdeMarket - Marketplace Universitario (UdeA) 🎓🛒

**UdeMarket** es una aplicación nativa para Android diseñada exclusivamente para la comunidad de la **Universidad de Antioquia**. La plataforma integra dos ecosistemas principales:
1. **Campus Food:** Gestión y consulta de menús en cafeterías y tiendas del campus.
2. **Marketplace C2C:** Compra y venta de artículos de segunda mano entre la comunidad universitaria.

---

## 🚀 Arquitectura y Tecnologías

El proyecto sigue los principios de **Clean Architecture** y una organización **MVVM basada en características (Feature-based)**, garantizando escalabilidad y modularidad.

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarativa y moderna)
- **Patrón de Arquitectura:** MVVM (Model-View-ViewModel)
- **Gestión de Estado:** `StateFlow` y `Unidirectional Data Flow (UDF)`
- **Diseño:** Material Design 3 con estética **Premium Ultra Dark & Neon Purple**.

---

## 📂 Estructura del Proyecto

```text
com.example.udemarket/
├── core/                # Infraestructura global (Navegación, DI, Utilidades)
├── common/              # Componentes UI y lógica compartida
├── ui/theme/            # Sistema de diseño (Colores, Tipografía, Temas)
└── features/            # Módulos funcionales independientes
    ├── auth/            # Login y Registro (Validación @udea.edu.co)
    ├── food/            # Módulo de alimentación campus
    ├── marketplace/     # Venta de artículos C2C
    ├── chat/            # Mensajería entre usuarios
    └── profile/         # Gestión de perfil y configuración
```

---

## 🛠️ Estado Actual del Desarrollo (Vistas de UI)

### 🔐 Módulo de Autenticación (`auth`)
- [x] **Login Screen:** Interfaz premium con efecto Glassmorphism y gradientes radiales.
- [x] **Register Screen:** Formulario con scroll dinámico (Nombre, Celular, Carrera opcional).
- [x] **Validación:** Lógica para dominios universitarios y fuerza de contraseña.

### 🍔 Módulo Campus Food (`food`)
- [x] **Exploración de Locales:** Lista de tiendas con categorías (Almuerzos, Snacks, Café).
- [x] **Filtros Dinámicos:** Selector de categorías con chips estilizados.
- [x] **Información de Tienda:** Visualización de ratings, tiempos de entrega y categorías.

### 📦 Módulo Marketplace C2C (`marketplace`)
- [x] **Grid de Productos:** Diseño de tarjetas modernas para visualización de artículos.
- [x] **Buscador:** Barra de búsqueda integrada con estética de cristal.
- [x] **Categorización:** Clasificación de productos (Libros, Tecnología, Insumos).

---

## 🎨 Estética Visual
La aplicación utiliza una interfaz de alto contraste diseñada para la legibilidad y el impacto visual:
- **Fondo:** Deep Black (#000000) con gradientes verticales hacia Deep Purple (#1A0033).
- **Acentos:** Neon Purple (#BB86FC) para componentes interactivos, botones y bordes de foco.
- **Efectos:** Glassmorphism (transparencias sutiles) y elevaciones mediante sombras de color.

---

**Desarrollado con ❤️ para la comunidad de la Universidad de Antioquia.**
