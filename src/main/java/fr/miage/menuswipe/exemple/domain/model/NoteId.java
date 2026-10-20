package fr.miage.menuswipe.exemple.domain.model;

import fr.miage.menuswipe.exemple.domain.exception.DonneeInvalideException;

/** Objet valeur : un identifiant typé évite de confondre les identifiants de différentes entités. */
public record NoteId(long valeur) {

    public NoteId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de note invalide : " + valeur);
        }
    }
}
