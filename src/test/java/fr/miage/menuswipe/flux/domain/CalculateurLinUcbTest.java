package fr.miage.menuswipe.flux.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateurLinUcbTest {

    private static final double PRECISION = 1e-10;

    private static final VecteurCaracteristiques X =
            new VecteurCaracteristiques(
                    "ingredients-v1",
                    new double[]{1, 1, 0.5}
            );

    @Test
    void sansHistoriqueLEstimationEstNulleEtLeBonusEstCalcule() {
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0);
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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0)
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
                new EtatLinUcb("ingredients-v1", 3, 1.0)
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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0)
                .apresReaction(X, TypeReaction.AIME);

        VecteurCaracteristiques autreRecette =
                new VecteurCaracteristiques(
                        "ingredients-v1",
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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0)
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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 2.0);

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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0);

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
        EtatLinUcb etat = new EtatLinUcb("ingredients-v1", 3, 1.0);
        CalculateurLinUcb calculateur = new CalculateurLinUcb(etat, 1.0);

        VecteurCaracteristiques autreVersion =
                new VecteurCaracteristiques(
                        "ingredients-v2",
                        new double[]{1, 1, 0.5}
                );

        VecteurCaracteristiques autreDimension =
                new VecteurCaracteristiques(
                        "ingredients-v1",
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
}