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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.javasoft.ciclope.amministrazione.AmministrazioneUtils;
import org.javasoft.ciclope.amministrazione.AmministrazioneUtils.DayHours;
import org.javasoft.ciclope.persistence.Personale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetLastWorkedDaysStatus", urlPatterns = {"/GetLastWorkedDaysStatus"})
public class GetLastWorkedDaysStatus extends HttpServlet {

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
            String json;
            String lastDays;
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
            
            lastDays = ((JSONObject) obj).get("lastDays").toString();
            DayHours todayDh = new DayHours(new Date(), 0f);
            float todayHours = 0f;
            boolean onlyTodayNotComplete;
            JSONArray ja = new JSONArray();
            JSONObject jo;
            StringBuilder sb = new StringBuilder(65000);
            for (Personale personale : AmministrazioneUtils.GetAllOperatori()) {
                jo = new JSONObject();
                sb.setLength(0);
                todayHours = 0;
                List<DayHours> thswpd = AmministrazioneUtils.GetTotalHourWorkedPerDay(personale.getIdPersonale(), Integer.parseInt(lastDays));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
                int count = 0;
                for (DayHours d : thswpd) {
                    if (d.getHours() < 8f) {
                        count++;
                    }
                    if (d.equals(todayDh)) {
                        todayHours = d.getHours();
                    }
                }
                sb.append("<div class=\"col-lg-3 col-md-4\">").append("\n");
                //Se hai lavorato poco solo oggi allora fai solo un warning
                onlyTodayNotComplete = todayHours < 8f && count == 1;
                if (onlyTodayNotComplete) {
                    sb.append("   <div class=\"panel panel-green\">").append("\n");
                } else {
                    sb.append("    <div class=\"panel ").append(count < 1 ? "panel-green" : "panel-red").append("\">").append("\n");
                }
                sb.append("       <div class=\"panel-heading\">").append("\n");
                sb.append("           <div class=\"row\">").append("\n");
                sb.append("               <div class=\"col-xs-3\">").append("\n");
                sb.append("                   <div>").append(personale.getCognome().toUpperCase()).append("</div>").append("\n");
                sb.append("                   <i class=\"fa fa-user fa-4x\"></i>").append("\n");
                sb.append("               </div>");
                sb.append("               <div class=\"col-xs-9 text-right\">").append("\n");
                if (onlyTodayNotComplete) {
                    sb.append("                   <div class=\"huge\">").append(todayHours).append("</div>").append("\n");
                    sb.append("                   <div>ORE LAVORATE OGGI</div>").append("\n");
                } else if (count > 0) {
                    sb.append("                   <div class=\"huge\">").append(Integer.toString(count)).append("</div>").append("\n");
                    sb.append("                   <div>GIORNI NON COMPLETATI</div>").append("\n");
                } else {
                    sb.append("                   <div class=\"huge\"> OK </div>").append("\n");
                    sb.append("                   <div>TUTTO SEGNATO</div>").append("\n");
                }
                sb.append("               </div>").append("\n");
                sb.append("           </div>").append("\n");
                sb.append("       </div>").append("\n");
                sb.append("       <a href=\"#\">").append("\n");
                sb.append("           <div class=\"panel-footer\">").append("\n");
                sb.append("               <span class=\"pull-left\">Vedi solo questo utente</span>").append("\n");
                sb.append("               <span class=\"pull-right\"><i class=\"fa fa-search-plus\"></i></span>").append("\n");
                sb.append("               <div class=\"clearfix\"></div>").append("\n");
                sb.append("           </div>").append("\n");
                sb.append("       </a>").append("\n");
                sb.append("   </div>").append("\n");
                sb.append("</div>").append("\n");
                
                jo.put("panel", sb.toString());
                ja.add(jo);
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
