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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.Veicolo;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "AddVeicolo", urlPatterns = {"/AddVeicolo"})
public class AddVeicolo extends HttpServlet {

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
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json;
            json = br.readLine();
            Object obj = null;
            JSONParser p = new JSONParser();
            json = URLDecoder.decode(json, "UTF-8");
            if (json != null) {
                try {
                    obj = p.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(EditCustomJob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String marca = (String) ((JSONObject) obj).get("marca");
            String modello = (String) ((JSONObject) obj).get("modello");
            String targa = ((String) ((JSONObject) obj).get("targa"));
            String anno = (String) ((JSONObject) obj).get("anno");
            String tipo = ((String) ((JSONObject) obj).get("tipo"));
            String matricola = (String) ((JSONObject) obj).get("matricola");
            
            //Sanitizing string
            marca = marca.replace("'", "''");
            modello = modello.replace("'", "''");
            targa = targa.replace("'", "''");
            anno = anno.replace("'", "''");
            tipo = tipo.replace("'", "''");
            matricola = matricola.replace("'", "''");
            
            Session s = SessionUtils.getCiclopeSession();
            Transaction t= s.getTransaction();
            try {
                t.begin();
                String qs = "INSERT INTO ciclope.veicolo ( marca, modello, targa, anno, tipo, matricola) "
                        + "VALUES (" + parseParam(marca) + ","
                        + parseParam(modello) + ","
                        + parseParam(targa) + ","
                        + parseParam(anno) + ","
                        + parseParam(tipo) + ","
                        + parseParam(matricola)
                        + ")";
                Query q = s.createSQLQuery(qs);
                int rows = q.executeUpdate();
                if (rows != 1) {
                    throw new Exception("Nessun lavoro aggiornato. E' già stata cancellata la riga?");
                }
                qs = "SELECT * FROM ciclope.veicolo WHERE "
                        + "marca " + parseParamForQuery(marca) + " AND "
                        + "modello " + parseParamForQuery(modello) + " AND "
                        + "targa " + parseParamForQuery(targa) + " AND "
                        + "anno " + parseParamForQuery(anno) + " AND "
                        + "tipo " + parseParamForQuery(tipo) + " AND "
                        + "matricola " + parseParamForQuery(matricola);
                
                q = s.createSQLQuery(qs).addEntity(Veicolo.class);
                ArrayList<Veicolo> veicolo = (ArrayList<Veicolo>) q.list();
                if (veicolo.size() != 1) {
                    throw new Exception("Veicolo inserito, ma il DB ha rilevato un problema nella ricerca.");
                }
                t.commit();
                HashMap<String, String> resMap = new HashMap<String, String>();
                resMap.put("result", "ok");
                StringBuilder sb = new StringBuilder();
                Veicolo v = veicolo.get(0);
                sb.append(v.getIdVeicolo())
                        .append("#")
                        .append(v.getMarca() == null ? "" : v.getMarca().replace("''", "'"))
                        .append("#")
                        .append(v.getModello() == null ? "" : v.getModello().replace("''", "'"))
                        .append("#")
                        .append(v.getTarga() == null ? "" : v.getTarga().replace("''", "'"))
                        .append("#")
                        .append(v.getAnno() == null ? "" : v.getAnno().toString().replace("''", "'"))
                        .append("#")
                        .append(v.getTipo() == null ? "" : v.getTipo().replace("''", "'"))
                        .append("#")
                        .append(v.getMatricola() == null ? "" : v.getMatricola().replace("''", "'"));
                resMap.put("veicolo", sb.toString());
                out.println(JSONObject.toJSONString(resMap));
            } catch (Exception ex) {
                Logger.getLogger(EditCustomJob.class.getName()).log(Level.SEVERE, null, ex);
                t.rollback();
                HashMap<String, String> resMap = new HashMap<String, String>();
                resMap.put("result", "ko");
                resMap.put("messaggio", ex.getMessage());
                out.println(JSONObject.toJSONString(resMap));
            }
        }
    }

    private static String parseParam(String param) {
        if (param.trim().equalsIgnoreCase("")) {
            return "NULL";
        } else {
            return "'".concat(param).concat("'");
        }
    }
    private static String parseParamForQuery(String param) {
        if (param.trim().equalsIgnoreCase("")) {
            return "IS NULL";
        } else {
            return "= '".concat(param).concat("'");
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
