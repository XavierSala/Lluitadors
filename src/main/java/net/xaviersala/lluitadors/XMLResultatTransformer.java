package net.xaviersala.lluitadors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.xaviersala.lluitadors.vista.Resultat;
import spark.ResponseTransformer;

/**
 * Converteix el resultat d'una lluita en XML.
 *
 * @author xavier
 *
 */
public class XMLResultatTransformer implements ResponseTransformer {

      @Override
      public String render(Object model) throws JAXBException {
        final Marshaller marshaller = JAXBContext.newInstance(Resultat.class)
            .createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(model, stringWriter);
        return stringWriter.toString();

      }

  }
