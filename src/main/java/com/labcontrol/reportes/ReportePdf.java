package com.labcontrol.reportes;

import com.labcontrol.dao.ResultadoReporte;
import com.labcontrol.modelo.CentroEducativo;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportePdf {

    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font FUENTE_NORMAL = new Font(Font.HELVETICA, 9, Font.NORMAL);
    private static final Font FUENTE_NEGRITA = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Color COLOR_AZUL = new Color(31, 78, 121);

    private ReportePdf() {
    }

    public static void exportar(File archivo, ResultadoReporte reporte, CentroEducativo centro)
            throws IOException, DocumentException {
        Document documento = new Document(PageSize.LETTER.rotate(), 30, 30, 36, 36);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, out);
            documento.open();

            Paragraph nombreCentro = new Paragraph(
                    centro != null ? centro.getNombre() : "Centro educativo", FUENTE_TITULO);
            documento.add(nombreCentro);
            Paragraph titulo = new Paragraph(reporte.getTitulo(), FUENTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(new Paragraph(" "));
            documento.add(titulo);
            documento.add(new Paragraph("Fecha de generación: "
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), FUENTE_NORMAL));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(reporte.getEncabezados().length);
            tabla.setWidthPercentage(100);
            Font fuenteEncabezado = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(255, 255, 255));
            for (String encabezado : reporte.getEncabezados()) {
                PdfPCell c = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
                c.setBackgroundColor(COLOR_AZUL);
                tabla.addCell(c);
            }
            for (Object[] fila : reporte.getFilas()) {
                for (Object valor : fila) {
                    tabla.addCell(new PdfPCell(new Phrase(
                            valor == null ? "" : String.valueOf(valor).replace("\n", " "), FUENTE_NORMAL)));
                }
            }
            documento.add(tabla);

            documento.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Registros: " + reporte.getFilas().size(), FUENTE_NEGRITA);
            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

            documento.close();
        }
    }
}
