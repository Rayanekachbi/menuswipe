package fr.miage.menuswipe.flux.domain.algo;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import fr.miage.menuswipe.flux.domain.model.EtatLinUcb;
import fr.miage.menuswipe.flux.domain.model.RecetteCandidate;

public final class SelectionneurLinUcb {

    private final double alpha;

    public SelectionneurLinUcb(double alpha) {
        if (!Double.isFinite(alpha) || alpha < 0) {
            throw new IllegalArgumentException(
                    "Alpha doit être fini et positif ou nul."
            );
        }

        this.alpha = alpha;
    }

    public Optional<RecetteCandidate> selectionner(
            EtatLinUcb etat,
            List<RecetteCandidate> candidates
    ) {
        Objects.requireNonNull(
                etat,
                "L'état du modèle est obligatoire."
        );

        Objects.requireNonNull(
                candidates,
                "La liste des recettes candidates est obligatoire."
        );

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        CalculateurLinUcb calculateur =
                new CalculateurLinUcb(etat, alpha);

        Set<String> identifiantsRencontres = new HashSet<>();

        RecetteCandidate meilleure = null;
        double meilleurScore = Double.NEGATIVE_INFINITY;

        for (RecetteCandidate candidate : candidates) {
            Objects.requireNonNull(
                    candidate,
                    "Une recette candidate ne peut pas être absente."
            );

            if (!identifiantsRencontres.add(candidate.recetteId())) {
                throw new IllegalArgumentException(
                        "Une recette apparaît plusieurs fois parmi les candidates."
                );
            }

            double score = calculateur
                    .evaluer(candidate.caracteristiques())
                    .total();

            boolean scoreSuperieur = score > meilleurScore;

            boolean egaliteAvecIdentifiantPrioritaire =
                    meilleure != null
                            && score == meilleurScore
                            && candidate.recetteId()
                                    .compareTo(meilleure.recetteId()) < 0;

            if (meilleure == null
                    || scoreSuperieur
                    || egaliteAvecIdentifiantPrioritaire) {
                meilleure = candidate;
                meilleurScore = score;
            }
        }

        return Optional.ofNullable(meilleure);
    }
}