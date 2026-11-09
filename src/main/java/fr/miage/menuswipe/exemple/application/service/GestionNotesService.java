package fr.miage.menuswipe.exemple.application.service;

import java.util.List;

import fr.miage.menuswipe.exemple.application.port.in.CreerNoteCommande;
import fr.miage.menuswipe.exemple.application.port.in.GestionNotesUseCase;
import fr.miage.menuswipe.exemple.application.port.out.NoteRepository;
import fr.miage.menuswipe.exemple.domain.algo.MotsFrequents;
import fr.miage.menuswipe.exemple.domain.exception.NoteIntrouvableException;
import fr.miage.menuswipe.exemple.domain.model.MotFrequent;
import fr.miage.menuswipe.exemple.domain.model.Note;
import fr.miage.menuswipe.exemple.domain.model.NoteId;
import fr.miage.menuswipe.exemple.domain.model.NouvelleNote;

/** Implémente les cas d'utilisation en orchestrant le domaine et les ports de sortie. Sans Spring. */
public class GestionNotesService implements GestionNotesUseCase {

    private final NoteRepository notes;
    private final MotsFrequents motsFrequents = new MotsFrequents();

    public GestionNotesService(NoteRepository notes) {
        this.notes = notes;
    }

    @Override
    public Note creer(CreerNoteCommande commande) {
        return notes.ajouter(new NouvelleNote(commande.titre(), commande.contenu()));
    }

    @Override
    public Note trouver(NoteId id) {
        return notes.trouver(id).orElseThrow(() -> new NoteIntrouvableException(id));
    }

    @Override
    public List<Note> lister() {
        return notes.toutes();
    }

    @Override
    public List<MotFrequent> motsFrequents(NoteId id, int k) {
        return motsFrequents.calculer(trouver(id).contenu(), k);
    }
}
