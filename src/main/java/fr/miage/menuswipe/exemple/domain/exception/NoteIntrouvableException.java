package fr.miage.menuswipe.exemple.domain.exception;

import fr.miage.menuswipe.exemple.domain.model.NoteId;

public class NoteIntrouvableException extends ExempleException {

    public NoteIntrouvableException(NoteId id) {
        super("Note introuvable : " + id.valeur());
    }
}
