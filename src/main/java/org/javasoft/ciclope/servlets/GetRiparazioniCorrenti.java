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
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetRiparazioniCorrenti", urlPatterns = {"/GetRiparazioniCorrenti"})
public class GetRiparazioniCorrenti extends HttpServlet {

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
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction t = s.getTransaction();
            t.begin();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json;
            json = br.readLine();
            JSONObject obj = null;
            JSONParser p = new JSONParser();
            Query q = null;
            if (json != null) {
                try {
                    obj = (JSONObject) p.parse(json);
                    q = s.createSQLQuery("select "
                            + " ciclope.pratica.idPratica as praticaId,\n"
                            + " ciclope.pratica.arrivo as arrivo,\n"
                            + " ciclope.veicolo.marca as marca,\n"
                            + " ciclope.veicolo.modello as modello,\n"
                            + " ciclope.veicolo.targa as targa,\n"
                            + " ciclope.veicolo.tipo as tipo,\n"
                            + " ciclope.pratica.data_arrivo as data_pratica\n"
                            + " from ciclope.pratica\n"
                            + " left join ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo \n"
                            + " where ciclope.pratica.uscita is null"
                            + " ORDER BY data_pratica " + (obj.get("order").equals("asc") ? "ASC" : "DESC") + "\n"
                            + " LIMIT " + obj.get("last_element_number")
                    );
                } catch (ParseException ex) {
                    Logger.getLogger(GetRifornimentiDaFare.class.getName()).log(Level.SEVERE, null, ex);
                    q = s.createSQLQuery("select "
                            + " ciclope.pratica.idPratica as praticaId,\n"
                            + " ciclope.pratica.arrivo as arrivo,\n"
                            + " ciclope.veicolo.marca as marca,\n"
                            + " ciclope.veicolo.modello as modello,\n"
                            + " ciclope.veicolo.targa as targa,\n"
                            + " ciclope.veicolo.tipo as tipo,\n"
                            + " ciclope.pratica.data_arrivo as data_pratica\n"
                            + " from ciclope.pratica\n"
                            + " left join ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo \n"
                            + " where ciclope.pratica.uscita is null"
                    );
                }
            } else {
                q = s.createSQLQuery("select "
                        + " ciclope.pratica.idPratica as praticaId,\n"
                        + " ciclope.pratica.arrivo as arrivo,\n"
                        + " ciclope.veicolo.marca as marca,\n"
                        + " ciclope.veicolo.modello as modello,\n"
                        + " ciclope.veicolo.targa as targa,\n"
                        + " ciclope.veicolo.tipo as tipo,\n"
                        + " ciclope.pratica.data_arrivo as data_pratica\n"
                        + " from ciclope.pratica\n"
                        + " left join ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo \n"
                        + " where ciclope.pratica.uscita is null"
                );
            }

            List<Object[]> aicrecs = q.list();
            t.commit();
            JSONObject jo;
            JSONArray array = new JSONArray();

            for (Object[] ob : aicrecs) {
                jo = new JSONObject();
                jo.put("praticaId", ob[0].toString());
                jo.put("arrivo", ob[1].toString());
                jo.put("marca", ob[2].toString());
                jo.put("modello", ob[3].toString());
                jo.put("targa", ob[4] == null ? "Speciale" : ob[4].toString());
                jo.put("tipo", ob[5].toString());
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
