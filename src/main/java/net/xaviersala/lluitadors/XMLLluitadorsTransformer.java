package net.xaviersala.lluitadors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.xaviersala.lluitadors.vista.Lluitadors;
import spark.ResponseTransformer;

/**
 * Converteix la llista de lluitadors en XML.
 *
 * @author xavier
 *
 */
public class XMLLluitadorsTransformer implements ResponseTransformer {

      @Override
      public String render(Object model) throws JAXBException {
        final Marshaller marshaller = JAXBContext.newInstance(Lluitadors.class)
            .createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(model, stringWriter);
        return stringWriter.toString();

      }

  }
