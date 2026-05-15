package com.aquavitae.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmnResponse {

    // La respuesta de SMN es una lista de objetos con la siguiente estructura.
    // En realidad es un array JSON, pero para simplificar mapeamos un objeto genérico.
    // Usaremos Jackson para leer el array completo.

    // No mapeamos directamente; dejamos que el adaptador procese el JSON en crudo.
}