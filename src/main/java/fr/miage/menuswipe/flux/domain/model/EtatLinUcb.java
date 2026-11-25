package fr.miage.menuswipe.flux.domain.model;

import java.util.Objects;

public final class EtatLinUcb {

    private final String versionDictionnaire;
    private final double[][] a;
    private final double[] b;

    public EtatLinUcb(
            String versionDictionnaire,
            int dimension,
            double lambda
    ) {
        if (versionDictionnaire == null
                || versionDictionnaire.isBlank()) {
            throw new IllegalArgumentException(
                    "La version du dictionnaire est obligatoire."
            );
        }

        if (dimension < 3) {
            throw new IllegalArgumentException(
                    "La dimension doit être au moins égale à 3."
            );
        }

        if (!Double.isFinite(lambda) || lambda <= 0) {
            throw new IllegalArgumentException(
                    "Lambda doit être un nombre fini strictement positif."
            );
        }

        this.versionDictionnaire = versionDictionnaire;
        this.a = new double[dimension][dimension];
        this.b = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            this.a[i][i] = lambda;
        }
    }

    private EtatLinUcb(
            String versionDictionnaire,
            double[][] a,
            double[] b
    ) {
        this.versionDictionnaire = versionDictionnaire;
        this.a = a;
        this.b = b;
    }

    public EtatLinUcb apresReaction(
            VecteurCaracteristiques caracteristiques,
            TypeReaction reaction
    ) {
        Objects.requireNonNull(
                caracteristiques,
                "Les caractéristiques sont obligatoires."
        );

        Objects.requireNonNull(
                reaction,
                "Le type de réaction est obligatoire."
        );

        if (caracteristiques.dimension() != dimension()) {
            throw new IllegalArgumentException(
                    "La dimension du vecteur ne correspond pas au modèle."
            );
        }

        if (!versionDictionnaire.equals(
                caracteristiques.versionDictionnaire())) {
            throw new IllegalArgumentException(
                    "La version du dictionnaire ne correspond pas au modèle."
            );
        }

        double recompense = switch (reaction) {
            case AIME -> 1.0;
            case PASSE -> 0.0;
        };

        double[] x = caracteristiques.copieValeurs();
        double[][] nouvelA = new double[dimension()][dimension()];
        double[] nouveauB = new double[dimension()];

        for (int i = 0; i < dimension(); i++) {
            nouveauB[i] = b[i] + recompense * x[i];
            verifierNombreFini(nouveauB[i]);

            for (int j = 0; j < dimension(); j++) {
                nouvelA[i][j] = a[i][j] + x[i] * x[j];
                verifierNombreFini(nouvelA[i][j]);
            }
        }

        return new EtatLinUcb(
                versionDictionnaire,
                nouvelA,
                nouveauB
        );
    }

    public String versionDictionnaire() {
        return versionDictionnaire;
    }

    public int dimension() {
        return b.length;
    }

    public double coefficientA(int ligne, int colonne) {
        return a[ligne][colonne];
    }

    public double coefficientB(int position) {
        return b[position];
    }

    private static void verifierNombreFini(double valeur) {
        if (!Double.isFinite(valeur)) {
            throw new ArithmeticException(
                    "La mise à jour dépasse les limites numériques."
            );
        }
    }
}