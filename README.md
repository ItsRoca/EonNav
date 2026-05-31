<h1 align="center">EonNav</h1>

<p align="left">
<img src="https://img.shields.io/badge/ESTADO-EN%20DESARROLLO-green">
</p>

![GitHub Org's stars](https://img.shields.io/github/stars/itsRoca?style=social)

<p align="center">
Aplicación Android para centralizar herramientas relacionadas con Pokémon.
</p>

---

## 📖 Índice
- [Descripción del proyecto](#descripción-del-proyecto)
- [Estado del proyecto](#estado-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [Acceso al proyecto](#acceso-al-proyecto)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Autor](#autor)

---

## 📌 Descripción del proyecto

EonNav es una aplicación para dispositivos Android que reúne distintas utilidades relacionadas con Pokémon. Incluye una Pokédex interactiva utilizando datos de PokeAPI y un sistema de creación de equipos.

---

## 🚧 Estado del proyecto

<h4 align="center">
🚧 Proyecto finalizado (TFG) con posibles mejoras futuras 🚧
</h4>

---

## 🔨 Funcionalidades

- `Funcionalidad 1`: Pokédex interactiva con todos los Pokémon hasta novena generación.
- `Funcionalidad 1a`: Buscador y filtros.
- `Funcionalidad 2`: Guardado de favoritos.
- `Funcionalidad 3`: Creación de equipos con personalización por Pokémon.
- `Funcionalidad 3a`: Edición y eliminación de equipos.

---

## 📁 Acceso al proyecto

Puedes clonar este repositorio con:

```bash
git clone https://github.com/ItsRoca/EonNav.git
````

Y el repositorio del servidor con:

```bash
git clone https://github.com/ItsRoca/EonNav-Backend.git
````

---

## 🛠️ Tecnologías utilizadas

### Frontend

- Java (Android)
- XML
- Android Studio
- RecyclerView
- Fragments y Activities

### Backend

- Django
- Django REST Framework

### API externa

- PokeAPI

---

## ▶️ Ejecución del proyecto

### 🤖 Backend (Django)

1. Acceder a la carpeta del backend:
```bash
cd backend
````
Ejecutar el servidor:
````bash
python manage.py runserver
````
Se podrá acceder al servidor en:
````bash
http://localhost:8000
````

### 🤖 Aplicación Android

- Abrir el proyecto en Android Studio
- Esperar a que carguen las dependencias
- Ejecutar la aplicación en un emulador o dispositivo físico

### ⚠️ Nota
Es necesario que el backend esté funcionando para evitar errores relacionados con las funciones de favoritos y la creación de equipos.

---

## 🧑 Autor

Adrián Roca Santiago
TFG - Desarrollo de Aplicaciones Multiplataforma
