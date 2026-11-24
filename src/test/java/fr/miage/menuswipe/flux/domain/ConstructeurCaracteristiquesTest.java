package fr.miage.menuswipe.flux.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstructeurCaracteristiquesTest {

    private static final String VERSION =
            "ingredients-lien-semaine-v1";

    private static final double TOLERANCE = 1e-12;

    private final ConstructeurCaracteristiques constructeur =
            new ConstructeurCaracteristiques(
                    VERSION,
                    List.of("pates", "champignons", "oeufs")
            );

    @Test
    void construitLeVecteurAvecUnIngredientCommunSurDeux() {
        Set<String> ingredientsRecettesAimeesRecemment =
                Set.of("oeufs", "champignons");

        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                ingredientsRecettesAimeesRecemment
        );

        assertAll(
                () -> assertArrayEquals(
                        new double[]{1, 1, 1, 0, 0.5},
                        resultat.copieValeurs(),
                        TOLERANCE
                ),
                () -> assertEquals(
                        VERSION,
                        resultat.versionDictionnaire()
                )
        );
    }

    @Test
    void sansIngredientsDeRecettesAimeesRecemmentLeLienEstNul() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                Set.of()
        );

        assertArrayEquals(
                new double[]{1, 1, 1, 0, 0},
                resultat.copieValeurs(),
                TOLERANCE
        );
    }

    @Test
    void desIngredientsTousCommunsDonnentUnLienEgalAUn() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons"),
                Set.of("pates", "champignons", "oeufs")
        );

        assertEquals(1.0, resultat.valeur(4), TOLERANCE);
    }

    @Test
    void diviseParLeNombreIngredientsDeLaRecette() {
        VecteurCaracteristiques resultat = constructeur.construire(
                Set.of("pates", "champignons", "oeufs"),
                Set.of("champignons", "tomates", "riz", "carottes")
        );

        assertEquals(1.0 / 3.0, resultat.valeur(4), TOLERANCE);
    }

    @Test
    void leGratinPartageDeuxIngredientsSurTroisAvecLaCarbonaraAimee() {
        ConstructeurCaracteristiques constructeurExemple =
                new ConstructeurCaracteristiques(
                        VERSION,
                        List.of("pates", "creme", "lardons", "fromage")
                );

        Set<String> ingredientsGratin =
                Set.of("pates", "creme", "fromage");

        Set<String> ingredientsRecettesAimeesRecemment =
                Set.of("pates", "creme", "lardons");

        VecteurCaracteristiques resultat =
                constructeurExemple.construire(
                        ingredientsGratin,
                        ingredientsRecettesAimeesRecemment
                );

        assertArrayEquals(
                new double[]{1, 1, 1, 0, 1, 2.0 / 3.0},
                resultat.copieValeurs(),
                TOLERANCE
        );
    }

    @Test
    void conserveLOrdreDuDictionnaireMalgreUneModificationExterieure() {
        List<String> dictionnaire = new ArrayList<>(
                List.of("oeufs", "champignons", "pates")
        );

        ConstructeurCaracteristiques autreConstructeur =
                new ConstructeurCaracteristiques(
                        "ingredients-lien-semaine-v2",
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
                TOLERANCE
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
                        VERSION,
                        List.of("pates", "champignons", "pates")
                )
        );
    }
}