package fr.miage.menuswipe.flux.domain;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropositionTest {

    private static final Instant DATE =
            Instant.parse("2026-11-21T10:00:00Z");

    private static final VecteurCaracteristiques CARACTERISTIQUES =
            new VecteurCaracteristiques(
                    "ingredients-v1",
                    new double[]{1, 1, 0.5}
            );

    @Test
    void accepteUnePropositionSansMenu() {
        Proposition proposition = new Proposition(
                "proposition-1",
                "utilisateur-1",
                "recette-1",
                DATE,
                CARACTERISTIQUES
        );

        assertAll(
                () -> assertEquals(
                        "proposition-1", proposition.propositionId()),
                () -> assertEquals(
                        "utilisateur-1", proposition.utilisateurId()),
                () -> assertEquals(
                        "recette-1", proposition.recetteId()),
                () -> assertEquals(
                        DATE, proposition.dateProposition()),
                () -> assertEquals(
                        CARACTERISTIQUES, proposition.caracteristiques())
        );
    }

    @Test
    void refuseChaqueIdentifiantAbsentOuVide() {
        String[] identifiantsInvalides = {null, "", "   "};

        for (String identifiant : identifiantsInvalides) {
            assertAll(
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> new Proposition(
                                    identifiant,
                                    "utilisateur-1",
                                    "recette-1",
                                    DATE,
                                    CARACTERISTIQUES)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> new Proposition(
                                    "proposition-1",
                                    identifiant,
                                    "recette-1",
                                    DATE,
                                    CARACTERISTIQUES)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> new Proposition(
                                    "proposition-1",
                                    "utilisateur-1",
                                    identifiant,
                                    DATE,
                                    CARACTERISTIQUES)
                    )
            );
        }
    }

    @Test
    void refuseUneDateAbsente() {
        assertThrows(
                NullPointerException.class,
                () -> new Proposition(
                        "proposition-1",
                        "utilisateur-1",
                        "recette-1",
                        null,
                        CARACTERISTIQUES)
        );
    }

    @Test
    void refuseDesCaracteristiquesAbsentes() {
        assertThrows(
                NullPointerException.class,
                () -> new Proposition(
                        "proposition-1",
                        "utilisateur-1",
                        "recette-1",
                        DATE,
                        null)
        );
    }
}