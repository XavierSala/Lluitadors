package net.xaviersala.lluitadors.lluita;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Objecte bàsic que representa un dels lluitadors
 * del servei de Lluita.
 *
 * @author xavier
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Lluitador {
  @XmlElement
  String nom;
  @XmlTransient
  int forca;
  @XmlElement
  int victories;
  @XmlElement
  int derrotes;
  @XmlElement
  int empats;

  public Lluitador() {
    // Fa falta.
  }
  public Lluitador(String nom, int forca) {
    this.nom = nom;
    this.forca = forca;
  }

  /**
   * @return the nom
   */
  public String getNom() {
    return nom;
  }

  /**
   * @return the forca
   */
  public int getForca() {
    return forca;
  }

  /**
   * @return the victories
   */
  public int getVictories() {
    return victories;
  }
  /**
   * @param victories the victories to set
   */
  public void setVictories(int victories) {
    this.victories = victories;
  }
  /**
   * @return the derrotes
   */
  public int getDerrotes() {
    return derrotes;
  }
  /**
   * @param derrotes the derrotes to set
   */
  public void setDerrotes(int derrotes) {
    this.derrotes = derrotes;
  }
  /**
   * @return the empats
   */
  public int getEmpats() {
    return empats;
  }
  /**
   * @param empats the empats to set
   */
  public void setEmpats(int empats) {
    this.empats = empats;
  }

  /**
   * Afegeix una victoria.
   */
  public void addVictories() {
    victories++;
  }

  /**
   * Afegeix una derrota.
   */
  public void addDerrotes() {
    derrotes++;
  }

  /**
   * Afegeix un empat.
   */
  public void addEmpats() {
    empats++;
  }

   /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return nom;
  }


}
