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
import org.javasoft.ciclope.persistence.Cliente;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.persistence.Veicolo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetClienteInfo", urlPatterns = {"/GetClienteInfo"})
public class GetClienteInfo extends HttpServlet {

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
        Transaction t = null;
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
            String cid = (String) ((JSONObject)obj).get("cid");         
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            t = s.getTransaction();
            try {
                t.begin();
                String qs = "SELECT * from ciclope.cliente WHERE idCliente='"+cid+"'";
                Query q = s.createSQLQuery(qs).addEntity(Cliente.class);
                ArrayList<Cliente> rows = (ArrayList<Cliente>) q.list();
                if(rows.size()!= 1){
                    throw new Exception("Nessun cliente trovato. Riprovare o contattare Andrea.");
                }
                t.commit();
                HashMap<String,String> resMap = new HashMap<String, String>();
                resMap.put("result", "ok");
                StringBuilder sb = new StringBuilder();
                Cliente c = rows.get(0);
                sb.append(c.getIdCliente())
                        .append("#")
                        .append(c.getNome() == null?"":c.getNome())
                        .append("#")
                        .append(c.getCognome() == null?"":c.getCognome())
                        .append("#")
                        .append(c.getCellulare() == null?"":c.getCellulare())
                        .append("#")
                        .append(c.getLocalita() == null ? "":c.getLocalita());
                resMap.put("row", sb.toString());
                out.println(JSONObject.toJSONString(resMap));
            } catch (Exception ex) {
                Logger.getLogger(EditCustomJob.class.getName()).log(Level.SEVERE, null, ex);
                t.rollback();
                HashMap<String,String> resMap = new HashMap<String, String>();
                resMap.put("result", "ko");
                resMap.put("descrizione", ex.getMessage());
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
