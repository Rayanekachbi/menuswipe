package fr.miage.menuswipe.flux.domain.model;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTest {

    private static final Instant DATE =
            Instant.parse("2026-11-21T10:00:00Z");

    @Test
    void accepteUneReactionCompletePourChaqueType() {
        for (TypeReaction type : TypeReaction.values()) {
            Reaction reaction = new Reaction(
                    "utilisateur-1",
                    "proposition-1",
                    type,
                    DATE
            );

            assertAll(
                    () -> assertEquals(
                            "utilisateur-1", reaction.utilisateurId()),
                    () -> assertEquals(
                            "proposition-1", reaction.propositionId()),
                    () -> assertEquals(type, reaction.type()),
                    () -> assertEquals(DATE, reaction.dateReaction())
            );
        }
    }

    @Test
    void refuseUnIdentifiantUtilisateurAbsentOuVide() {
        String[] identifiantsInvalides = {null, "", "   "};

        for (String identifiant : identifiantsInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Reaction(
                            identifiant,
                            "proposition-1",
                            TypeReaction.AIME,
                            DATE
                    )
            );
        }
    }

    @Test
    void refuseUnIdentifiantPropositionAbsentOuVide() {
        String[] identifiantsInvalides = {null, "", "   "};

        for (String identifiant : identifiantsInvalides) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Reaction(
                            "utilisateur-1",
                            identifiant,
                            TypeReaction.AIME,
                            DATE
                    )
            );
        }
    }

    @Test
    void refuseUnTypeAbsent() {
        assertThrows(
                NullPointerException.class,
                () -> new Reaction(
                        "utilisateur-1",
                        "proposition-1",
                        null,
                        DATE
                )
        );
    }

    @Test
    void refuseUneDateAbsente() {
        assertThrows(
                NullPointerException.class,
                () -> new Reaction(
                        "utilisateur-1",
                        "proposition-1",
                        TypeReaction.AIME,
                        null
                )
        );
    }
}