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
import java.text.SimpleDateFormat;
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
@WebServlet(name = "AddPraticaLavorataOdierna", urlPatterns = {"/AddPraticaLavorataOdierna"})
public class AddPraticaLavorataOdierna extends HttpServlet {

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
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction t = s.getTransaction();
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
                jo.put("result", "ko");
                out.println(jo.toJSONString());
                return;
            }
            String praticaId = (String) ((JSONObject) obj).get("praticaId");
            String giornataJs = (String) ((JSONObject) obj).get("giornata");
            String personaleId = (String) ((JSONObject) obj).get("personale");
            String giornata =""; 
            try {
                giornata = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(giornataJs));
            } catch (java.text.ParseException ex) {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("result", "ko");
                out.println(jo.toJSONString());
                return;
            }

            //Inserisci il materiale nella pratica ed aggiorna di una quantita.
            Query q = s.createSQLQuery("INSERT INTO ciclope.orelavorate "
                    + "(ore, personale, pratica, giornata) VALUES ('0','" + personaleId + "',"
                    + " '" + praticaId + "', '" + giornata + "')");
            int n_row = q.executeUpdate();
            if (n_row != 1) {
                t.rollback();
                JSONObject jo = new JSONObject();
                jo.put("result", "ko");
                out.println(jo.toJSONString());
                return;
            }
            t.commit();

            JSONObject jo = new JSONObject();
            jo.put("result", "ok");
            out.println(jo.toJSONString());
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
    }// </editor-fold
}