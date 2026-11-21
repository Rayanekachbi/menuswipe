package fr.miage.menuswipe.flux.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectionneurLinUcbTest {

    private static final VecteurCaracteristiques X =
            new VecteurCaracteristiques(
                    "ingredients-v1",
                    new double[]{1, 1, 0, 0}
            );

    private static final VecteurCaracteristiques Y =
            new VecteurCaracteristiques(
                    "ingredients-v1",
                    new double[]{1, 0, 1, 0}
            );

    @Test
    void sansExplorationChoisitLaMeilleureEstimation() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        RecetteCandidate appreciee = new RecetteCandidate("recette-b", X);
        RecetteCandidate autre = new RecetteCandidate("recette-a", Y);

        RecetteCandidate resultat = new SelectionneurLinUcb(0.0)
                .selectionner(etat, List.of(autre, appreciee))
                .orElseThrow();

        assertEquals("recette-b", resultat.recetteId());
    }

    @Test
    void leBonusPeutFaireChoisirUneRecetteMoinsBienEstimee() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        RecetteCandidate appreciee = new RecetteCandidate("recette-b", X);
        RecetteCandidate autre = new RecetteCandidate("recette-a", Y);

        RecetteCandidate resultat = new SelectionneurLinUcb(1.0)
                .selectionner(etat, List.of(appreciee, autre))
                .orElseThrow();

        assertEquals("recette-a", resultat.recetteId());
    }

    @Test
    void departageLesEgalitesIndependammentDeLOrdreDeLaListe() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0);
        SelectionneurLinUcb selectionneur = new SelectionneurLinUcb(1.0);

        RecetteCandidate a = new RecetteCandidate("recette-a", X);
        RecetteCandidate b = new RecetteCandidate("recette-b", X);

        assertAll(
                () -> assertEquals(
                        "recette-a",
                        selectionneur.selectionner(etat, List.of(b, a))
                                .orElseThrow().recetteId()
                ),
                () -> assertEquals(
                        "recette-a",
                        selectionneur.selectionner(etat, List.of(a, b))
                                .orElseThrow().recetteId()
                )
        );
    }

    @Test
    void uneListeVideNeDonneAucuneSelection() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0);

        assertTrue(
                new SelectionneurLinUcb(1.0)
                        .selectionner(etat, List.of())
                        .isEmpty()
        );
    }

    @Test
    void selectionneLaSeuleCandidateDisponible() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0);
        RecetteCandidate candidate = new RecetteCandidate("recette-a", X);

        RecetteCandidate resultat = new SelectionneurLinUcb(1.0)
                .selectionner(etat, List.of(candidate))
                .orElseThrow();

        assertEquals(candidate, resultat);
    }

    @Test
    void refuseDeuxCandidatesAvecLeMemeIdentifiant() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0);

        RecetteCandidate premiere = new RecetteCandidate("recette-a", X);
        RecetteCandidate seconde = new RecetteCandidate("recette-a", Y);

        assertThrows(
                IllegalArgumentException.class,
                () -> new SelectionneurLinUcb(1.0)
                        .selectionner(etat, List.of(premiere, seconde))
        );
    }

    @Test
    void selectionnerNeModifiePasLEtatDApprentissage() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 4, 1.0);
        RecetteCandidate candidate = new RecetteCandidate("recette-a", X);

        new SelectionneurLinUcb(1.0)
                .selectionner(etat, List.of(candidate));

        for (int i = 0; i < etat.dimension(); i++) {
            assertEquals(0.0, etat.coefficientB(i), 1e-12);

            for (int j = 0; j < etat.dimension(); j++) {
                double attendu = i == j ? 1.0 : 0.0;
                assertEquals(attendu, etat.coefficientA(i, j), 1e-12);
            }
        }
    }
}