package fr.miage.menuswipe.exemple.domain.model;

import java.util.Objects;

/** Note enregistrée (reconstituée depuis la persistance). */
public record Note(NoteId id, String titre, String contenu) {

    public Note {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(titre, "titre");
        Objects.requireNonNull(contenu, "contenu");
    }
}
