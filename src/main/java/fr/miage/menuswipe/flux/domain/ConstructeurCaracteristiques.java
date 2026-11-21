package fr.miage.menuswipe.flux.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ConstructeurCaracteristiques {

    private final String versionDictionnaire;
    private final List<String> ingredientsOrdonnes;
    private final Set<String> ingredientsConnus;

    public ConstructeurCaracteristiques(
            String versionDictionnaire,
            List<String> ingredientsOrdonnes
    ) {
        if (versionDictionnaire == null
                || versionDictionnaire.isBlank()) {
            throw new IllegalArgumentException(
                    "La version du dictionnaire est obligatoire."
            );
        }

        if (ingredientsOrdonnes == null
                || ingredientsOrdonnes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Le dictionnaire doit contenir au moins un ingrédient."
            );
        }

        Set<String> identifiants = new HashSet<>();

        for (String ingredientId : ingredientsOrdonnes) {
            verifierIngredient(ingredientId);

            if (!identifiants.add(ingredientId)) {
                throw new IllegalArgumentException(
                        "Le dictionnaire contient un ingrédient en double."
                );
            }
        }

        this.versionDictionnaire = versionDictionnaire;
        this.ingredientsOrdonnes = List.copyOf(ingredientsOrdonnes);
        this.ingredientsConnus = Set.copyOf(identifiants);
    }

    public VecteurCaracteristiques construire(
            Set<String> ingredientsRecette,
            Set<String> ingredientsMenu
    ) {
        Set<String> recette = copierIngredients(ingredientsRecette);
        Set<String> menu = copierIngredients(ingredientsMenu);

        if (recette.isEmpty()) {
            throw new IllegalArgumentException(
                    "Une recette sans ingrédient ne peut pas être évaluée."
            );
        }

        if (!ingredientsConnus.containsAll(recette)) {
            throw new IllegalArgumentException(
                    "Un ingrédient de la recette est absent du dictionnaire."
            );
        }

        double[] valeurs = new double[ingredientsOrdonnes.size() + 2];
        valeurs[0] = 1.0;

        for (int i = 0; i < ingredientsOrdonnes.size(); i++) {
            String ingredientId = ingredientsOrdonnes.get(i);
            valeurs[i + 1] = recette.contains(ingredientId) ? 1.0 : 0.0;
        }

        int nombreIngredientsCommuns = 0;

        for (String ingredientId : recette) {
            if (menu.contains(ingredientId)) {
                nombreIngredientsCommuns++;
            }
        }

        double lienMenu =
                (double) nombreIngredientsCommuns / recette.size();

        valeurs[valeurs.length - 1] = lienMenu;

        return new VecteurCaracteristiques(
                versionDictionnaire,
                valeurs
        );
    }

    private static Set<String> copierIngredients(Set<String> ingredients) {
        if (ingredients == null) {
            throw new IllegalArgumentException(
                    "L'ensemble des ingrédients est obligatoire."
            );
        }

        Set<String> copie = new HashSet<>();

        for (String ingredientId : ingredients) {
            verifierIngredient(ingredientId);
            copie.add(ingredientId);
        }

        return copie;
    }

    private static void verifierIngredient(String ingredientId) {
        if (ingredientId == null || ingredientId.isBlank()) {
            throw new IllegalArgumentException(
                    "Chaque ingrédient doit avoir un identifiant non vide."
            );
        }
    }
}