package fr.miage.menuswipe.flux.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VecteurCaracteristiquesTest {

    private static final String VERSION =
            "ingredients-lien-semaine-v1";

    @Test
    void conserveLesCaracteristiquesEtLaVersion() {
        double[] valeurs = {1, 1, 1, 0, 0.5};

        VecteurCaracteristiques vecteur =
                new VecteurCaracteristiques(VERSION, valeurs);

        assertAll(
                () -> assertEquals(
                        VERSION, vecteur.versionDictionnaire()),
                () -> assertEquals(5, vecteur.dimension()),
                () -> assertEquals(0.5, vecteur.valeur(4)),
                () -> assertArrayEquals(
                        valeurs, vecteur.copieValeurs())
        );
    }

    @Test
    void accepteLesBornesDuLienSemaine() {
        assertAll(
                () -> assertDoesNotThrow(
                        () -> new VecteurCaracteristiques(
                                VERSION,
                                new double[]{1, 1, 0}
                        )
                ),
                () -> assertDoesNotThrow(
                        () -> new VecteurCaracteristiques(
                                VERSION,
                                new double[]{1, 1, 1}
                        )
                )
        );
    }

    @Test
    void uneModificationDuTableauInitialNeChangePasLeVecteur() {
        double[] valeurs = {1, 1, 0.5};

        VecteurCaracteristiques vecteur =
                new VecteurCaracteristiques(VERSION, valeurs);

        valeurs[1] = 0;
        valeurs[2] = 0;

        assertArrayEquals(
                new double[]{1, 1, 0.5},
                vecteur.copieValeurs()
        );
    }

    @Test
    void uneModificationDeLaCopieNeChangePasLeVecteur() {
        VecteurCaracteristiques vecteur =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 1, 0.5}
                );

        double[] copie = vecteur.copieValeurs();
        copie[1] = 0;
        copie[2] = 1;

        assertArrayEquals(
                new double[]{1, 1, 0.5},
                vecteur.copieValeurs()
        );
    }

    @Test
    void refuseUneVersionAbsenteOuVide() {
        String[] versionsInvalides = {null, "", "   "};

        for (String version : versionsInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new VecteurCaracteristiques(
                            version,
                            new double[]{1, 1, 0}
                    )
            );
        }
    }

    @Test
    void refuseLesVecteursInvalides() {
        double[][] vecteursInvalides = {
                null,
                {},
                {1, 0},
                {0, 1, 0.5},       // Constante différente de 1.
                {1, 0.5, 0.5},     // Indicateur non binaire.
                {1, -1, 0.5},      // Indicateur négatif.
                {1, 2, 0.5},       // Indicateur supérieur à 1.
                {1, 1, -0.1},      // LienSemaine négatif.
                {1, 1, 1.1},       // LienSemaine supérieur à 1.
                {Double.NaN, 1, 0},
                {1, Double.NaN, 0},
                {1, 1, Double.NaN},
                {1, Double.POSITIVE_INFINITY, 0},
                {1, 1, Double.NEGATIVE_INFINITY}
        };

        for (double[] valeurs : vecteursInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new VecteurCaracteristiques(
                            VERSION,
                            valeurs
                    )
            );
        }
    }
}