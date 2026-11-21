package fr.miage.menuswipe.flux.domain;

import java.time.Instant;
import java.util.Objects;

public record Proposition(
        String propositionId,
        String utilisateurId,
        String recetteId,
        String menuId,
        Instant dateProposition,
        VecteurCaracteristiques caracteristiques
) {

    public Proposition {
        verifierIdentifiant(propositionId, "proposition");
        verifierIdentifiant(utilisateurId, "utilisateur");
        verifierIdentifiant(recetteId, "recette");
        verifierIdentifiant(menuId, "menu");

        Objects.requireNonNull(
                dateProposition,
                "La date de la proposition est obligatoire."
        );

        Objects.requireNonNull(
                caracteristiques,
                "Les caractéristiques de la proposition sont obligatoires."
        );
    }

    private static void verifierIdentifiant(
            String identifiant,
            String nom
    ) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant de " + nom + " est obligatoire."
            );
        }
    }
}