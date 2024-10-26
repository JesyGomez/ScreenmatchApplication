# ScreenMatch

ScreenMatch es una aplicación Java que permite consultar información detallada sobre series de televisión utilizando la API de [OMDb](http://www.omdbapi.com/). El programa permite buscar series por nombre, explorar episodios por temporada, filtrar episodios por fecha de lanzamiento, y mucho más.

## Funcionalidades
- **Buscar Serie**: Ingresa el nombre de una serie y obtén datos generales.
- **Consultar Temporadas**: Recupera la información de todas las temporadas de la serie.
- **Listar Episodios**: Muestra todos los episodios de la serie seleccionada.
- **Top 5 Episodios**: Genera un top 5 de episodios mejor evaluados.
- **Filtrar por Fecha**: Muestra episodios lanzados a partir de un año específico.
- **Buscar Episodio por Título**: Encuentra un episodio por una parte del título.
- **Evaluaciones**: Calcula la media de evaluaciones, la mejor y peor evaluadas por temporada.

## Tecnologías Utilizadas
- **Java**
- **Spring Framework**
- **API de OMDb**
- **Manejo de JSON**
- **Stream API (Java 8)**
- **Java Time API**

## Requisitos Previos
Antes de ejecutar el proyecto, asegúrate de tener instalados:
- [Java 17 o superior](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven](https://maven.apache.org/install.html)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
- Clave API para [OMDb](http://www.omdbapi.com/) (reemplaza tu clave en `API_KEY`).

## Instalación
1. Clona el repositorio:
   ```bash
   git clone (https://github.com/JesyGomez/ScreenmatchApplication.git)
