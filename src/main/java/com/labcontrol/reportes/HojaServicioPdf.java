package com.labcontrol.reportes;

import com.labcontrol.modelo.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
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
import java.util.List;

public class HojaServicioPdf {

    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font FUENTE_SUBTITULO = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FUENTE_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FUENTE_NEGRITA = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Color COLOR_AZUL = new Color(31, 78, 121);

    private HojaServicioPdf() {
    }

    
    public static void generar(File archivo, Mantenimiento m, Equipo equipo, Laboratorio laboratorio,
                               Usuario responsable, List<Pieza> piezas, CentroEducativo centro)
            throws IOException, DocumentException {

        Document documento = new Document(PageSize.LETTER, 36, 36, 40, 40);
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, out);
            documento.open();

            encabezado(documento, centro);
            titulo(documento, m);
            tablaServicio(documento, m, responsable);
            tablaLaboratorioEquipo(documento, laboratorio, equipo, m);
            seccionTexto(documento, "Falla reportada", texto(m.getFallaReportada()));
            seccionTexto(documento, "Diagnóstico técnico", texto(m.getDiagnostico()));
            seccionTexto(documento, "Actividades realizadas", texto(m.getActividadesRealizadas()));
            tablaResultado(documento, m);
            seccionTexto(documento, "Recomendaciones", texto(m.getRecomendaciones()));
            seccionTexto(documento, "Observaciones", texto(m.getObservaciones()));
            if (piezas != null && !piezas.isEmpty()) {
                tablaPiezas(documento, piezas);
            } else {
                documento.add(new Paragraph("Piezas reparadas o reemplazadas: no aplica "
                        + "(mantenimiento preventivo).", FUENTE_NORMAL));
            }
            firmas(documento);
            pieDePagina(documento);

            documento.close();
        }
    }


    private static void encabezado(Document documento, CentroEducativo centro) throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{70f, 30f});

        PdfPCell celdaDatos = new PdfPCell();
        celdaDatos.setBorder(PdfPCell.NO_BORDER);
        Paragraph nombre = new Paragraph(centro != null ? texto(centro.getNombre()) : "Centro educativo",
                FUENTE_SUBTITULO);
        celdaDatos.addElement(nombre);
        if (centro != null) {
            if (centro.getDireccion() != null) {
                celdaDatos.addElement(new Paragraph("Dirección: " + texto(centro.getDireccion()), FUENTE_NORMAL));
            }
            String contacto = join("  |  ", "Tel: " + texto(centro.getTelefono()),
                    centro.getCorreo() != null ? centro.getCorreo() : null);
            if (!contacto.isEmpty()) {
                celdaDatos.addElement(new Paragraph(contacto, FUENTE_NORMAL));
            }
            if (centro.getDirector() != null) {
                celdaDatos.addElement(new Paragraph("Director(a): " + texto(centro.getDirector()), FUENTE_NORMAL));
            }
        }
        tabla.addCell(celdaDatos);

        PdfPCell celdaLogo = new PdfPCell();
        celdaLogo.setBorder(PdfPCell.NO_BORDER);
        celdaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (centro != null && centro.getRutaLogo() != null && new File(centro.getRutaLogo()).isFile()) {
            try {
                Image logo = Image.getInstance(centro.getRutaLogo());
                logo.scaleToFit(90, 60);
                celdaLogo.addElement(logo);
            } catch (Exception e) {
            }
        }
        tabla.addCell(celdaLogo);
        documento.add(tabla);
        documento.add(new Paragraph(" "));
    }

    private static void titulo(Document documento, Mantenimiento m) throws DocumentException {
        Paragraph titulo = new Paragraph("HOJA DE SERVICIO DE MANTENIMIENTO", FUENTE_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        Paragraph numero = new Paragraph("Número de servicio: " + texto(m.getNumeroServicio())
                + "   -   Tipo: " + texto(m.tipoValorBd()), FUENTE_NORMAL);
        numero.setAlignment(Element.ALIGN_CENTER);
        documento.add(numero);
        documento.add(new Paragraph(" "));
    }

    private static void tablaServicio(Document documento, Mantenimiento m, Usuario responsable)
            throws DocumentException {
        PdfPTable tabla = nuevaTablaDatos();
        fila(tabla, "Número de servicio", texto(m.getNumeroServicio()));
        fila(tabla, "Tipo de mantenimiento", texto(m.tipoValorBd()));
        fila(tabla, "Estado del servicio", texto(m.getEstadoTexto()));
        fila(tabla, "Fecha de inicio", fecha(m.getFechaInicio()));
        fila(tabla, "Fecha de finalización", m.getFechaFin() != null ? fecha(m.getFechaFin()) : "En proceso");
        fila(tabla, "Responsable del mantenimiento", responsable != null
                ? responsable.getNombre() + " " + texto(responsable.getApellido()) : "Sin responsable");
        documento.add(tabla);
        documento.add(new Paragraph(" "));
    }

    private static void tablaLaboratorioEquipo(Document documento, Laboratorio laboratorio, Equipo equipo,
                                               Mantenimiento m) throws DocumentException {
        documento.add(new Paragraph("Datos del laboratorio y del equipo", FUENTE_SUBTITULO));
        documento.add(new Paragraph(" "));
        PdfPTable tabla = nuevaTablaDatos();
        if (laboratorio != null) {
            fila(tabla, "Laboratorio", laboratorio.getCodigo() + " - " + texto(laboratorio.getNombre()));
            fila(tabla, "Ubicación", texto(laboratorio.getUbicacion()));
        }
        if (equipo != null) {
            fila(tabla, "Equipo (código de inventario)", texto(equipo.getCodigoInventario()));
            fila(tabla, "Tipo / Marca / Modelo", texto(equipo.getTipo()) + "  "
                    + texto(equipo.getMarca()) + "  " + texto(equipo.getModelo()));
            fila(tabla, "Número de serie", texto(equipo.getNumSerie()));
            fila(tabla, "Estado final del equipo", texto(m.getEstadoFinalEquipo()));
        }
        documento.add(tabla);
        documento.add(new Paragraph(" "));
    }

    private static void tablaResultado(Document documento, Mantenimiento m) throws DocumentException {
        PdfPTable tabla = nuevaTablaDatos();
        fila(tabla, "Resultado del servicio", texto(m.getResultado()));
        fila(tabla, "Estado final del equipo", texto(m.getEstadoFinalEquipo()));
        documento.add(tabla);
        documento.add(new Paragraph(" "));
    }

    private static void tablaPiezas(Document documento, List<Pieza> piezas) throws DocumentException {
        documento.add(new Paragraph("Piezas reparadas o reemplazadas", FUENTE_SUBTITULO));
        documento.add(new Paragraph(" "));
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{24f, 26f, 10f, 16f, 24f});
        encabezadoTabla(tabla, "Nombre", "Descripción", "Cant.", "Acción", "Observaciones");
        for (Pieza p : piezas) {
            celda(tabla, texto(p.getNombre()), FUENTE_NORMAL);
            celda(tabla, texto(p.getDescripcion()), FUENTE_NORMAL);
            celda(tabla, String.valueOf(p.getCantidad()), FUENTE_NORMAL);
            celda(tabla, p.getAccion() != null ? p.getAccion().name() : "-", FUENTE_NORMAL);
            celda(tabla, texto(p.getObservaciones()), FUENTE_NORMAL);
        }
        documento.add(tabla);
        documento.add(new Paragraph(" "));
    }

    private static void firmas(Document documento) throws DocumentException {
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph(" "));
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{50f, 50f});
        PdfPCell firma1 = new PdfPCell(new Phrase("_______________________________\n"
                + "Firma del responsable del mantenimiento", FUENTE_NORMAL));
        firma1.setBorder(PdfPCell.NO_BORDER);
        firma1.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell firma2 = new PdfPCell(new Phrase("_______________________________\n"
                + "Recibe conforme el equipo", FUENTE_NORMAL));
        firma2.setBorder(PdfPCell.NO_BORDER);
        firma2.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(firma1);
        tabla.addCell(firma2);
        documento.add(tabla);
    }

    private static void pieDePagina(Document documento) throws DocumentException {
        documento.add(new Paragraph(" "));
        Paragraph pie = new Paragraph("Documento generado por el sistema el "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".", FUENTE_NORMAL);
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);
    }


    private static PdfPTable nuevaTablaDatos() throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{38f, 62f});
        return tabla;
    }

    private static void fila(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, FUENTE_NEGRITA));
        c1.setBackgroundColor(new Color(232, 240, 254));
        tabla.addCell(c1);
        tabla.addCell(new PdfPCell(new Phrase(valor, FUENTE_NORMAL)));
    }

    private static void encabezadoTabla(PdfPTable tabla, String... textos) {
        Font fuenteEncabezado = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(255, 255, 255));
        for (String texto : textos) {
            PdfPCell c = new PdfPCell(new Phrase(texto, fuenteEncabezado));
            c.setBackgroundColor(COLOR_AZUL);
            tabla.addCell(c);
        }
    }

    private static void celda(PdfPTable tabla, String texto, Font fuente) {
        tabla.addCell(new PdfPCell(new Phrase(texto, fuente)));
    }

    private static void seccionTexto(Document documento, String titulo, String contenido)
            throws DocumentException {
        documento.add(new Paragraph(titulo, FUENTE_SUBTITULO));
        documento.add(new Paragraph(contenido, FUENTE_NORMAL));
        documento.add(new Paragraph(" "));
    }

    private static String texto(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor.trim();
    }

    private static String fecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
    }

    private static String join(String separador, String... partes) {
        StringBuilder sb = new StringBuilder();
        for (String parte : partes) {
            if (parte != null && !parte.trim().isEmpty() && !parte.trim().equals("-")) {
                if (sb.length() > 0) {
                    sb.append(separador);
                }
                sb.append(parte.trim());
            }
        }
        return sb.toString();
    }
}
