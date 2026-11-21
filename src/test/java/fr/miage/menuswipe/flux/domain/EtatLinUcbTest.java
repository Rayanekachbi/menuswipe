package fr.miage.menuswipe.flux.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EtatLinUcbTest {

    private static final double PRECISION = 1e-12;

    private static final VecteurCaracteristiques X =
            new VecteurCaracteristiques(
                    "ingredients-v1",
                    new double[]{1, 1, 0.5}
            );

    @Test
    void initialiseLaDiagonaleAvecLambdaEtBAvecZero() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 2.0);

        verifierEtat(
                etat,
                new double[][]{
                        {2, 0, 0},
                        {0, 2, 0},
                        {0, 0, 2}
                },
                new double[]{0, 0, 0}
        );

        assertEquals("ingredients-v1", etat.versionDictionnaire());
    }

    @Test
    void aimeMetAJourAEtB() {
        EtatLinUcb initial = new EtatLinUcb("ingredients-v1", 3, 1.0);

        EtatLinUcb resultat = initial.apresReaction(X, TypeReaction.AIME);

        verifierEtat(
                resultat,
                new double[][]{
                        {2, 1, 0.5},
                        {1, 2, 0.5},
                        {0.5, 0.5, 1.25}
                },
                new double[]{1, 1, 0.5}
        );

        assertEquals("ingredients-v1", resultat.versionDictionnaire());
    }

    @Test
    void passeApresAimeMetAJourAMaisConserveB() {
        EtatLinUcb initial = new EtatLinUcb("ingredients-v1", 3, 1.0);

        EtatLinUcb apresAime =
                initial.apresReaction(X, TypeReaction.AIME);

        EtatLinUcb apresPasse =
                apresAime.apresReaction(X, TypeReaction.PASSE);

        verifierEtat(
                apresPasse,
                new double[][]{
                        {3, 2, 1},
                        {2, 3, 1},
                        {1, 1, 1.5}
                },
                new double[]{1, 1, 0.5}
        );
    }

    @Test
    void uneMiseAJourNeModifiePasLEtatInitial() {
        EtatLinUcb initial = new EtatLinUcb("ingredients-v1", 3, 1.0);

        EtatLinUcb resultat = initial.apresReaction(X, TypeReaction.AIME);

        assertNotSame(initial, resultat);

        verifierEtat(
                initial,
                new double[][]{
                        {1, 0, 0},
                        {0, 1, 0},
                        {0, 0, 1}
                },
                new double[]{0, 0, 0}
        );
    }

    @Test
    void refuseUnVecteurDeDimensionDifferente() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0);

        VecteurCaracteristiques autreDimension =
                new VecteurCaracteristiques(
                        "ingredients-v1",
                        new double[]{1, 1, 0, 0.5}
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> etat.apresReaction(
                        autreDimension, TypeReaction.AIME)
        );
    }

    @Test
    void refuseUneVersionDeDictionnaireDifferente() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0);

        VecteurCaracteristiques autreVersion =
                new VecteurCaracteristiques(
                        "ingredients-v2",
                        new double[]{1, 1, 0.5}
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> etat.apresReaction(
                        autreVersion, TypeReaction.AIME)
        );
    }

    @Test
    void refuseUnLambdaInvalide() {
        double[] valeursInvalides = {
                0,
                -1,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double lambda : valeursInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new EtatLinUcb("ingredients-v1", 3, lambda)
            );
        }
    }

    private static void verifierEtat(
            EtatLinUcb etat,
            double[][] attenduA,
            double[] attenduB
    ) {
        assertEquals(attenduB.length, etat.dimension());

        for (int i = 0; i < attenduB.length; i++) {
            assertEquals(
                    attenduB[i],
                    etat.coefficientB(i),
                    PRECISION,
                    "Valeur incorrecte dans b à la position " + i
            );

            for (int j = 0; j < attenduB.length; j++) {
                assertEquals(
                        attenduA[i][j],
                        etat.coefficientA(i, j),
                        PRECISION,
                        "Valeur incorrecte dans A à la position "
                                + i + ", " + j
                );
            }
        }
    }
}