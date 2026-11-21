package fr.miage.menuswipe.flux.domain;

public record ScoreLinUcb(double estimation, double bonus) {

    public ScoreLinUcb {
        if (!Double.isFinite(estimation)) {
            throw new IllegalArgumentException(
                    "L'estimation doit être finie."
            );
        }

        if (!Double.isFinite(bonus) || bonus < 0) {
            throw new IllegalArgumentException(
                    "Le bonus doit être fini et positif ou nul."
            );
        }

        if (!Double.isFinite(estimation + bonus)) {
            throw new IllegalArgumentException(
                    "Le score total doit être fini."
            );
        }
    }

    public double total() {
        return estimation + bonus;
    }
}