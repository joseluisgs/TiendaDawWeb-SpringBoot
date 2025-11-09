package com.joseluisgs.walaspringboot.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.joseluisgs.walaspringboot.models.Purchase;
import com.joseluisgs.walaspringboot.models.Product;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Una idea de como hacer PDFS usando la librería iText
 */
public class GeneradorPDF {

    /**
     * Factura en PDF, falta mejorar el formato
     *
     * @param compra
     * @param productos
     * @param total
     * @return
     */
    public static ByteArrayInputStream factura2PDF(Purchase compra, List<Product> productos, Double total) {
        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(documento, out);
            documento.open();
            
            // HEADER EMPRESA
            documento.add(new Paragraph("🛍️ WALA MARKETPLACE", 
                FontFactory.getFont("Arial", 24, Font.BOLD, BaseColor.BLUE)));
            documento.add(new Paragraph("Marketplace de Productos Premium"));
            documento.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            documento.add(new Paragraph(" "));
            
            // INFO FACTURA
            documento.add(new Paragraph("📄 FACTURA Nº " + String.format("%06d", compra.getId()),
                FontFactory.getFont("Arial", 18, Font.BOLD, BaseColor.DARK_GRAY)));
            documento.add(new Paragraph("Fecha de emisión: " + compra.getFechaCompra().toString()));
            documento.add(new Paragraph(" "));
            
            // DATOS CLIENTE
            documento.add(new Paragraph("👤 DATOS DEL CLIENTE", 
                FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK)));
            documento.add(new Paragraph("───────────────────────────────────────────────────────────"));
            documento.add(new Paragraph("Nombre completo: " + compra.getPropietario().getNombre() 
                + " " + compra.getPropietario().getApellidos()));
            documento.add(new Paragraph("Correo electrónico: " + compra.getPropietario().getEmail()));
            documento.add(new Paragraph("Fecha de compra: " + compra.getFechaCompra()));
            documento.add(new Paragraph(" "));
            
            // TABLA PRODUCTOS
            documento.add(new Paragraph("📦 DETALLE DE PRODUCTOS", 
                FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK)));
            documento.add(new Paragraph("───────────────────────────────────────────────────────────"));
            
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 3f, 1f, 1f});
            
            // Headers con estilo
            Font headerFont = FontFactory.getFont("Arial", 11, Font.BOLD, BaseColor.WHITE);
            
            PdfPCell cell1 = new PdfPCell(new Paragraph("ITEM", headerFont));
            cell1.setBackgroundColor(BaseColor.DARK_GRAY);
            cell1.setPadding(8);
            tabla.addCell(cell1);
            
            PdfPCell cell2 = new PdfPCell(new Paragraph("PRODUCTO", headerFont));
            cell2.setBackgroundColor(BaseColor.DARK_GRAY);
            cell2.setPadding(8);
            tabla.addCell(cell2);
            
            PdfPCell cell3 = new PdfPCell(new Paragraph("CANTIDAD", headerFont));
            cell3.setBackgroundColor(BaseColor.DARK_GRAY);
            cell3.setPadding(8);
            tabla.addCell(cell3);
            
            PdfPCell cell4 = new PdfPCell(new Paragraph("PRECIO", headerFont));
            cell4.setBackgroundColor(BaseColor.DARK_GRAY);
            cell4.setPadding(8);
            tabla.addCell(cell4);
            
            // Productos
            int contador = 1;
            for (Product producto : productos) {
                tabla.addCell(String.valueOf(contador++));
                tabla.addCell(producto.getNombre());
                tabla.addCell("1");
                tabla.addCell(String.format("%.2f €", producto.getPrecio()));
            }
            
            documento.add(tabla);
            documento.add(new Paragraph(" "));
            
            // RESUMEN TOTAL
            documento.add(new Paragraph("───────────────────────────────────────────────────────────"));
            documento.add(new Paragraph("SUBTOTAL: " + String.format("%.2f €", total), 
                FontFactory.getFont("Arial", 12, Font.NORMAL, BaseColor.BLACK)));
            documento.add(new Paragraph("IVA (21%): " + String.format("%.2f €", total * 0.21), 
                FontFactory.getFont("Arial", 12, Font.NORMAL, BaseColor.BLACK)));
            documento.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            documento.add(new Paragraph("💰 TOTAL: " + String.format("%.2f €", total * 1.21),
                FontFactory.getFont("Arial", 16, Font.BOLD, BaseColor.GREEN)));
            documento.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            
            // PIE DE PÁGINA
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("🎉 ¡Gracias por confiar en Wala Marketplace!", 
                FontFactory.getFont("Arial", 12, Font.ITALIC, BaseColor.BLUE)));
            documento.add(new Paragraph("Esperamos que disfrutes de tus productos.", 
                FontFactory.getFont("Arial", 10, Font.NORMAL, BaseColor.GRAY)));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("📞 Atención al cliente: admin@waladaw.com", 
                FontFactory.getFont("Arial", 9, Font.NORMAL, BaseColor.GRAY)));
            documento.add(new Paragraph("🌐 Web: localhost:8080", 
                FontFactory.getFont("Arial", 9, Font.NORMAL, BaseColor.GRAY)));
            
            documento.close();
            
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        
        return new ByteArrayInputStream(out.toByteArray());
    }

}
