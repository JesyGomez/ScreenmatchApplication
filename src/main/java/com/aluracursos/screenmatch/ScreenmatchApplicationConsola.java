//package com.aluracursos.screenmatch;
//
//import com.aluracursos.screenmatch.principal.Principal;
//import com.aluracursos.screenmatch.repository.SerieRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ScreenmatchApplicationConsola implements CommandLineRunner {
//
//	@Autowired
//	private SerieRepository repository;
//	public static void main(String[] args) {
//		SpringApplication.run(ScreenmatchApplicationConsola.class, args);
//	}
//
//	@Override
//	public void run(String... args) throws Exception {
//		Principal principal = new Principal(repository);
//		principal.muestraElMenu();
//
//
//
//
////		var consumoApi = new ConsumoAPI();
////		var json = consumoApi.obtenerDatos("https://www.omdbapi.com/?t=game+of+thrones&apikey=4fc7c187");
////		System.out.println(json);
////		ConvierteDatos conversor = new ConvierteDatos();
////		var datos = conversor.obtenerDatos(json, DatosSerie.class);
////		System.out.println(datos);
////		json = consumoApi.obtenerDatos("https://www.omdbapi.com/?t=game+of+thrones&Season=1&episode=1&apikey=4fc7c187");
////		DatosEpisodio episodio = conversor.obtenerDatos(json, DatosEpisodio.class);
////		System.out.println(episodio);
//
//	}
//
//}
