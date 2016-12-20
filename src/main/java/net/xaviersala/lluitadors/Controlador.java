package net.xaviersala.lluitadors;

import net.xaviersala.lluitadors.data.LluitadorsDAO;
import net.xaviersala.lluitadors.data.MemoryLluitadorsDAO;
import net.xaviersala.lluitadors.errors.LluitadorException;
import net.xaviersala.lluitadors.lluita.Lluitador;
import net.xaviersala.lluitadors.lluita.Ring;
import net.xaviersala.lluitadors.vista.Resultat;

import java.util.List;

/**
 * Classe que se n'encarrega del control i recuperació de les dades.
 *
 * @author xavier
 *
 */
class Controlador {

    public static final String NO_EXISTEIX_EL_LLUITADOR = "No existeix el lluitador ";
    public static final String EL_LLUITADOR_JA_HI_ES = "EL lluitador ja hi és";

    private static LluitadorsDAO dades = new MemoryLluitadorsDAO();
    private static Ring ring = new Ring();

    /**
     * Obtenir tota la llista dels lluitadors.
     *
     * @return llista dels lluitadors
     */
    List<Lluitador> getTotsElsLluitadors() {

        return dades.getLluitadors();
    }

    /**
     * Fa que dos lluitadors s'enfrontin.
     *
     * @param nom1 primer lluitador
     * @param nom2 segon lluitador
     * @return Retorna el resultat o genera una excepció
     * @throws LluitadorException En cas d'error es genera una excepció
     */
    Resultat lluita(String nom1, String nom2) throws LluitadorException {
        Lluitador lluitador1 = dades.getLluitador(nom1);
        if (lluitador1 == null) {
            throw new LluitadorException(NO_EXISTEIX_EL_LLUITADOR + nom1);
        }
        Lluitador lluitador2 = dades.getLluitador(nom2);
        if (lluitador2 == null) {
            throw new LluitadorException(NO_EXISTEIX_EL_LLUITADOR + nom2);
        }

        return ring.combat(lluitador1, lluitador2);
    }

    /**
     * Obtenir un lluitador de la font de dades.
     *
     * @param nom nom del lluitador a trobar
     * @return retorna el lluitador o genera una excepció
     * @throws LluitadorException
     */
    Lluitador getLluitador(String nom) throws LluitadorException {

      Lluitador ll =  dades.getLluitador(nom);
      if (ll == null) {
        throw new LluitadorException(NO_EXISTEIX_EL_LLUITADOR + nom);
      }
      return ll;
    }

    /**
     * Buida la base de dades.
     */
    public void buida() {
        dades.buida();
    }

    /**
     * Afegir un lluitador a la base de dades
     * @param nom nom del lluitador
     * @param forca força que té
     * @throws LluitadorException Alguna cosa no va bé ...
     */
    public void afegirLluitador(String nom, int forca) throws LluitadorException {
        if (!dades.afegirLluitador(nom, forca)) {
            throw  new LluitadorException(EL_LLUITADOR_JA_HI_ES + nom);
        }
    }
}
