package com.explorateur.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse paginée générique")
public class PageResponse<T> {
    
    @Schema(description = "Liste des éléments de la page courante")
    private List<T> content;
    
    @Schema(description = "Numéro de la page courante (commence à 0)")
    private int page;
    
    @Schema(description = "Nombre d'éléments par page")
    private int size;
    
    @Schema(description = "Nombre total d'éléments")
    private long totalElements;
    
    @Schema(description = "Nombre total de pages")
    private int totalPages;
    
    @Schema(description = "Est-ce la première page")
    private boolean first;
    
    @Schema(description = "Est-ce la dernière page")
    private boolean last;
    
    @Schema(description = "Y a-t-il une page suivante")
    private boolean hasNext;
    
    @Schema(description = "Y a-t-il une page précédente")
    private boolean hasPrevious;
}
