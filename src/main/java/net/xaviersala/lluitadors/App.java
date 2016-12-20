package net.xaviersala.lluitadors;

import javax.xml.bind.JAXBException;

import net.xaviersala.lluitadors.errors.LluitadorException;
import net.xaviersala.lluitadors.vista.Lluitadors;
import net.xaviersala.lluitadors.vista.Resultat;
import spark.Spark;

import java.util.Random;

import static spark.Spark.*;

/**
 * Programa que fa de servei de lluitadors.
 *
 */
class App {

  /**
   * Volem que el resultat sigui XML.
   */
  private static final String XML = "application/xml";

  // Missatges d'error
  public static final String EXPULSATS_TOTS_ELS_LLUITADORS_DEL_CAMPIONAT = "Expulsats tots els lluitadors del campionat";
  public static final String ERROR_AL_AFEGIR_EL_LLUITADOR = "Error al afegir el lluitador";
  public static final String UN_LLUITADOR_NO_POT_LLUITAR_AMB_SI_MATEIX = "Un lluitador no pot lluitar amb si mateix";

  // URLs de la web.
  public static final String LLUITADORS_RING_LLUITA = "/Lluitadors/ring/lluita";
  public static final String LLUITADORS_RING_RESULTAT = "/Lluitadors/ring/resultat";
  public static final String LLUITADORS_RING_BUIDA = "/Lluitadors/ring/buida";
  public static final String LLUITADORS_LLISTA = "/Lluitadors/llista";
  public static final String LLUITADORS_RING_ADD = "/Lluitadors/ring/add";
  public static final String ERROR_EN_ELS_LLUITADORS = "Error en els Lluitadors";

  private static final Random aleatori = new Random();
  /**
   * Classe que fa de pont amb les dades i control·la coses.
   */
  private static final Controlador controlador = new Controlador();

  /**
   * Provabilitat.
   *
   * @param args
   * @throws JAXBException
   */
  public static void main(String[] args) throws JAXBException {

    Spark.port(8080);
    staticFiles.location("/public");

    exception(LluitadorException.class, (exception, req, res) -> {
      res.status(400);
      res.body(exception.getMessage());
    });

    // L'adreça no és vàlida...
    redirect.get("/Lluitadors", "/");

    /**
     * Llista tots els lluitadors de la base de dades.
     */
    get(LLUITADORS_LLISTA, (req, res) -> {
      res.status(200);
      res.type(XML);
      return new Lluitadors(controlador.getTotsElsLluitadors());
    }, new XMLLluitadorsTransformer());

    /**
     * Elimina tots els lluitadors del campionat.
     */
    get(LLUITADORS_RING_BUIDA, (req, res) -> {
      controlador.buida();
      return EXPULSATS_TOTS_ELS_LLUITADORS_DEL_CAMPIONAT;
    });

    /**
     * Llista UN dels lluitadors de la base de dades.
     */
    get(LLUITADORS_LLISTA + "/:nom", (req, res) -> {
      String nom = req.params(":nom");
      res.type(XML);
      res.status(200);
      return controlador.getLluitador(nom);
    }, new XMLLluitadorTransformer());

    /**
     * Afegeix UN nou lluitador a la base de dades.
     */
    post(LLUITADORS_LLISTA, (req, res) -> {
      String nom = req.queryParams("nom");
      if (nom == null || nom.isEmpty()) {
        throw new LluitadorException(ERROR_AL_AFEGIR_EL_LLUITADOR);
      }
      controlador.afegirLluitador(nom, aleatori.nextInt(10));
      res.status(200);
      res.redirect(LLUITADORS_LLISTA + "/" + nom);
      return "";
    });

    /**
     * Lluita entre dos lluitadors a través d'un enviament en POST.
     */
    post(LLUITADORS_RING_LLUITA, (req, res) -> {

      req.session(true);

      String un = req.queryParams("nom1");
      String altre = req.queryParams("nom2");

      if (un == null || altre == null) {
        throw new LluitadorException(ERROR_EN_ELS_LLUITADORS);
      }

      if (un.equals(altre)) {
        throw new LluitadorException(UN_LLUITADOR_NO_POT_LLUITAR_AMB_SI_MATEIX);
      }
      Resultat lastCombat = controlador.lluita(un, altre);
      req.session().attribute("combat", lastCombat);

      res.redirect(LLUITADORS_RING_RESULTAT);
      return "";
    });

    /**
     * Mostra el resultat del darrer combat.
     */
    get(LLUITADORS_RING_RESULTAT, (req, res) -> {
      Resultat lastCombat = req.session().attribute("combat");
      if (lastCombat != null) {
        res.status(200);
        res.type(XML);
        return lastCombat;
      } else {
        res.redirect("/");
        return "";
      }
    }, new XMLResultatTransformer());

    /**
     * Afegeix un lluitador amb la força especificada.
     */
    post(LLUITADORS_RING_ADD, (req, res) -> {
      String nom = req.queryParams("nom");
      String forca = req.queryParams("forca");
      if (nom == null || nom.isEmpty() || forca == null || forca.isEmpty()) {
        throw new LluitadorException(ERROR_AL_AFEGIR_EL_LLUITADOR);
      }
      controlador.afegirLluitador(nom, Integer.valueOf(forca));
      res.status(200);
      res.redirect(LLUITADORS_LLISTA + nom);
      return "";
    });

  }
}
