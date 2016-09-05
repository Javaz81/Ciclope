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
@WebServlet(name = "AddCustomJob", urlPatterns = {"/AddCustomJob"})
public class AddCustomJob extends HttpServlet {

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
            if (json != null) {
                try {
                    obj = p.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(EditCustomJob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String praticaId = (String) ((JSONObject)obj).get("praticaId");
            String categoriaId = (String) ((JSONObject)obj).get("categoriaId");
            String descrizione = ((String)((JSONObject)obj).get("descrizione"));            
            
            //Sanitizes string for single quote char.
            descrizione = descrizione.replace("'", "''");
            
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.getCurrentSession();
            Transaction t = s.getTransaction();
            try {
                t.begin();
                String qs = "INSERT INTO ciclope.lavoripratichecustom ( pratica, categoria, descrizione) VALUES ('"+praticaId+"','"+categoriaId+"','"+descrizione+"')";
                Query q = s.createSQLQuery(qs);
                int rows = q.executeUpdate();
                if(rows != 1){
                    throw new Exception("Nessun lavoro aggiornato. E' già stata cancellata la riga?");
                }
                qs = "SELECT id FROM ciclope.lavoripratichecustom WHERE pratica='"+praticaId+"' AND categoria = '"+categoriaId+"' AND descrizione = '"+descrizione+"'";
                q = s.createSQLQuery(qs);
                ArrayList<Integer> ids = (ArrayList<Integer>) q.list();
                if(ids.size()!=1){
                    throw new Exception("Lavoro inserito, ma il DB ha rilevato un problema nella ricerca.");
                }
                t.commit();
                HashMap<String,String> resMap = new HashMap<String, String>();
                resMap.put("result", "ok");
                resMap.put("jobId", ids.get(0).toString());
                resMap.put("descrizione",descrizione.replace("''", "'"));
                out.println(JSONObject.toJSONString(resMap));
            } catch (Exception ex) {
                Logger.getLogger(EditCustomJob.class.getName()).log(Level.SEVERE, null, ex);
                t.rollback();
                HashMap<String,String> resMap = new HashMap<String, String>();
                resMap.put("result", "ko");
                resMap.put("messaggio", ex.getMessage());
                out.println(JSONObject.toJSONString(resMap));
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
