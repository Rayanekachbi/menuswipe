package fr.miage.menuswipe.flux.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateurLinUcbTest {

    private static final double PRECISION = 1e-10;

    private static final String VERSION =
            "ingredients-lien-semaine-v1";

    // Vecteur synthétique utilisé pour vérifier les calculs numériques.
    private static final VecteurCaracteristiques X =
            new VecteurCaracteristiques(
                    VERSION,
                    new double[]{1, 1, 0.5}
            );

    @Test
    void sansHistoriqueLEstimationEstNulleEtLeBonusEstCalcule() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0);
        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        ScoreLinUcb score = calculateur.evaluer(X);

        assertAll(
                () -> assertEquals(0.0, score.estimation(), PRECISION),
                () -> assertEquals(1.5, score.bonus(), PRECISION),
                () -> assertEquals(1.5, score.total(), PRECISION)
        );
    }

    @Test
    void apresAimeLeScoreCorrespondAuCalculAttendu() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        ScoreLinUcb score = new CalculateurLinUcb(etat, 1.0).evaluer(X);

        double estimationAttendue = 9.0 / 13.0;
        double bonusAttendu = 3.0 / Math.sqrt(13.0);

        assertAll(
                () -> assertEquals(
                        estimationAttendue, score.estimation(), PRECISION),
                () -> assertEquals(
                        bonusAttendu, score.bonus(), PRECISION),
                () -> assertEquals(
                        estimationAttendue + bonusAttendu,
                        score.total(),
                        PRECISION)
        );
    }

    @Test
    void apresAimePuisPasseLEstimationEtLeBonusDiminuent() {
        EtatLinUcb apresAime =
                new EtatLinUcb(VERSION, 3, 1.0)
                        .apresReaction(X, TypeReaction.AIME);

        EtatLinUcb apresPasse =
                apresAime.apresReaction(X, TypeReaction.PASSE);

        ScoreLinUcb avant =
                new CalculateurLinUcb(apresAime, 1.0).evaluer(X);

        ScoreLinUcb apres =
                new CalculateurLinUcb(apresPasse, 1.0).evaluer(X);

        assertAll(
                () -> assertEquals(
                        9.0 / 22.0, apres.estimation(), PRECISION),
                () -> assertEquals(
                        3.0 / Math.sqrt(22.0), apres.bonus(), PRECISION),
                () -> assertTrue(apres.estimation() < avant.estimation()),
                () -> assertTrue(apres.bonus() < avant.bonus())
        );
    }

    @Test
    void uneObservationInfluenceUneAutreRecette() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        VecteurCaracteristiques autreRecette =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 0, 1}
                );

        ScoreLinUcb score =
                new CalculateurLinUcb(etat, 1.0).evaluer(autreRecette);

        assertAll(
                () -> assertEquals(
                        6.0 / 13.0, score.estimation(), PRECISION),
                () -> assertEquals(
                        Math.sqrt(17.0 / 13.0), score.bonus(), PRECISION)
        );
    }

    @Test
    void alphaNulSupprimeLeBonus() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        ScoreLinUcb score = new CalculateurLinUcb(etat, 0.0).evaluer(X);

        assertAll(
                () -> assertEquals(
                        9.0 / 13.0, score.estimation(), PRECISION),
                () -> assertEquals(0.0, score.bonus(), PRECISION),
                () -> assertEquals(
                        score.estimation(), score.total(), PRECISION)
        );
    }

    @Test
    void tientCompteDeLambdaEtAlpha() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 2.0);

        ScoreLinUcb score = new CalculateurLinUcb(etat, 0.5).evaluer(X);

        assertAll(
                () -> assertEquals(0.0, score.estimation(), PRECISION),
                () -> assertEquals(
                        0.5 * Math.sqrt(2.25 / 2.0),
                        score.bonus(),
                        PRECISION)
        );
    }

    @Test
    void refuseUnAlphaInvalide() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0);

        double[] valeursInvalides = {
                -1,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double alpha : valeursInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new CalculateurLinUcb(etat, alpha)
            );
        }
    }

    @Test
    void refuseLesCaracteristiquesIncompatibles() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0);
        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        VecteurCaracteristiques autreVersion =
                new VecteurCaracteristiques(
                        "autre-version",
                        new double[]{1, 1, 0.5}
                );

        VecteurCaracteristiques autreDimension =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 1, 0, 0.5}
                );

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> calculateur.evaluer(autreVersion)),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> calculateur.evaluer(autreDimension))
        );
    }

    @Test
    void sansHistoriqueLEstimationSansLienSemaineEstNulle() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0);
        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        double estimation = calculateur.estimerSansLienSemaine(X);

        assertEquals(0.0, estimation, PRECISION);
    }

    @Test
    void estimeSansLienSemaineSansModifierLeVecteurNiLeScoreDuFlux() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        double[] valeursAvant = X.copieValeurs();
        ScoreLinUcb scoreAvant = calculateur.evaluer(X);

        double estimation = calculateur.estimerSansLienSemaine(X);

        ScoreLinUcb scoreApres = calculateur.evaluer(X);

        assertAll(
                () -> assertEquals(
                        8.0 / 13.0, estimation, PRECISION),
                () -> assertArrayEquals(
                        valeursAvant, X.copieValeurs()),
                () -> assertEquals(
                        0.5, X.valeur(2), PRECISION),
                () -> assertEquals(
                        9.0 / 13.0, scoreApres.estimation(), PRECISION),
                () -> assertEquals(
                        scoreAvant.estimation(),
                        scoreApres.estimation(),
                        PRECISION),
                () -> assertEquals(
                        scoreAvant.bonus(),
                        scoreApres.bonus(),
                        PRECISION)
        );
    }

    @Test
    void estimationSansLienSemaineIgnoreAlphaEtLaValeurDuLien() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        CalculateurLinUcb sansExploration =
                new CalculateurLinUcb(etat, 0.0);

        CalculateurLinUcb forteExploration =
                new CalculateurLinUcb(etat, 10.0);

        VecteurCaracteristiques lienNul =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 1, 0}
                );

        VecteurCaracteristiques lienMaximal =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 1, 1}
                );

        double estimationAttendue = 8.0 / 13.0;

        assertAll(
                () -> assertEquals(
                        estimationAttendue,
                        sansExploration.estimerSansLienSemaine(lienNul),
                        PRECISION),
                () -> assertEquals(
                        estimationAttendue,
                        sansExploration.estimerSansLienSemaine(lienMaximal),
                        PRECISION),
                () -> assertEquals(
                        estimationAttendue,
                        forteExploration.estimerSansLienSemaine(lienNul),
                        PRECISION),
                () -> assertEquals(
                        estimationAttendue,
                        forteExploration.estimerSansLienSemaine(lienMaximal),
                        PRECISION)
        );
    }

    @Test
    void estimationSansLienSemaineRefuseLesEntreesInvalides() {
        EtatLinUcb etat = new EtatLinUcb(VERSION, 3, 1.0);
        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        VecteurCaracteristiques autreVersion =
                new VecteurCaracteristiques(
                        "autre-version",
                        new double[]{1, 1, 0.5}
                );

        VecteurCaracteristiques autreDimension =
                new VecteurCaracteristiques(
                        VERSION,
                        new double[]{1, 1, 0, 0.5}
                );

        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> calculateur.estimerSansLienSemaine(null)),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> calculateur.estimerSansLienSemaine(
                                autreVersion)),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> calculateur.estimerSansLienSemaine(
                                autreDimension))
        );
    }
}