/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.servlets.utils.SessionUtils;

/**
 *
 * @author andrea
 */
@WebServlet(name = "ExportMaterialePratica", urlPatterns = {"/ExportMaterialePratica"})
public class ExportMaterialePratica extends HttpServlet {

    private static short FILE_NUMBER_SUFFIX = 0;

    private static enum EXPORTED_ELEMENT {
        MATERIALE,
        ORE_LAVORATE,
        LAVORI_ESEGUITI;

        public String getTitle() {
            switch (this) {
                case MATERIALE:
                    return "MATERIALI UTILIZZATI";
                case ORE_LAVORATE:
                    return "TOTALE ORE LAVORATE";
                case LAVORI_ESEGUITI:
                    return "ELENCO LAVORI ESEGUITI";
                default:
                    return "";
            }
        }
    }

    private static XSSFCellStyle createTitleFont(XSSFWorkbook workbook) {
        //Create a new font and alter it.
        XSSFFont font = workbook.createFont();
        font.setFontHeight(15);
        font.setFontName("MYTITLE");
        font.setBold(true);
        font.setColor(HSSFColor.BLACK.index);
        //Set font into style
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private synchronized static short GetFileSuffix() {
        FILE_NUMBER_SUFFIX += 1;
        if (FILE_NUMBER_SUFFIX == Short.MIN_VALUE) {
            FILE_NUMBER_SUFFIX = 0;
        } else {
            FILE_NUMBER_SUFFIX++;
        }
        return FILE_NUMBER_SUFFIX;
    }

    private synchronized static File createTmpFile(String path, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.close();
            File f = new File(path);
            if (f.exists()) {
                return f;
            }
        } catch (IOException e) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    private static synchronized File createExportFile(String fileName,
            List<Object[]> materialeRows,
            List<Object[]> oreLavorateRows,
            List<Object[]> lavoriEseguitiRows
    ) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        //Add general style for title headers to the workbook
        XSSFCellStyle titleStyle = createTitleFont(workbook);
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Dati Pratica");
        int nextRow = 0;
        Row r = sheet.createRow(nextRow++);
        Cell c = r.createCell(0);
        c.setCellStyle(titleStyle);
        c.setCellValue(EXPORTED_ELEMENT.MATERIALE.getTitle());
        nextRow = AddElements(nextRow, titleStyle, EXPORTED_ELEMENT.MATERIALE, sheet, materialeRows);
        r = sheet.createRow(nextRow++);
        c = r.createCell(0);
        c.setCellStyle(titleStyle);
        c.setCellValue(EXPORTED_ELEMENT.LAVORI_ESEGUITI.getTitle());
        nextRow = AddElements(nextRow, titleStyle, EXPORTED_ELEMENT.LAVORI_ESEGUITI, sheet, lavoriEseguitiRows);
        r = sheet.createRow(nextRow++);
        c = r.createCell(0);
        c.setCellStyle(titleStyle);
        c.setCellValue(EXPORTED_ELEMENT.ORE_LAVORATE.getTitle());
        AddElements(nextRow, titleStyle, EXPORTED_ELEMENT.ORE_LAVORATE, sheet, oreLavorateRows);
        //adjust some known column size
        sheet.autoSizeColumn(1);
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
            File f = new File(fileName);
            if (f.exists()) {
                return f;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fornisce i dati del materiale associato alla pratica.
     *
     * @param praticaId l'Id MySQL della pratica.
     * @return I dati del materiale riguardo <param>praticaId</param>
     */
    private static List<Object[]> getDati(String praticaId, EXPORTED_ELEMENT elementType) {
        Transaction t = null;
        try {
            Session s = SessionUtils.getCiclopeSession();
            t = s.getTransaction();
            t.begin();
            Query q;
            List<Object[]> aicrecs = new ArrayList<Object[]>();
            switch (elementType) {
                case MATERIALE:
                    q = s.createSQLQuery("select"
                            + " ciclope.articolo.idArticolo as codice,\n"
                            + " ciclope.articolo.descrizione as descrizione,\n"
                            + " ciclope.materialepratica.quantita_consumata as quantita_consumata,\n"
                            + " ciclope.articolo.unita_di_misura as unita_misura\n"
                            + " from \n"
                            + " ciclope.pratica\n"
                            + " inner join\n"
                            + " ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo\n"
                            + " inner join\n"
                            + " ciclope.materialepratica on ciclope.pratica.idPratica = ciclope.materialepratica.pratica\n"
                            + " inner join\n"
                            + " ciclope.articolo on ciclope.materialepratica.articolo = ciclope.articolo.idArticolo\n"
                            + " where (ciclope.pratica.idPratica = " + praticaId + ")\n"
                            + " order by ciclope.articolo.descrizione asc");
                    aicrecs = q.list();
                    break;
                case LAVORI_ESEGUITI:
                    q = s.createSQLQuery("select ciclope.tipolavoro.descrizione\n"
                            + "from ciclope.tipolavoro\n"
                            + "inner join\n"
                            + " (\n"
                            + "select ciclope.lavoripratichestandard.tipolavoro\n"
                            + "from ciclope.lavoripratichestandard\n"
                            + "where ciclope.lavoripratichestandard.pratica = " + praticaId + "\n"
                            + ") ext on ciclope.tipolavoro.idTipoLavoro = ext.tipolavoro");
                    aicrecs = q.list();
                    q = s.createSQLQuery("SELECT descrizione \n"
                            + "FROM ciclope.lavoripratichecustom\n"
                            + "WHERE ciclope.lavoripratichecustom.pratica = " + praticaId);
                    aicrecs.addAll(q.list());
                    break;
                default:
                    q = s.createSQLQuery("SELECT sum(ore) FROM ciclope.orelavorate\n"
                            + "where pratica = " + praticaId);
                    aicrecs = q.list();
                    break;
            }
            t.commit();
            return aicrecs;
        } catch (Exception ex) {
            if (t != null) {
                t.rollback();
            }
            List<Object[]> l = new ArrayList<Object[]>();
            l.add(new Object[]{"Errore", ex.getMessage()});
            return l;
        }
    }

    /**
     * Aggiunge il materiale utilizzato nello sheet.
     *
     * @param nextRowFree l'indice zero-based della prima riga da cui scrivere.
     * @param element Il tipo di gruppo di elementi costituenti le righe.
     * @param sheet Il foglio di lavoro su cui aggiungere il contenuto.
     * @param rows Le righe dei dati.
     * @return L'indice zero-based della prima riga libera da cui poter
     * continuare a modificare <param>sheet</param>
     */
    private static int AddElements(int nextRowFree, XSSFCellStyle titleStyle, EXPORTED_ELEMENT element, XSSFSheet sheet, List<Object[]> rows) {
        //Crea l'intestazione...
        int rid = nextRowFree;
        int cid = 0;
        Row head = sheet.createRow(rid);
        Cell headCell = head.createCell(cid);
        headCell.setCellStyle(titleStyle);
        //imposta l'header
        headCell.setCellValue(element.getTitle());
        //Crea lo style di default delle celle
        XSSFCellStyle defStyle = sheet.getWorkbook().createCellStyle();
        defStyle.setAlignment(CellStyle.ALIGN_LEFT);
        //metti i valori
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEEE dd-MM-yyyy", Locale.ITALY);
        //Iterate over data and write to sheet
        try {
            for (Object[] objs : rows) {
                Row row = sheet.createRow(rid++);
                int cellnum = 0;
                if (objs != null) {
                    for (Object obj : objs) {
                        Cell cell = row.createCell(cellnum++);
                        //sets left alignement
                        cell.setCellStyle(defStyle);
                        if (obj instanceof String) {
                            cell.setCellValue((String) obj);
                        } else if (obj instanceof Float) {
                            cell.setCellValue((Float) obj);
                        } else if (obj instanceof Integer) {
                            cell.setCellValue((Integer) obj);
                        } else if (obj instanceof BigDecimal) {
                            cell.setCellValue(((BigDecimal) obj).floatValue());
                        } else if (obj instanceof Date) {
                            cell.setCellValue(sdf.format((Date) obj));
                        }
                    }
                }
            }
        } catch (ClassCastException ex) {
            for (Object obj : rows) {
                Row row = sheet.createRow(rid++);
                int cellnum = 0;
                Cell cell = row.createCell(cellnum++);
                //sets left alignement
                cell.setCellStyle(defStyle);
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Float) {
                    cell.setCellValue((Float) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                } else if (obj instanceof BigDecimal) {
                    cell.setCellValue(((BigDecimal) obj).floatValue());
                } else if (obj instanceof Date) {
                    cell.setCellValue(sdf.format((Date) obj));
                }
            }
        }
        //lascia alcune celle per spaziare i gruppi di elementi.
        sheet.createRow(rid++);
        sheet.createRow(rid++);
        return rid++;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // reads input file from an absolute path
        //Genero il suffisso unico del file
        int suffix = GetFileSuffix();
        String praticaId = request.getParameter("praticaId");
        String filePath = "MaterialePratica_".concat(praticaId).concat("_").concat(String.valueOf(suffix).concat(".xlsx"));
        List<Object[]> materiale = ExportMaterialePratica.getDati(praticaId, EXPORTED_ELEMENT.MATERIALE);
        List<Object[]> lavoriEseguiti = ExportMaterialePratica.getDati(praticaId, EXPORTED_ELEMENT.LAVORI_ESEGUITI);
        List<Object[]> oreTotaliLavorate = ExportMaterialePratica.getDati(praticaId, EXPORTED_ELEMENT.ORE_LAVORATE);

        File downloadFile = ExportMaterialePratica.createExportFile(filePath, materiale, oreTotaliLavorate, lavoriEseguiti);
        FileInputStream inStream = new FileInputStream(downloadFile);

        // if you want to use a relative path to context root:
        String relativePath = getServletContext().getRealPath("");

        // obtains ServletContext
        ServletContext context = getServletContext();

        // gets MIME type of the file
        String mimeType = context.getMimeType(filePath);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        //force MIME type to excel sheet: to do only in this case
        mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
