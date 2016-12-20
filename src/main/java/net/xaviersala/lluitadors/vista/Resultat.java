package net.xaviersala.lluitadors.vista;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Resultat {
    String victoria;
    String derrota;
    String empat;

    public Resultat() {
      victoria = null;
      derrota = null;
    }

    /**
     * @return the victoria
     */
    public String getVictoria() {
      return victoria;
    }

    /**
     * @param victoria the victoria to set
     */
    public void setVictoria(String victoria) {
      this.victoria = victoria;
    }

    /**
     * @return the derrota
     */
    public String getDerrota() {
      return derrota;
    }

    /**
     * @param derrota the derrota to set
     */
    public void setDerrota(String derrota) {
      this.derrota = derrota;
    }

}
