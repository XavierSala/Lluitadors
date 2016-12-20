package net.xaviersala.lluitadors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.xaviersala.lluitadors.lluita.Lluitador;
import spark.ResponseTransformer;

/**
 * Converteix un objecte Lluitador en XML.
 *
 * @author xavier
 *
 */
public class XMLLluitadorTransformer implements ResponseTransformer {

  @Override
  public String render(Object model) throws JAXBException {
    final Marshaller marshaller = JAXBContext.newInstance(Lluitador.class)
        .createMarshaller();
    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(model, stringWriter);
    return stringWriter.toString();
  }

}
