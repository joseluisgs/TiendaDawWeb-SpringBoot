package com.joseluisgs.walaspringboot.utils;

import com.itextpdf.html2pdf.HtmlConverter;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

@Service
public class Html2PdfServiceImpl implements Html2PdfService {

    @Autowired
    PebbleEngine pebbleEngine;

    @Override
    public InputStreamResource html2PdfGenerator(Map<String, Object> data) {
        try {
            PebbleTemplate compiledTemplate = pebbleEngine.getTemplate("app/pdf/facturapdf.peb.html");
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, data);
            final String html = writer.toString();
            final String DEST = "target/factura.pdf";

            HtmlConverter.convertToPdf(html, new FileOutputStream(DEST));
            return new InputStreamResource(new FileInputStream(DEST));

        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

}
