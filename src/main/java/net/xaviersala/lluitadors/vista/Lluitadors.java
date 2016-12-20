package net.xaviersala.lluitadors.vista;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import net.xaviersala.lluitadors.lluita.Lluitador;

/**
 * Objecte de vista pensat per convertir una llista
 * de lluitadors a XML.
 *
 * @author xavier
 *
 */
@XmlRootElement
public class Lluitadors {

  List<Lluitador> lluitador;


  public Lluitadors() {
    // Cal pel procediment
  }

  public Lluitadors(List<Lluitador> llista) {
    lluitador = llista;
  }

  /**
   * @return the lluitadors
   */
  public List<Lluitador> getLluitador() {
    return lluitador;
  }

  /**
   * @param lluitadors the lluitadors to set
   */
  public void setLluitador(List<Lluitador> lluitadors) {
    this.lluitador = lluitadors;
  }
}
