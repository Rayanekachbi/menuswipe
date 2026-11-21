package fr.miage.menuswipe.flux.domain;

public final class VecteurCaracteristiques {

    private final String versionDictionnaire;
    private final double[] valeurs;

    public VecteurCaracteristiques(
            String versionDictionnaire,
            double[] valeurs
    ) {
        if (versionDictionnaire == null
                || versionDictionnaire.isBlank()) {
            throw new IllegalArgumentException(
                    "La version du dictionnaire est obligatoire."
            );
        }

        if (valeurs == null || valeurs.length < 3) {
            throw new IllegalArgumentException(
                    "Le vecteur doit contenir une constante, "
                            + "au moins un indicateur d'ingrédient "
                            + "et le lien avec le menu."
            );
        }

        double[] copie = valeurs.clone();

        for (double valeur : copie) {
            if (!Double.isFinite(valeur)) {
                throw new IllegalArgumentException(
                        "Toutes les caractéristiques doivent être finies."
                );
            }
        }

        if (copie[0] != 1.0) {
            throw new IllegalArgumentException(
                    "La première caractéristique doit être égale à 1."
            );
        }

        for (int i = 1; i < copie.length - 1; i++) {
            if (copie[i] != 0.0 && copie[i] != 1.0) {
                throw new IllegalArgumentException(
                        "Chaque indicateur d'ingrédient doit valoir 0 ou 1."
                );
            }
        }

        double lienMenu = copie[copie.length - 1];

        if (lienMenu < 0.0 || lienMenu > 1.0) {
            throw new IllegalArgumentException(
                    "Le lien avec le menu doit être compris entre 0 et 1."
            );
        }

        this.versionDictionnaire = versionDictionnaire;
        this.valeurs = copie;
    }

    public String versionDictionnaire() {
        return versionDictionnaire;
    }

    public int dimension() {
        return valeurs.length;
    }

    public double valeur(int position) {
        return valeurs[position];
    }

    public double[] copieValeurs() {
        return valeurs.clone();
    }
}