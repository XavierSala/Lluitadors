package net.xaviersala.lluitadors.lluita;

import net.xaviersala.lluitadors.errors.LluitadorException;
import net.xaviersala.lluitadors.vista.Resultat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Objecte per enfrontar dos lluitadors entre ells.
 *
 * @author Xavier Sala
 *
 */
public class Ring {

    public static final Logger LOG = LoggerFactory.getLogger("Ring");

    /**
     * Combat entre dos lluitadors diferents.
     * @param l1 primer lluitador
     * @param l2 segon lluitador
     * @return resultat de la lluita
     * @throws LluitadorException Hauria de ser rar, però pot ser que no existeixin.
     */
    public Resultat combat(Lluitador l1, Lluitador l2) throws LluitadorException {
        Resultat resultat = new Resultat();


        if (l2 == null || l1 == null) {
            throw new LluitadorException("Algun dels lluitadors no existeixen");
        }

        LOG.info("Combat entre " + l1.getNom() + "(" + l1.getForca() + ") vs " + l2.getNom() + "(" + l2.getForca() + ")");

        if (l1.getForca() > l2.getForca()) {
            resultat.setVictoria(l1.getNom());
            resultat.setDerrota(l2.getNom());
            l1.addVictories();
            l2.addDerrotes();
        } else {
            if (l1.getForca() < l2.getForca()) {
                resultat.setVictoria(l2.getNom());
                resultat.setDerrota(l1.getNom());
                l1.addDerrotes();
                l2.addVictories();
            } else {
                l1.addEmpats();
                l2.addEmpats();

            }
        }
        return resultat;
    }
}
