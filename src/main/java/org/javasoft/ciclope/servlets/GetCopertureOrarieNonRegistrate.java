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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.javasoft.ciclope.amministrazione.AmministrazioneUtils;
import org.javasoft.ciclope.persistence.Personale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetCopertureOrarieNonRegistrate", urlPatterns = {"/GetCopertureOrarieNonRegistrate"})
public class GetCopertureOrarieNonRegistrate extends HttpServlet {

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
            String json,lastDays,idOperatore;
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
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

            idOperatore = ((JSONObject) obj).get("idOperatore").toString();
            lastDays = ((JSONObject) obj).get("lastDays").toString();
            JSONArray ja = new JSONArray();
            JSONObject jo;
            StringBuilder infoOperatore = new StringBuilder(200); 
            for (Personale personale : AmministrazioneUtils.GetAllOperatori()) {
                List<AmministrazioneUtils.DayHours> thswpd = AmministrazioneUtils.GetTotalHourWorkedPerDay(personale.getIdPersonale(), Integer.parseInt(lastDays));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                int count = 0;
                
                for (AmministrazioneUtils.DayHours d : thswpd) {
                    if (d.getHours() < 8f) {
                        jo = new JSONObject();
                        infoOperatore.append(personale.getIdPersonale().toString())
                                .append(" - ")
                                .append(personale.getNome())
                                .append(" ")
                                .append(personale.getCognome());
                        jo.put("operatore", infoOperatore.toString());
                        infoOperatore.setLength(0);
                        jo.put("day", sdf.format(d.getDay()));
                        jo.put("hours", Float.toString(d.getHours()));
                        ja.add(jo);
                    }
                }
            }
            out.println(ja.toJSONString());
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
