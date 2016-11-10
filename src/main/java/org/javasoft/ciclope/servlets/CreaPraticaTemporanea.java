/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.servlets.utils.DateUtils;
import static org.javasoft.ciclope.servlets.utils.DateUtils.formatAdminYearForMySQL;
import static org.javasoft.ciclope.servlets.utils.DateUtils.formatExtendedDateFromAdministrator;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "CreaPraticaTemporanea", urlPatterns = {"/CreaPraticaTemporanea"})
public class CreaPraticaTemporanea extends HttpServlet {

    private static String parseParam(String fromHtml) {
        if (fromHtml.equalsIgnoreCase("") || fromHtml.equalsIgnoreCase("-1")) {
            return "NULL";
        } else {
            return "'".concat(fromHtml).concat("'");
        }
    }

    private String parseBooleanParam(String param) {
        if (param.equalsIgnoreCase("NULL") || param.equalsIgnoreCase("")) {
            return "'0'";
        } else if (param.equalsIgnoreCase("SI")) {
            return "'1'";
        } else {
            return "'0'";
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Transaction t = null;
            HashMap<String, Object> result = new HashMap<String, Object>();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String json;
                json = br.readLine();
                json = URLDecoder.decode(json, "UTF-8");
                Object obj = null;

                JSONParser p = new JSONParser();
                Session s = SessionUtils.getCiclopeSession();
                t = s.getTransaction();
                t.begin();
                if (json != null) {
                    try {
                        obj = p.parse(json);
                    } catch (ParseException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                        t.rollback();
                        JSONObject jo = new JSONObject();
                        result.put("result", "ko");
                        result.put("messaggio", ex.getMessage());
                        jo.putAll(result);
                        out.println(jo.toJSONString());
                        return;
                    }
                } else {
                    t.rollback();
                    JSONObject jo = new JSONObject();
                    result.put("result", "ko");
                    result.put("messaggio", "Errore tra i dati forniti...ricontrollare la pratica");
                    jo.putAll(result);
                    out.println(jo.toJSONString());
                    return;
                }
                
                // String praticaId = parseParam(((JSONObject) obj).get("praticaId").toString().replace("'", "''"));
                String arrivo = generateArrivo();
                /*
                String data_arrivo = parseParam(((JSONObject) obj).get("data_arrivo").toString().replace("'", "''"));
                String uscita = parseParam(((JSONObject) obj).get("uscita").toString().replace("'", "''"));
                String data_uscita = parseParam(((JSONObject) obj).get("data_uscita").toString().replace("'", "''"));
                String veicoloId = parseParam(((JSONObject) obj).get("veicoloId").toString().replace("'", "''"));
                String marca = parseParam(((JSONObject) obj).get("marca").toString().replace("'", "''"));
                String modello = parseParam(((JSONObject) obj).get("modello").toString().replace("'", "''"));
                String targa = parseParam(((JSONObject) obj).get("targa").toString().replace("'", "''"));
                 */
                String kilometraggio = parseParam(((JSONObject) obj).get("kilometri").toString().replace("'", "''"));
                /*
                String anno = parseParam(((JSONObject) obj).get("anno").toString().replace("'", "''"));
                String tipo = parseParam(((JSONObject) obj).get("tipo").toString().replace("'", "''"));
                String matricola = parseParam(((JSONObject) obj).get("matricola").toString().replace("'", "''"));
                String ore = parseParam(((JSONObject) obj).get("ore").toString().replace("'", "''"));
                String idCliente = parseParam(((JSONObject) obj).get("idCliente").toString().replace("'", "''"));
                String nome = parseParam(((JSONObject) obj).get("nome").toString().replace("'", "''"));
                String cognome = parseParam(((JSONObject) obj).get("cognome").toString().replace("'", "''"));
                String cellulare = parseParam(((JSONObject) obj).get("cellulare").toString().replace("'", "''"));
                String localita = parseParam(((JSONObject) obj).get("localita").toString().replace("'", "''"));
                String preventivo_lavori = parseBooleanParam(((JSONObject) obj).get("preventivo_lavori").toString().replace("'", "''"));
                String preventivo_lavori_data = parseParam(((JSONObject) obj).get("preventivo_lavori_data").toString().replace("'", "''"));
                String revisione_mctc = parseBooleanParam(((JSONObject) obj).get("revisione_mctc").toString().replace("'", "''"));
                String revisione_mctc_data = parseParam(((JSONObject) obj).get("revisione_mctc_data").toString().replace("'", "''"));
                String collaudo_usl = parseBooleanParam(((JSONObject) obj).get("collaudo_usl").toString().replace("'", "''"));
                String collaudo_usl_data = parseParam(((JSONObject) obj).get("collaudo_usl_data").toString().replace("'", "''"));
                String registro_di_controllo = parseBooleanParam(((JSONObject) obj).get("registro_di_controllo").toString().replace("'", "''"));
                 */
                String lavori_segnalati = parseParam(((JSONObject) obj).get("lavori").toString().replace("'", "''"));
                String nome_cliente = parseParam(((JSONObject) obj).get("cliente").toString().replace("'", "''"));
                String dati_veicolo = parseParam(((JSONObject) obj).get("veicolo").toString().replace("'", "''"));

                boolean nuovaPratica = false;
                boolean clienteAssente = true;
                boolean nuovoVeicolo = false;

                //salva dati pratica
                //se la pratica è nuova allora...
                nuovaPratica = true;
                Query q = s.createSQLQuery("INSERT INTO ciclope.pratica "
                        + "("
                        + "arrivo,"
                        + "data_arrivo,"
                        + "lavori_segnalati,"
                        + "Cliente_idCliente,"
                        + "Veicolo,"
                        + "ore,"
                        + "kilometraggio,"
                        + "cliente_temporaneo,"
                        + "veicolo_temporaneo"
                        + ")"
                        + "VALUES "
                        + "("
                        + arrivo + ","
                        + "CURDATE(),"
                        + lavori_segnalati + ","
                        + "1,"
                        + "1,"
                        + "0,"
                        + kilometraggio + ","
                        + nome_cliente + ","
                        + dati_veicolo
                        + ")");
                int n_row = q.executeUpdate();
                if (n_row != 1) {
                    t.rollback();
                    JSONObject jo = new JSONObject();
                    jo.put("result", "ko");
                    jo.put("messaggio", "Non è stato possibile salvare i dati della pratica");
                    out.println(jo.toJSONString());
                    return;
                }
                t.commit();
                JSONObject jo = new JSONObject();
                result.put("result", "ok");
                result.put("messaggio", "Aggiornamento effettuato con successo!!!");
                jo.putAll(result);
                out.println(jo.toJSONString());
            } catch (HibernateException | IOException ex) {
                t.rollback();
                JSONObject jo = new JSONObject();
                result.put("result", "ko");
                result.put("messaggio", "Non è stato possibile aggiornare "
                        + "i dati del cliente. Contattare Andrea.\nErrore -> "
                        + ex.getMessage());
                jo.putAll(result);
                out.println(jo.toJSONString());
                return;
            }
        }
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
    private static final SimpleDateFormat FORMATTER_ARRIVO_CODE = new SimpleDateFormat("dd-mm-yyyy,HH:MM:ss");

    private String generateArrivo() {
        return "'SOSTITUISCIMI_".concat(FORMATTER_ARRIVO_CODE.format(new Date()).concat("'"));
    }

}
