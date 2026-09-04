# UdeMarket - Marketplace Universitario (Comunidad SENA) 🎓🛒

**UdeMarket** es una aplicación nativa para Android diseñada exclusivamente para la comunidad del **SENA**. La plataforma integra dos ecosistemas principales:
1. **Campus Food:** Gestión y consulta de menús en cafeterías y tiendas del campus con actualizaciones en tiempo real.
2. **Marketplace C2C:** Compra y venta de artículos entre aprendices.

---

## Arquitectura y Tecnologías

El proyecto sigue los principios de **Clean Architecture** y una organización **MVVM**, garantizando escalabilidad y modularidad.

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
- **Backend:** [Google Firebase](https://firebase.google.com/) (Auth, Firestore, Storage)
- **Patrón de Arquitectura:** Repository Pattern con Kotlin Coroutines y Flow.
- **Gestión de Estado:** `StateFlow` y `ResultState` para manejo de carga y errores.
- **Diseño:** Estética **Premium Ultra Dark & Neon Purple** con efectos de Glassmorphism.

---

## Estructura del Proyecto

```text
com.example.udemarket/
├── core/                # Infraestructura global (Navegación, ResultState)
├── data/                # Capa de Datos
│   ├── model/           # Data Classes (User, FoodStore, MarketplaceItem)
│   └── repository/      # Interfaces e implementaciones de Repositorios (Firebase)
├── ui/                  # Componentes y Temas globales
└── features/            # Módulos funcionales independientes
    ├── auth/            # Login y Registro (Validación @misena.edu.co)
    ├── food/            # Módulo de alimentación campus (Real-time)
    └── marketplace/     # Venta de artículos C2C
```

---

##  Implementaciones Recientes

###  Integración de Firebase
- [x] **Firebase Auth:** Autenticación robusta con validación obligatoria de dominio institucional `@misena.edu.co`.
- [x] **Cloud Firestore:** Base de datos en tiempo real para el directorio de comidas y marketplace.
- [x] **Firebase Storage:** Almacenamiento de imágenes para productos del marketplace.

###  Capa de Repositorios
- [x] **AuthRepository:** Gestión de sesiones, registro de perfiles y verificación de correo.
- [x] **FoodRepository:** Flujos de datos (`Flow`) para actualizaciones en vivo de locales abiertos/cerrados.
- [x] **MarketplaceRepository:** Lógica de publicación de artículos con subida de multimedia.

### UI & UX
- [x] **Login Neón:** Interfaz optimizada con validación en tiempo real y feedback visual.
- [x] **Food Dashboard:** Lista dinámica de tiendas con carga de imágenes mediante Coil.

---


