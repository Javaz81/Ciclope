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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "SavePratica", urlPatterns = {"/SavePratica"})
public class SavePratica extends HttpServlet {

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
            HashMap<String,Object> result = new HashMap<String, Object>();
            JSONParser p = new JSONParser();
            if (json != null) {
                try {
                    obj = p.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(GetRifornimentiDaFare.class.getName()).log(Level.SEVERE, null, ex);
                    result.put("result", "ko");
                    result.put("messaggio", ex.getMessage());
                    t.rollback();
                    return;
                }
            } else {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("result", "ko");
                jo.put("messaggio","Errore tra i dati forniti...ricontrollare la pratica");
                out.println(jo.toJSONString());
                return;
            }
            String praticaId = ((JSONObject) obj).get("praticaId").toString();
            String arrivo = ((JSONObject) obj).get("arrivo").toString();
            String data_arrivo = ((JSONObject) obj).get("data_arrivo").toString();
            String idChiusuraPratica = ((JSONObject) obj).get("idChiusuraPratica").toString();
            String uscita = ((JSONObject) obj).get("uscita").toString();
            String data_uscita = ((JSONObject) obj).get("data_uscita").toString();
            String veicoloId = ((JSONObject) obj).get("veicoloId").toString();
            String marca = ((JSONObject) obj).get("marca").toString();
            String modello = ((JSONObject) obj).get("modello").toString();
            String targa = ((JSONObject) obj).get("targa").toString();
            String kilometraggio = ((JSONObject) obj).get("kilometraggio").toString();
            String anno = ((JSONObject) obj).get("anno").toString();
            String tipo = ((JSONObject) obj).get("tipo").toString();
            String matricola = ((JSONObject) obj).get("matricola").toString();
            String ore = ((JSONObject) obj).get("ore").toString();
            String idCliente = ((JSONObject) obj).get("idCliente").toString();
            String nome = ((JSONObject) obj).get("nome").toString();
            String cognome = ((JSONObject) obj).get("cognome").toString();
            String cellulare = ((JSONObject) obj).get("cellulare").toString();
            String localita = ((JSONObject) obj).get("localita").toString();
            String preventivo_lavori = ((JSONObject) obj).get("preventivo_lavori").toString();
            String preventivo_lavori_data = ((JSONObject) obj).get("preventivo_lavori_data").toString();
            String revisione_mctc = ((JSONObject) obj).get("revisione_mctc").toString();
            String revisione_mctc_data = ((JSONObject) obj).get("revisione_mctc_data").toString();
            String collaudo_usl = ((JSONObject) obj).get("collaudo_usl").toString();
            String collaudo_usl_data = ((JSONObject) obj).get("collaudo_usl_data").toString();
            String registro_di_controllo = ((JSONObject) obj).get("registro_di_controllo").toString();
            
            boolean nuovaPratica = false;
            boolean nuovoCliente = false;
            //salva dati pratica
            if(praticaId.equalsIgnoreCase("-1")){
                //se la pratica è nuova allora...
                nuovaPratica = true;
            }
            if(veicoloId.equalsIgnoreCase("-1") || veicoloId.equalsIgnoreCase("")){
                JSONObject jo = new JSONObject();
                jo.put("result", "ko");
                jo.put("messaggio","Crea un nuovo veicolo/piattaforma o "
                        + "sceglilo tra quelli esistenti.");
                out.println(jo.toJSONString());
                return;
            }else{
                //salva dati cliente.
            }
            //salva dati cliente
            if(idCliente.equalsIgnoreCase("") || idCliente.equalsIgnoreCase("-1")){
                //crea i dati del cliente
                
            }else{
                //salva dati cliente
            }
            //salva dati veicolo - il veicolo è o registrato o viene creato al momento.
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
