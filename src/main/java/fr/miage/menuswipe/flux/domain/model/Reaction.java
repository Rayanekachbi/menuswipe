package fr.miage.menuswipe.flux.domain.model;

import java.time.Instant;
import java.util.Objects;

public record Reaction(
        String utilisateurId,
        String propositionId,
        TypeReaction type,
        Instant dateReaction
) {

    public Reaction {
        if (utilisateurId == null || utilisateurId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant de l'utilisateur est obligatoire."
            );
        }

        if (propositionId == null || propositionId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant de la proposition est obligatoire."
            );
        }

        Objects.requireNonNull(
                type,
                "Le type de réaction est obligatoire."
        );

        Objects.requireNonNull(
                dateReaction,
                "La date de la réaction est obligatoire."
        );
    }
}
