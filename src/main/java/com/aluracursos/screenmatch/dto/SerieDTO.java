package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categoria;

public record SerieDTO(Long Id,
        String titulo,
        Integer totalDeTemporadas,
        Double evaluacion,
        Categoria genero,
        String actores,
        String poster,
        String sinopsis) {
}
