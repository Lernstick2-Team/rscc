package ch.imedias.rsccfx.model.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class of Supporter, enables to use List data types of Supporter with JAXB.
 */
@XmlRootElement(name = "supporters")
@XmlAccessorType(XmlAccessType.FIELD)
public class Supporters {

  @XmlElement(name = "supporter")
  private List<Supporter> supporters = null;

  public List<Supporter> getSupporters() {
    return supporters;
  }

  public void setSupporters(List<Supporter> supporters) {
    this.supporters = supporters;
  }
}
