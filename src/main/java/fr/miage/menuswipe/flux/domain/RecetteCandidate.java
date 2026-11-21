package fr.miage.menuswipe.flux.domain;

import java.util.Objects;

public record RecetteCandidate(
        String recetteId,
        VecteurCaracteristiques caracteristiques
) {

    public RecetteCandidate {
        if (recetteId == null || recetteId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant de la recette est obligatoire."
            );
        }

        Objects.requireNonNull(
                caracteristiques,
                "Les caractéristiques de la recette sont obligatoires."
        );
    }
}