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
import java.util.List;
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
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetMaterialiRiparazione", urlPatterns = {"/GetMaterialiRiparazione"})
public class GetMaterialiRiparazione extends HttpServlet {

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
            }
            Session s = SessionUtils.getCiclopeSession();
            Transaction t = s.getTransaction();
            t.begin();
            Query q = s.createSQLQuery("select"
                    + " ciclope.articolo.idArticolo as codice,\n"
                    + " ciclope.articolo.descrizione as descrizione,\n"
                    + " ciclope.materialepratica.quantita_consumata as quantita_consumata,\n"
                    + " ciclope.articolo.scorta_rimanente as rimanenza,\n"
                    + " ciclope.articolo.unita_di_misura as unita_misura\n"
                    + " from \n"
                    + " ciclope.pratica\n"
                    + " inner join\n"
                    + " ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo\n"
                    + " inner join\n"
                    + " ciclope.materialepratica on ciclope.pratica.idPratica = ciclope.materialepratica.pratica\n"
                    + " inner join\n"
                    + " ciclope.articolo on ciclope.materialepratica.articolo = ciclope.articolo.idArticolo\n"
                    + " where (ciclope.pratica.uscita is null) and (ciclope.pratica.idPratica = " + ((JSONObject) obj).get("praticaId") + ")\n"
                    + " order by ciclope.articolo.descrizione asc;");
            List<Object[]> aicrecs = q.list();
            t.commit();
            JSONObject jo;
            JSONArray array = new JSONArray();

            String[] sq;
            for (Object[] ob : aicrecs) {
                jo = new JSONObject();
                jo.put("codice", ob[0].toString());
                jo.put("descrizione", ob[1].toString());
                jo.put("quantita_consumata",ob[2].toString());
                jo.put("rimanenza", ob[3].toString());
                jo.put("unita_misura", ob[4].toString());
                jo.put("praticaHeader",((JSONObject) obj).get("praticaHeader"));
                array.add(jo);
            }
            out.println(array.toJSONString());
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
