package com.joseluisgs.walaspringboot.utilidades;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Otra forma de hacer PDFs es usando un servicio
 * Implementación del servicio.
 * Lo unico que hace es que recibe un Map de claves valor que se lo pasa a la plantilla
 */
@Service
public class Html2PdfServiceImpl implements Html2PdfService {

    @Autowired
    TemplateEngine templateEngine;

    @Override
    public InputStreamResource html2PdfGenerator(Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);
        // Dirección de la plantilla
        final String html = templateEngine.process("/app/pdf/facturapdf", context);
        final String DEST = "target/factura.pdf";

        // Aquí convertimos el HTML que hemos renderizado en base a la plantilla y parametros en PDF
        // y lo devolvemos
        try {
            HtmlConverter.convertToPdf(html, new FileOutputStream(DEST));
            return new InputStreamResource(new FileInputStream(DEST));

        } catch (IOException e) {
           System.err.println(e.toString());
            return null;
        }
    }

}
