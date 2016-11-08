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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
public class UpdateQuantitaMateriale extends HttpServlet {

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
        boolean exception = false;
        String praticaId;
        String materialeId;
        Float qty_upd;
        Float qty_cur = null;
        Transaction t = null;
        
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
            }
        } else {
            JSONObject jo = new JSONObject();
            jo.put("qty", Float.toString(0));
            jo.put("result", "ko");
            try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
            return;
        }
        praticaId = (String) ((JSONObject) obj).get("praticaId");
        materialeId = (String) ((JSONObject) obj).get("materialeId");
        qty_upd = Float.parseFloat((String) ((JSONObject) obj).get("quantita_da_aggiornare"));
        qty_cur = Float.parseFloat((String) ((JSONObject) obj).get("quantita_corrente"));
        
        Session s = SessionUtils.getCiclopeSession();
        t = s.getTransaction();
        t.begin();        
        Query q = s.createSQLQuery("UPDATE ciclope.materialepratica "
                + "SET quantita_consumata = quantita_consumata+'" + qty_upd + "' "
                + "WHERE pratica='" + praticaId + "' and articolo = '" + materialeId + "'");
        try {
            int n_row = q.executeUpdate();
            if (n_row != 1) {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("qty", Float.toString(qty_cur));
                jo.put("result", "ko");
                 try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
                return;
            }
            q = s.createSQLQuery(" UPDATE `ciclope`.`articolo` SET `scorta_rimanente`=scorta_rimanente-'" + qty_upd + "' WHERE `idArticolo`='" + materialeId + "'");
            n_row = q.executeUpdate();
            if (n_row != 1) {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("qty", Float.toString(qty_cur));
                jo.put("result", "ko");
                 try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
                return;
            }
            t.commit();
            JSONObject jo = new JSONObject();
            Float res = qty_cur + qty_upd;
            jo.put("qty", Float.toString(res));            
            jo.put("result", "ok");
             try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
        } catch (Exception ex) {
            t.rollback();
            JSONObject jo = new JSONObject();
            jo.put("qty", Float.toString(qty_cur));
            jo.put("result", "ko");
            jo.put("message", ex.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
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

}
