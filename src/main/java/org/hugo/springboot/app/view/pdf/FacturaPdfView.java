package org.hugo.springboot.app.view.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.springboot.app.models.entity.Factura;
import org.hugo.springboot.app.models.entity.ItemFactura;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import java.awt.*;
import java.util.Map;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Factura factura = (Factura) model.get("factura");
        PdfPTable tabla = new PdfPTable(1);
        tabla.setSpacingAfter(20);
        PdfPCell cellTituloT1 = new PdfPCell(new Phrase("Datos del cliente"));
        cellTituloT1.setBackgroundColor(new Color(184,218,255));
        cellTituloT1.setPadding(8f);
        tabla.addCell(cellTituloT1);
        tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellidos());
        tabla.addCell(factura.getCliente().getEmail());

        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingAfter(20);
        PdfPCell cellTituloT2 = new PdfPCell(new Phrase("Datos de la factura"));
        cellTituloT2.setBackgroundColor(new Color(195,230,203));
        cellTituloT2.setPadding(8f);
        tabla2.addCell(cellTituloT2);
        tabla2.addCell("Folio: " + factura.getId());
        tabla2.addCell("Descripcion: " + factura.getDescripcion());
        tabla2.addCell("Fecha: " + factura.getCreateAt());

        PdfPTable tabla3 = new PdfPTable(4);
        tabla3.setWidths(new float[] {3.5f,1,1,1});
        tabla3.addCell("Producto: ");
        tabla3.addCell("Precio: ");
        tabla3.addCell("Cantidad: ");
        tabla3.addCell("Total: ");
        for(ItemFactura item: factura.getItems()){
            tabla3.addCell(item.getProducto().getNombre());
            tabla3.addCell(item.getProducto().getPrecio().toString());
            PdfPCell cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            tabla3.addCell(cell);
            tabla3.addCell(item.calcularImporte().toString());
        }
        PdfPCell cell = new PdfPCell(new Phrase("Total"));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla3.addCell(cell);
        tabla3.addCell(factura.getTotal().toString());

        document.add(tabla);
        document.add(tabla2);
        document.add(tabla3);
    }
}
