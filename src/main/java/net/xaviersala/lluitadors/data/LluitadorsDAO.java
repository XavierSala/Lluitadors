package net.xaviersala.lluitadors.data;

import java.util.List;

import net.xaviersala.lluitadors.errors.LluitadorException;
import net.xaviersala.lluitadors.lluita.Lluitador;

/**
 * Font de dades dels Lluitadors.
 *
 * @author xavier
 *
 */
public interface LluitadorsDAO {
  /***
   * Obtenir tots els lluitadors del campionat
   * @return
   */
    List<Lluitador> getLluitadors();
    /**
     * Obtenir el lluitador que té aquest nom.
     * @param nom nom del lluitador
     * @return objecte
     */
    Lluitador getLluitador(String nom);

  /**
   * Afegeix un lluitador a la base de dades amb una força determinada.
   *
   * @param nom Nom del lluitador
   * @param forca Força del lluitador
   * @return Si s'ha afegit o no
   */
    boolean afegirLluitador(String nom, int forca) throws  LluitadorException;

  /**
   * Elimina els lluitadors del ring.
   */
  void buida();
}
