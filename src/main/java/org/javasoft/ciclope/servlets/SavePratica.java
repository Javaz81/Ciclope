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
import static org.javasoft.ciclope.servlets.utils.DateUtils.formatAdminYearForMySQL;
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
        }
        return fromHtml;
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
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction t = s.getTransaction();
            t.begin();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json;
            json = br.readLine();
            Object obj = null;
            HashMap<String, Object> result = new HashMap<String, Object>();
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
            String praticaId = parseParam(((JSONObject) obj).get("praticaId").toString());
            String arrivo = parseParam(((JSONObject) obj).get("arrivo").toString());
            String data_arrivo = parseParam(((JSONObject) obj).get("data_arrivo").toString());
            String uscita = parseParam(((JSONObject) obj).get("uscita").toString());
            String data_uscita = parseParam(((JSONObject) obj).get("data_uscita").toString());
            String veicoloId = parseParam(((JSONObject) obj).get("veicoloId").toString());
            String marca = parseParam(((JSONObject) obj).get("marca").toString());
            String modello = parseParam(((JSONObject) obj).get("modello").toString());
            String targa = parseParam(((JSONObject) obj).get("targa").toString());
            String kilometraggio = parseParam(((JSONObject) obj).get("kilometraggio").toString());
            String anno = parseParam(((JSONObject) obj).get("anno").toString());
            String tipo = parseParam(((JSONObject) obj).get("tipo").toString());
            String matricola = parseParam(((JSONObject) obj).get("matricola").toString());
            String ore = parseParam(((JSONObject) obj).get("ore").toString());
            String idCliente = parseParam(((JSONObject) obj).get("idCliente").toString());
            String nome = parseParam(((JSONObject) obj).get("nome").toString());
            String cognome = parseParam(((JSONObject) obj).get("cognome").toString());
            String cellulare = parseParam(((JSONObject) obj).get("cellulare").toString());
            String localita = parseParam(((JSONObject) obj).get("localita").toString());
            String preventivo_lavori = parseParam(((JSONObject) obj).get("preventivo_lavori").toString());
            String preventivo_lavori_data = parseParam(((JSONObject) obj).get("preventivo_lavori_data").toString());
            String revisione_mctc = parseParam(((JSONObject) obj).get("revisione_mctc").toString());
            String revisione_mctc_data = parseParam(((JSONObject) obj).get("revisione_mctc_data").toString());
            String collaudo_usl = parseParam(((JSONObject) obj).get("collaudo_usl").toString());
            String collaudo_usl_data = parseParam(((JSONObject) obj).get("collaudo_usl_data").toString());
            String registro_di_controllo = parseParam(((JSONObject) obj).get("registro_di_controllo").toString());
            String lavori_segnalati = parseParam(((JSONObject)obj).get("lavori_segnalati").toString());

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
                        + "marca='" + marca + "', "
                        + "modello='" + modello + "', "
                        + "targa='" + targa + "', "
                        + "kilometraggio='" + kilometraggio + "', "
                        + "anno='" + formatAdminYearForMySQL(anno, Locale.ITALY) + "', "
                        + "tipo='" + tipo + "', "
                        + "matricola='" + matricola + "', "
                        + "ore='" + ore + "' "
                        + "WHERE idVeicolo='" + veicoloId + "'");
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
                        + "nome='" + nome + "', "
                        + "cognome='" + cognome + "', "
                        + "cellulare='" + cellulare + "', "
                        + "localita='" + localita + "' "
                        + "WHERE Cliente_idCliente='" + idCliente + "'");
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
            if (praticaId.equalsIgnoreCase("-1") || praticaId.equalsIgnoreCase("")) {
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
                        + "lavori_segnalati"
                        + ")"
                        + "VALUES "
                        + "("
                        + "'"+arrivo+"',"
                        + "'"+data_arrivo+"',"
                        + "'"+uscita+"',"
                        + "'"+data_uscita+"',"
                        + "'"+preventivo_lavori+"',"
                        + "'"+preventivo_lavori_data+"',"
                        + "'"+revisione_mctc+"',"
                        + "'"+revisione_mctc_data+"',"
                        + "'"+collaudo_usl+"',"
                        + "'"+collaudo_usl_data+"',"
                        + "'"+registro_di_controllo+"',"
                        + "'"+lavori_segnalati+"'"
                        + ") "
                        + "WHERE ciclope.pratica.idPratica = "+praticaId);
                int n_row = q.executeUpdate();
                if (n_row != 1) {
                    t.rollback();
                    JSONObject jo = new JSONObject();
                    jo.put("result", "ko");
                    jo.put("messaggio","Non è stato possibile salvare i dati della pratica");
                    out.println(jo.toJSONString());
                    return;
                }
                t.commit();
            } else {
                //salva dati della pratica.
                Query q = s.createSQLQuery("UPDATE ciclope.pratica "
                        + "SET "
                        + "arrivo='" + arrivo + "', "
                        + "data_arrivo='" + data_arrivo + "', "
                        + "uscita='" + uscita + "', "
                        + "data_uscita='" + data_uscita + "', "
                        + "preventivo_lavori='" + preventivo_lavori + "', "
                        + "preventivo_lavori_data='" + preventivo_lavori_data + "', "
                        + "revisione_mctc='" + revisione_mctc + "', "
                        + "revisione_mctc_data='" + revisione_mctc_data + "', "
                        + "collaudo_usl='" + collaudo_usl + "', "
                        + "collaudo_usl_data='" + collaudo_usl_data + "', "
                        + "registro_di_controllo='" + registro_di_controllo + "', "
                        + "lavori_segnalati='"+ lavori_segnalati+"' "
                        + "WHERE ciclope.pratica.idPratica='" + praticaId + "' ");
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
            result = new JSONObject();
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

}
