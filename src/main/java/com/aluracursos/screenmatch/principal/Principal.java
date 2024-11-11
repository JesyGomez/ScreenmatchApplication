package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=4fc7c187";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie>datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;
    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu(){

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar serie por titulo
                    5 - Top 5 mejores series  
                    6 - Buscar series por categoría       
                    7 - Buscar series por temporadas y su evaluación   
                    8 - Buscar episodios por título 
                    9 - Top 5 episodios por serie    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCartegoria();
                    break;
                case 7: buscarSeriePorTemporadaYEvaluacion();
                    break;
                case 8: buscarEpisodiosPorTitulo();
                    break;
                case 9: buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().equalsIgnoreCase(nombreSerie))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }

            // Mapear episodios con la serie
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> {
                                Episodio episodio = new Episodio(d.numero(), e);
                                episodio.setSerie(serieEncontrada); // Set serie en cada episodio, (cada episodio está asociado a la serie actual antes de guardarlo)
                                return episodio;
                            }))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
            System.out.println("Episodios guardados en la base de datos para la serie " + serieEncontrada.getTitulo());
        } else {
            System.out.println("Serie no encontrada.");
        }

    }
    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else{
            System.out.println("La serie no ha sido encontrada");
        }
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + " Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCartegoria() {
        System.out.println("Escriba el genero/categoría de la serie que desea buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de las categorías " + genero);
        seriePorCategoria.forEach(System.out::println);
    }

    private void buscarSeriePorTemporadaYEvaluacion() {
        try {
            System.out.println("¿Filtrar series con cuántas temporadas?");
            var totalDeTemporadas = teclado.nextInt(); // Lee el entero para temporadas
            teclado.nextLine(); // Limpia el buffer

            System.out.println("¿A partir de qué valor debe ser la Evaluación? (e.g., 4.0, 8.2)");
            String evaluacionStr = teclado.nextLine(); // Lee como String
            Double evaluacion = Double.parseDouble(evaluacionStr); // Convierte a Double

            List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(totalDeTemporadas, evaluacion);
            System.out.println("*** Series Filtradas ***");
            filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " evaluación: " + s.getEvaluacion()));
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Error: Entrada no válida. Por favor ingresa un número entero para temporadas y un número decimal para evaluación.");
            teclado.nextLine(); // Limpia el buffer si hay un error
        }
    }
    private void buscarEpisodiosPorTitulo() {
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %S Evaluación %s\n",
                        e.getSerie(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodio = repositorio.top5Episodios(serie);
            topEpisodio.forEach(e ->
                    System.out.printf("Serie: %s \n Temporada %s \n Episodio %S \n Evaluación %s\n",
                            e.getSerie(), e.getTemporada(),
                            e.getTitulo(), e.getEvaluacion()));
        }
    }


//    private void buscarEpisodioPorSerie() {
//        mostrarSeriesBuscadas();
//        System.out.println("Escribe el nombre de la seria de la cual quieres ver los episodios");
//        var nombreSerie = teclado.nextLine();
//
//        Optional<Serie> serie = series.stream()
//                .filter(s -> s.getTitulo().toLowerCase().contains((nombreSerie.toUpperCase())))
//                .findFirst();
//
//        if(serie.isPresent()){
//            var serieEncontrada = serie.get();
//            List<DatosTemporadas> temporadas = new ArrayList<>();
//
//            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
//                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
//                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
//                temporadas.add(datosTemporada);
//            }
//            temporadas.forEach(System.out::println);
//
//            List<Episodio> episodios = temporadas.stream()
//                    .flatMap(d -> d.episodios().stream()
//                            .map(e -> new Episodio(d.numero(), e)))
//                    .collect(Collectors.toList());
//
//            serieEncontrada.setEpisodios(episodios);
//            repositorio.save(serieEncontrada);
//        }
//
//        }


    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


//        System.out.println("Por favor ingresa el nombre de la serie que deseas buscar");
//
//        //Busca los datos generales de la serie
//
//        var nombreSerie = teclado.nextLine();
//        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
//        var datos = conversor.obtenerDatos(json, DatosSerie.class);
//        System.out.println(datos);
//
//        //Buscamos los datos de toda la temporada
//        List<DatosTemporadas> temporadas = new ArrayList<>();
//        for (int i = 1; i < datos.totalDeTemporadas(); i++) {
//            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") +"&Season=" +i+API_KEY);
//            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
//            temporadas.add(datosTemporadas);
//        }
//       // temporadas.forEach(System.out::println);
//
//        //Mostrar el título de los episodios de cada temporada
////        for (int i = 0; i < datos.totalDeTemporadas(); i++) {
////            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
////            for (int j = 0; j < episodiosTemporada.size(); j++) {
////                System.out.println(episodiosTemporada.get(j).titulo());
////            }
////        }
//
//        //función que nos da mismo resultado mejor practica
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
//        //Convertir todas las informaciones a una lista del tipo DatosEpisodio
//
//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                        .collect(Collectors.toList());
//
//        //Top 5 episodios
////        System.out.println("Top 5 episodios");
////        datosEpisodios.stream()
////                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
////                .limit(5)
////                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
////                .forEach(System.out::println);
//
//        //Convertir los datos a una lista del tipo Episodio
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                .map(d -> new Episodio(t.numero(), d)))
//                .collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
//        //Busqueda de episodios a partir de x año
//        System.out.println("Por favor ingresa el año a partir del cual deseas ver los episodios de tu serie favorita");
//         var fecha = teclado.nextInt();
//         teclado.nextLine();
//
//        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e-> System.out.println(
//                        "Temporada " + e.getTemporada() +
//                                " Episodio " + e.getTitulo() +
//                                " Fecha de Lanzamiento " + e.getFechaDeLanzamiento().format(dtf)
//                ));
//
//        //Busqueda de titulo por partes
//        System.out.println("Ingresa el nombre el título de episodio que deseas ver:");
//        var parteTitulo = teclado.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(parteTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episodio encontrado:");
//            System.out.println("Los datos son: " + episodioBuscado.get());
//        }else{
//            System.out.println("Episodio No encontrado");
//        }
//
//        Map<Integer, Double> evaluacionesPorTemporadas = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getEvaluacion)));
//        System.out.println(evaluacionesPorTemporadas);
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println( "Media de las evaluaciones: " + est.getAverage());
//        System.out.println("Episodio Mejor evaluado: " + est.getMax());
//        System.out.println("Episodio Peor evaluado: " + est.getMin());
//    }


}
