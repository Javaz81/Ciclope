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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
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
@WebServlet(name = "AggiornaOreLavorate", urlPatterns = {"/AggiornaOreLavorate"})
public class AggiornaOreLavorate extends HttpServlet {

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
        String oreLavorateId;
        String materialeId;
        Integer qty_upd;
        Integer qty_cur = null;
        Transaction t = null;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
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
            }
        } else {
            t.rollback();
            JSONObject jo = new JSONObject();
            jo.put("qty", Integer.toString(0));
            jo.put("result", "ko");
            try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
            return;
        }
        oreLavorateId = (String) ((JSONObject) obj).get("idOreLavorate");
        qty_upd = Integer.parseInt((String) ((JSONObject) obj).get("ore"));
        Query q = s.createSQLQuery("UPDATE ciclope.orelavorate SET ore='" + qty_upd + "' WHERE idOreLavorate='" + oreLavorateId + "'");
        try {
            int n_row = q.executeUpdate();
            if (n_row != 1) {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("qty", Integer.toString(qty_cur));
                jo.put("result", "ko");
                try (PrintWriter out = response.getWriter()) {
                    out.println(jo.toJSONString());
                }
                return;
            }
            t.commit();
            JSONObject jo = new JSONObject();
            jo.put("qty", Integer.toString(qty_upd));
            jo.put("result", "ok");
            try (PrintWriter out = response.getWriter()) {
                out.println(jo.toJSONString());
            }
        } catch (Exception ex) {
            t.rollback();
            JSONObject jo = new JSONObject();
            jo.put("qty", Integer.toString(qty_cur));
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
