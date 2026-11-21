package fr.miage.menuswipe.flux.domain;

import java.util.Objects;

public final class CalculateurLinUcb {

    private final String versionDictionnaire;
    private final int dimension;
    private final double alpha;
    private final double[][] l;
    private final double[] theta;

    public CalculateurLinUcb(EtatLinUcb etat, double alpha) {
        Objects.requireNonNull(etat, "L'état du modèle est obligatoire.");

        if (!Double.isFinite(alpha) || alpha < 0) {
            throw new IllegalArgumentException(
                    "Alpha doit être fini et positif ou nul."
            );
        }

        this.versionDictionnaire = etat.versionDictionnaire();
        this.dimension = etat.dimension();
        this.alpha = alpha;

        // Factorisation de Cholesky : A = L × L transposée.
        this.l = factoriser(etat);

        double[] b = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            b[i] = etat.coefficientB(i);
        }

        // Résoudre A × theta = b en deux étapes.
        double[] y = resoudreInferieur(b);
        this.theta = resoudreTranspose(y);
    }

    public ScoreLinUcb evaluer(VecteurCaracteristiques caracteristiques) {
        Objects.requireNonNull(
                caracteristiques,
                "Les caractéristiques sont obligatoires."
        );

        if (caracteristiques.dimension() != dimension) {
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

        double[] x = caracteristiques.copieValeurs();

        double estimation = 0.0;

        for (int i = 0; i < dimension; i++) {
            estimation += x[i] * theta[i];
        }

        estimation = verifierFini(estimation);

        // Si L × z = x, alors xᵀ A⁻¹ x = zᵀ z.
        double[] z = resoudreInferieur(x);
        double norme = 0.0;

        for (double valeur : z) {
            norme = Math.hypot(norme, valeur);
        }

        double bonus = verifierFini(alpha * norme);

        return new ScoreLinUcb(estimation, bonus);
    }

    private static double[][] factoriser(EtatLinUcb etat) {
        int taille = etat.dimension();
        double[][] resultat = new double[taille][taille];

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j <= i; j++) {
                double valeur = etat.coefficientA(i, j);

                for (int k = 0; k < j; k++) {
                    valeur -= resultat[i][k] * resultat[j][k];
                }

                valeur = verifierFini(valeur);

                if (i == j) {
                    if (valeur <= 0) {
                        throw new ArithmeticException(
                                "La factorisation de Cholesky a échoué : "
                                        + "diagonale non positive."
                        );
                    }

                    resultat[i][j] = Math.sqrt(valeur);
                } else {
                    resultat[i][j] = verifierFini(
                            valeur / resultat[j][j]
                    );
                }
            }
        }

        return resultat;
    }

    // Résoudre L × résultat = secondMembre, de haut en bas.
    private double[] resoudreInferieur(double[] secondMembre) {
        double[] resultat = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            double valeur = secondMembre[i];

            for (int j = 0; j < i; j++) {
                valeur -= l[i][j] * resultat[j];
            }

            resultat[i] = verifierFini(valeur / l[i][i]);
        }

        return resultat;
    }

    // Résoudre Lᵀ × résultat = secondMembre, de bas en haut.
    private double[] resoudreTranspose(double[] secondMembre) {
        double[] resultat = new double[dimension];

        for (int i = dimension - 1; i >= 0; i--) {
            double valeur = secondMembre[i];

            for (int j = i + 1; j < dimension; j++) {
                valeur -= l[j][i] * resultat[j];
            }

            resultat[i] = verifierFini(valeur / l[i][i]);
        }

        return resultat;
    }

    private static double verifierFini(double valeur) {
        if (!Double.isFinite(valeur)) {
            throw new ArithmeticException(
                    "Le calcul produit une valeur numérique non finie."
            );
        }

        return valeur;
    }
}