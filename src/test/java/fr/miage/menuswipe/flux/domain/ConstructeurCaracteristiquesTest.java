package fr.miage.menuswipe.flux.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstructeurCaracteristiquesTest {

    private final ConstructeurCaracteristiques constructeur =
            new ConstructeurCaracteristiques(
                    "ingredients-v1",
                    List.of("pates", "champignons", "oeufs")
            );

    @Test
    void construitLeVecteurAvecUnIngredientCommunSurDeux() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                Set.of("oeufs", "champignons")
        );

        assertAll(
                () -> assertArrayEquals(
                        new double[]{1, 1, 1, 0, 0.5},
                        resultat.copieValeurs(),
                        1e-12
                ),
                () -> assertEquals(
                        "ingredients-v1",
                        resultat.versionDictionnaire()
                )
        );
    }

    @Test
    void unMenuVideDonneUnLienNul() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                Set.of()
        );

        assertArrayEquals(
                new double[]{1, 1, 1, 0, 0},
                resultat.copieValeurs(),
                1e-12
        );
    }

    @Test
    void desIngredientsTousCommunsDonnentUnLienEgalAUn() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                Set.of("pates", "champignons", "oeufs")
        );

        assertEquals(1.0, resultat.valeur(4), 1e-12);
    }

    @Test
    void diviseParLeNombreIngredientsDeLaRecette() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons", "oeufs"),
                Set.of("champignons", "tomates", "riz", "carottes")
        );

        assertEquals(1.0 / 3.0, resultat.valeur(4), 1e-12);
    }

    @Test
    void conserveLOrdreDuDictionnaireMalgreUneModificationExterieure() {
        List<String> dictionnaire = new ArrayList<>(
                List.of("oeufs", "champignons", "pates")
        );

        ConstructeurCaracteristiques autreConstructeur =
                new ConstructeurCaracteristiques(
                        "ingredients-v2",
                        dictionnaire
                );

        dictionnaire.set(0, "tomates");

        VecteurCaracteristiques resultat = autreConstructeur.construire(
                Set.of("pates", "champignons"),
                Set.of("champignons")
        );

        assertArrayEquals(
                new double[]{1, 0, 1, 1, 0.5},
                resultat.copieValeurs(),
                1e-12
        );
    }

    @Test
    void refuseUneRecetteSansIngredient() {
        assertThrows(
                IllegalArgumentException.class,
                () -> constructeur.construire(
                        Set.of(),
                        Set.of("champignons")
                )
        );
    }

    @Test
    void refuseUneRecetteAvecUnIngredientAbsentDuDictionnaire() {
        assertThrows(
                IllegalArgumentException.class,
                () -> constructeur.construire(
                        Set.of("pates", "tomates"),
                        Set.of()
                )
        );
    }

    @Test
    void refuseUnDictionnaireAvecDesDoublons() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConstructeurCaracteristiques(
                        "ingredients-v1",
                        List.of("pates", "champignons", "pates")
                )
        );
    }
}