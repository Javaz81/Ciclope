/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.Cliente;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetClienti", urlPatterns = {"/GetClienti"})
public class GetClienti extends HttpServlet {

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
           
            Map<String, String[]> maps = null;
            if (request.getParameterMap().size() > 0) {
                maps = request.getParameterMap();
            }
            String[] columnNames = new String[]{
                "id",
                "nome",
                "cognome",
                "cellulare",
                "localita"
            };
            String orderColumn = Integer.valueOf(maps.get("order[0][column]")[0]).equals(0)
                    ? "idCliente"
                    : columnNames[Integer.valueOf(maps.get("order[0][column]")[0])];
            String orderDir = maps.get("order[0][dir]")[0];
            StringBuilder extSearch = new StringBuilder(2000);
            String extSearchLimit = maps.get("length")[0];
            String startSearchLimit = maps.get("start")[0];
            extSearch.append("");
            int i = 0;
            String colNameVal = "";
            Query q = null;
            for (Map.Entry<String, String[]> e : maps.entrySet()) {
                if (e.getKey().contains("columns[" + i + "][search][value]")) {
                    if (e.getValue()[0].equalsIgnoreCase("") || e.getValue()[0].equalsIgnoreCase("null")) {
                        extSearch.append("");
                    } else {
                        extSearch.append(i != 0 ? columnNames[i] : "idCliente")
                                .append(" LIKE '")
                                .append(e.getValue()[0])
                                .append("%'");
                        extSearch.append(" AND ");
                    }
                    i++;
                }

            }
            if (extSearch.toString().endsWith(" AND ")) {
                extSearch.replace(extSearch.length() - 5, extSearch.length(), "");
            }
            
            Session s = SessionUtils.getCiclopeSession();
            Transaction t= s.getTransaction();
            t.begin();
            q = s.createSQLQuery("SELECT * FROM ciclope.cliente "
                    + (extSearch.toString().trim().equals("")
                    ? "" : "WHERE " + extSearch.toString())
                    + "ORDER BY " + orderColumn + " " + orderDir + " "
                    + "LIMIT " + extSearchLimit + " "
                    + "OFFSET " + startSearchLimit).addEntity(Cliente.class);
            List<Cliente> aicrecs = q.list();
            t.commit();

            // Changing "draw" variable let the jquery datatables redraw itself 
            // after click on column to resorting rows, avoiding the 
            // "processing" string to stuck on html tables.
            String draw = "1";
            if (!maps.isEmpty()) {
                int dw = Integer.parseInt(maps.get("draw")[0]);
                dw += 1;
                draw = String.valueOf(dw);
            }

            JSONObject jo;
            jo = new JSONObject();
            jo.put("draw", draw);
            jo.put("recordsTotal", Integer.toString(aicrecs.size()));
            jo.put("recordsFiltered", Integer.toString(aicrecs.size()));
            JSONArray row;
            JSONArray array = new JSONArray();
            for (Cliente ob : aicrecs) {
                row = new JSONArray();
                row.add(ob.getIdCliente());
                row.add(ob.getNome());
                row.add(ob.getCognome());
                row.add(ob.getCellulare());
                row.add(ob.getLocalita());
                array.add(row);
            }
            jo.put("data", array);
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
    }// </editor-fold>

}
