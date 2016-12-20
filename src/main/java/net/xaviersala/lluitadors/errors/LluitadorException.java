package net.xaviersala.lluitadors.errors;

/**
 * Excepció per generar errors en el servei de Lluitadors.
 *
 * @author xavier
 *
 */
public class LluitadorException extends Exception {
    /**
   *
   */
  private static final long serialVersionUID = -2251499866011952129L;

    public LluitadorException(String text) {
      super(text);
    }
}
