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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.servlets.utils.SessionUtils;

/**
 *
 * @author andrea
 */
@WebServlet(name = "ExportMaterialePratica", urlPatterns = {"/ExportMaterialePratica"})
public class ExportMaterialePratica extends HttpServlet {

    private static short FILE_NUMBER_SUFFIX = 0;

    private synchronized static short GetFileSuffix() {
        FILE_NUMBER_SUFFIX += 1;
        if (FILE_NUMBER_SUFFIX == Short.MIN_VALUE) {
            FILE_NUMBER_SUFFIX = 0;
        } else {
            FILE_NUMBER_SUFFIX++;
        }
        return FILE_NUMBER_SUFFIX;
    }

    ;
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

    private static synchronized File createExportFile(String fileName, List<Object[]> rows) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Materiale pratica");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        if (rows.isEmpty()) {
            data.put("1", new Object[]{"Nessun materiale associato"});
        } else {
            int i = 1;
            for (Object[] objs : rows) {
                data.put(String.valueOf(i), objs);
                i++;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEEE dd-MM-yyyy", Locale.ITALY);
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                } else if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                }
            }
        }
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
    private static List<Object[]> getDati(String praticaId) {
        Transaction t;
        try {
            Session s = SessionUtils.getCiclopeSession();
            t= s.getTransaction();
            t.begin();
            Query q = s.createSQLQuery("select"
                    + " ciclope.articolo.idArticolo as codice,\n"
                    + " ciclope.articolo.descrizione as descrizione,\n"
                    + " ciclope.materialepratica.quantita_consumata as quantita_consumata,\n"
                    + " ciclope.articolo.scorta_rimanente as rimanenza,\n"
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
                    + " order by ciclope.articolo.descrizione asc;");
            List<Object[]> aicrecs = q.list();
            t.commit();
            return aicrecs;
        } catch (Exception ex) {
            List<Object[]> l = new ArrayList<Object[]>();
            l.add(new Object[]{"Errore", ex.getMessage()});
            return l;
        }
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
        List<Object[]> dati = ExportMaterialePratica.getDati(praticaId);
        File downloadFile = ExportMaterialePratica.createExportFile(filePath, dati);
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
