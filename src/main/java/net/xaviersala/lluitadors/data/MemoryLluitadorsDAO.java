package net.xaviersala.lluitadors.data;

import net.xaviersala.lluitadors.errors.LluitadorException;
import net.xaviersala.lluitadors.lluita.Lluitador;

import java.util.*;
/**
 * Implementació de LluitadorsDAO que se n'encarrega de
 * recuperar dades de la base de dades.
 *
 * @author xavier
 *
 */
public class MemoryLluitadorsDAO implements LluitadorsDAO {

    private Map<String, Lluitador> lluitadors;

    public MemoryLluitadorsDAO() {
        lluitadors = new HashMap<>();
        emplenaLluitadors();
    }

    public MemoryLluitadorsDAO(List<Lluitador> llista) {
        for (Lluitador lluitador : llista) {
            lluitadors.put(lluitador.getNom(), lluitador);
        }

    }

    @Override
    public List<Lluitador> getLluitadors() {
        return new ArrayList<>(lluitadors.values());
    }

    @Override
    public Lluitador getLluitador(String nom) {
        if (lluitadors.containsKey(nom)) {
            return lluitadors.get(nom);
        }
        return null;
    }

    @Override
    public boolean afegirLluitador(String nom, int forca) throws LluitadorException {
        if (lluitadors.containsKey(nom)) {
            return false;
        } else {
            lluitadors.put(nom, new Lluitador(nom, forca));
            return true;
        }
    }

    /**
     * Elimina tots els lluitadors del campionat.
     */
    @Override
    public void buida() {
        lluitadors.clear();
    }

    private void emplenaLluitadors() {

        Random aleatori = new Random();
        List<String> noms = Arrays.asList("Matagalls", "AixafaGuitarres", "Demolidor", "Arrassador", "Bestia",
                "Bongo", "Canival", "Salvatge", "Destroyer", "Martell de persones", "MasclePelut",
                "Matador", "Toro Boig", "Terrible", "Punxa");

        for (String nom : noms) {
            lluitadors.put(nom, new Lluitador(nom, aleatori.nextInt(10)));
        }

    }

}
