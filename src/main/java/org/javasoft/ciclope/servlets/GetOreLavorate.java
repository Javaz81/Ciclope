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
import java.util.Calendar;
import java.util.Date;
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
import org.javasoft.ciclope.persistence.Orelavorate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetOreLavorate", urlPatterns = {"/GetOreLavorate"})
public class GetOreLavorate extends HttpServlet {

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
            Object obj = null;
            JSONParser p = new JSONParser();
            if (json != null) {
                try {
                    obj = p.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(GetRifornimentiDaFare.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            SimpleDateFormat sdfext = new SimpleDateFormat("dd/MM/yyyy"); 
            SimpleDateFormat sdfsql = new SimpleDateFormat("yyyy-MM-dd");
            Date giornata_selezionata ;
            try {
                giornata_selezionata = sdfsql.parse((String) ((JSONObject)obj).get("giornata"));
            } catch (java.text.ParseException ex) {
                Logger.getLogger(GetOreLavorate.class.getName()).log(Level.SEVERE, null, ex);
                giornata_selezionata = Calendar.getInstance().getTime();
            }
            Query q = s.createSQLQuery("select personale.Cognome, orelavorate.giornata, veicolo.matricola, veicolo.marca, veicolo.modello, ore\n" +
"from orelavorate\n" +
"inner join ciclope.pratica on orelavorate.pratica = pratica.idPratica\n" +
"inner join ciclope.veicolo on ciclope.pratica.Veicolo = veicolo.idVeicolo\n" +
"inner join ciclope.personale on personale.idPersonale = orelavorate.personale\n" +
"where pratica.idPratica = 2 AND orelavorate.personale = 1\n" +
"order by pratica.data_arrivo asc");
            List<Object[]> aicrecs = q.list();
            t.commit();
            JSONObject jo;
            JSONArray array = new JSONArray();

            String[] sq;
            for (Object[] ob : aicrecs) {
                jo = new JSONObject();
                jo.put("codice", ob[0].toString());
                jo.put("descrizione", ob[1].toString());
                sq = ((String) ob[2].toString()).split("\\.");
                jo.put("quantita_consumata",
                        ob[4].toString().equalsIgnoreCase("pz")
                        ? sq[0]
                        : ob[2].toString()
                );
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
