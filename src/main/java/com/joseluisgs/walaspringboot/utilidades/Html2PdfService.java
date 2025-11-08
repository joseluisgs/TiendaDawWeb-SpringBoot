package com.joseluisgs.walaspringboot.utilidades;

import org.springframework.core.io.InputStreamResource;

import java.util.Map;

/**
 * Otra opción de hacer los PDFs es a través de un servicio
 * Definimos su interfaz
 */
public interface Html2PdfService {

    /**
     * @param data {@link Map}
     * @return a stream {@link InputStreamResource}
     */
    InputStreamResource html2PdfGenerator(Map<String, Object> data);

}
