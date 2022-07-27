package pro1041.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class PDFHelper {
    
    private static BaseFont unicode;
    private static Font font;
    
    static {
        try {
            unicode = BaseFont.createFont("rs/ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(unicode, 12);
        } catch (DocumentException ex) {
            Logger.getLogger(PDFHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String exportOrderPDF(String direction, int billID, String employeeName, String createdTime, String hinhThucGD, String paymentMethod, List<Object[]> listOrderDetail, String total) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(direction + "\\HoaDonSo" + billID + ".pdf"));
            document.open();
            
            Paragraph title = new Paragraph("SNEAKER.BEATT", new Font(unicode, 24, Font.BOLD));
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);
            Paragraph title1 = new Paragraph("HÓA ĐƠN BÁN HÀNG", new Font(unicode, 18, Font.BOLD));
            title1.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title1);
            document.add(new Paragraph("Ngày  :  " + createdTime, font));
            document.add(new Paragraph("Thu Ngân  :  " + employeeName, font));
            document.add(new Paragraph("Hình Thức Giao Dịch  :  " + hinhThucGD, font));
            document.add(new Paragraph("Phương thức thanh toán  :  " + paymentMethod, font));
            
            document.add(PDFHelper.gererateOrderTableHeader());
            
            for (Object[] objects : listOrderDetail) {
                document.add(gererateRow(objects));
            }
            
            Paragraph totalPara = new Paragraph("Tổng Tiền  :  " + total, font);
            totalPara.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(Chunk.NEWLINE);
            document.add(totalPara);
            
            try {
                document.add(generateQRCode("ShoeStoreSys-HoaDonSo" + billID));
            } catch (WriterException ex) {
                Logger.getLogger(PDFHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            document.close();
            return direction + "\\HoaDonSo" + billID + ".pdf";
        } catch (DocumentException | FileNotFoundException e) {
            Logger.getLogger(PDFHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException ex) {
            Logger.getLogger(PDFHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static PdfPTable gererateOrderTableHeader() {
        PdfPTable table = new PdfPTable(6);
        table.addCell(gererateCell("Mã Sản Phẩm", Rectangle.BOTTOM));
        table.addCell(gererateCell("Tên Sản Phẩm", Rectangle.BOTTOM));
        table.addCell(gererateCell("Loại", Rectangle.BOTTOM));
        table.addCell(gererateCell("Số Lượng", Rectangle.BOTTOM));
        table.addCell(gererateCell("Đơn Giá", Rectangle.BOTTOM));
        table.addCell(gererateCell("Thành Tiền", Rectangle.BOTTOM));
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        return table;
    }
    
    private static PdfPTable gererateRow(Object[] obj) {
        PdfPTable table = new PdfPTable(6);
        table.addCell(gererateCell(String.valueOf(obj[0]), Rectangle.NO_BORDER));
        table.addCell(gererateCell(String.valueOf(obj[1]), Rectangle.NO_BORDER));
        table.addCell(gererateCell(obj[3] + "-" + obj[4], Rectangle.NO_BORDER));
        table.addCell(gererateCell(String.valueOf(obj[6]), Rectangle.NO_BORDER));
        table.addCell(gererateCell(MoneyFormat.format(obj[5]), Rectangle.NO_BORDER));
        table.addCell(gererateCell(MoneyFormat.format(obj[7]), Rectangle.NO_BORDER));
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        return table;
    }
    
    private static PdfPCell gererateCell(String value, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBorder(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
    
    private static Image generateQRCode(String data) throws WriterException, IOException, BadElementException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 150, 150);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        Image img = Image.getInstance(pngData);
        img.setAlignment(Image.ALIGN_CENTER);
        return img;
    }
}
