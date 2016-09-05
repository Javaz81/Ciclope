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
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.servlets.utils.DateUtils;
import static org.javasoft.ciclope.servlets.utils.DateUtils.formatAdminYearForMySQL;
import static org.javasoft.ciclope.servlets.utils.DateUtils.formatExtendedDateFromAdministrator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "SavePratica", urlPatterns = {"/SavePratica"})
public class SavePratica extends HttpServlet {

    private static String parseParam(String fromHtml) {
        if (fromHtml.equalsIgnoreCase("") || fromHtml.equalsIgnoreCase("-1")) {
            return "NULL";
        } else {
            return "'".concat(fromHtml).concat("'");
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
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Transaction t = null;
            HashMap<String, Object> result = new HashMap<String, Object>();
            try {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.getCurrentSession();
                t = s.getTransaction();
                t.begin();
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String json;
                json = br.readLine();
                Object obj = null;

                JSONParser p = new JSONParser();
                if (json != null) {
                    try {
                        obj = p.parse(json);
                    } catch (ParseException ex) {
                        Logger.getLogger(GetRifornimentiDaFare.class.getName()).log(Level.SEVERE, null, ex);
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
                String praticaId = parseParam(((JSONObject) obj).get("praticaId").toString().replace("'", "''"));
                String arrivo = parseParam(((JSONObject) obj).get("arrivo").toString().replace("'", "''"));
                String data_arrivo = parseParam(((JSONObject) obj).get("data_arrivo").toString().replace("'", "''"));
                String uscita = parseParam(((JSONObject) obj).get("uscita").toString().replace("'", "''"));
                String data_uscita = parseParam(((JSONObject) obj).get("data_uscita").toString().replace("'", "''"));
                String veicoloId = parseParam(((JSONObject) obj).get("veicoloId").toString().replace("'", "''"));
                String marca = parseParam(((JSONObject) obj).get("marca").toString().replace("'", "''"));
                String modello = parseParam(((JSONObject) obj).get("modello").toString().replace("'", "''"));
                String targa = parseParam(((JSONObject) obj).get("targa").toString().replace("'", "''"));
                String kilometraggio = parseParam(((JSONObject) obj).get("kilometraggio").toString().replace("'", "''"));
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
                String lavori_segnalati = parseParam(((JSONObject) obj).get("lavori_segnalati").toString().replace("'", "''"));

                boolean nuovaPratica = false;
                boolean clienteAssente = true;
                boolean nuovoVeicolo = false;

                if (veicoloId.equalsIgnoreCase("NULL")) {
                    // Il veicolo deve essere scelto tra quelli esistenti, o 
                    // creato tramite form dedicato.
                    t.rollback();
                    JSONObject jo = new JSONObject();
                    jo.put("result", "ko");
                    jo.put("messaggio", "Il veicolo deve essere scelto tra quelli creati."
                            + "\nCreare un nuovo veicolo o selezionarlo tra quelli esistenti.");
                    out.println(jo.toJSONString());
                    return;
                } else {
                    //salva dati veicolo
                    Query q = s.createSQLQuery("UPDATE ciclope.veicolo "
                            + "SET "
                            + "marca=" + marca + ", "
                            + "modello=" + modello + ", "
                            + "targa=" + targa + ", "
                            + "kilometraggio=" + kilometraggio + ", "
                            + "anno=" + formatAdminYearForMySQL(anno, Locale.ITALY) + ", "
                            + "tipo=" + tipo + ", "
                            + "matricola=" + matricola + ", "
                            + "ore=" + ore + " "
                            + "WHERE idVeicolo" + (veicoloId.contains("NULL") ? " IS " : " = ") + veicoloId);
                    int n_row = q.executeUpdate();
                    if (n_row != 1) {
                        t.rollback();
                        JSONObject jo = new JSONObject();
                        result.put("result", "ko");
                        result.put("messaggio", "Non è stato possibile aggiornare "
                                + "i dati del veicolo. Contattare Andrea.");
                        jo.putAll(result);
                        out.println(jo.toJSONString());
                        return;
                    }
                }
                //se il cliente c'è già salvalo sennò prova a crearlo nuovo.
                if (idCliente.equalsIgnoreCase("NULL")) {

                    clienteAssente = true;
                } else {
                    // Il cliente deve essere scelto tra quelli esistenti, o 
                    // creato tramite form dedicato.
                    clienteAssente = false;
                    Query q = s.createSQLQuery("UPDATE ciclope.cliente "
                            + "SET "
                            + "nome=" + nome + ", "
                            + "cognome=" + cognome + ", "
                            + "cellulare=" + cellulare + ", "
                            + "localita=" + localita + " "
                            + "WHERE idCliente" + (idCliente.contains("NULL") ? " IS " : " = ") + idCliente);
                    int n_row = q.executeUpdate();
                    if (n_row != 1) {
                        t.rollback();
                        JSONObject jo = new JSONObject();
                        result.put("result", "ko");
                        result.put("messaggio", "Non è stato possibile aggiornare "
                                + "i dati del cliente. Contattare Andrea.");
                        jo.putAll(result);
                        out.println(jo.toJSONString());
                        return;
                    }
                }
                //salva dati pratica
                if (praticaId.equalsIgnoreCase("-1") || praticaId.equalsIgnoreCase("") || praticaId.contains("NULL")) {
                    //se la pratica è nuova allora...
                    nuovaPratica = true;
                    Query q = s.createSQLQuery("INSERT INTO ciclope.pratica "
                            + "("
                            + "arrivo,"
                            + "data_arrivo,"
                            + "uscita,"
                            + "data_uscita,"
                            + "preventivo_lavori,"
                            + "preventivo_lavori_data,"
                            + "revisione_mctc,"
                            + "revisione_mctc_data,"
                            + "collaudo_usl,"
                            + "collaudo_usl_data,"
                            + "registro_di_controllo,"
                            + "lavori_segnalati,"
                            + "Cliente_idCliente,"
                            + "Veicolo"
                            + ")"
                            + "VALUES "
                            + "("
                            + arrivo + ","
                            + DateUtils.formatDateForAdministration(data_arrivo, Locale.ITALY) + ","
                            + uscita + ","
                            + DateUtils.formatDateForAdministration(data_uscita, Locale.ITALY) + ","
                            + preventivo_lavori + ","
                            + DateUtils.formatDateForAdministration(preventivo_lavori_data, Locale.ITALY) + ","
                            + revisione_mctc + ","
                            + DateUtils.formatDateForAdministration(revisione_mctc_data, Locale.ITALY) + ","
                            + collaudo_usl + ","
                            + DateUtils.formatDateForAdministration(collaudo_usl_data, Locale.ITALY) + ","
                            + registro_di_controllo + ","
                            + lavori_segnalati + ","
                            + idCliente + ","
                            + veicoloId
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
                } else {
                    //salva dati della pratica.
                    Query q = s.createSQLQuery("UPDATE ciclope.pratica "
                            + "SET "
                            + "arrivo=" + arrivo + ", "
                            + "data_arrivo=" + formatExtendedDateFromAdministrator(data_arrivo) + ", "
                            + "uscita=" + uscita + ", "
                            + "data_uscita=" + formatExtendedDateFromAdministrator(data_uscita) + ", "
                            + "preventivo_lavori=" + parseBooleanParam(preventivo_lavori) + ", "
                            + "preventivo_lavori_data=" + formatExtendedDateFromAdministrator(preventivo_lavori_data) + ", "
                            + "revisione_mctc=" + parseBooleanParam(revisione_mctc) + ", "
                            + "revisione_mctc_data=" + formatExtendedDateFromAdministrator(revisione_mctc_data) + ", "
                            + "collaudo_usl=" + parseBooleanParam(collaudo_usl) + ", "
                            + "collaudo_usl_data=" + formatExtendedDateFromAdministrator(collaudo_usl_data) + ", "
                            + "registro_di_controllo=" + parseBooleanParam(registro_di_controllo) + ", "
                            + "lavori_segnalati=" + lavori_segnalati + ", "
                            + "Cliente_idCliente=" + idCliente + ", "
                            + "veicolo=" + veicoloId + " "
                            + "WHERE ciclope.pratica.idPratica" + (praticaId.contains("NULL") ? " IS " : " = ") + praticaId);
                    int n_row = q.executeUpdate();
                    if (n_row != 1) {
                        t.rollback();
                        JSONObject jo = new JSONObject();
                        result.put("result", "ko");
                        result.put("messaggio", "Non è stato possibile aggiornare "
                                + "i dati del cliente. Contattare Andrea.");
                        jo.putAll(result);
                        out.println(jo.toJSONString());
                        return;
                    }
                }
                t.commit();
                JSONObject jo = new JSONObject();
                result.put("result", "ok");
                if (!(uscita.trim().equalsIgnoreCase("") || uscita.trim().equalsIgnoreCase("NULL"))
                        && !(data_uscita.trim().equalsIgnoreCase("") || data_uscita.trim().equalsIgnoreCase("NULL"))) {
                    result.put("messaggio", "Aggiornamento eseguito con successo. La pratica è considerata <b>chiusa</b>");
                } else {
                    result.put("messaggio", "Aggiornamento effettuato con successo!!!");
                }
                result.put("praticaId", praticaId);
                jo.putAll(result);
                out.println(jo.toJSONString());
            } catch (Exception ex) {
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

    private String parseBooleanParam(String param) {
        if (param.equalsIgnoreCase("NULL") || param.equalsIgnoreCase("")) {
            return "'0'";
        } else if (param.equalsIgnoreCase("SI")) {
            return "'1'";
        } else {
            return "'0'";
        }
    }

}
