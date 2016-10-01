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
import java.util.Map.Entry;
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

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetStandardJobsDataTable", urlPatterns = {"/GetStandardJobsDataTable"})
public class GetStandardJobsDataTable extends HttpServlet {

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
            Map<String, String[]> maps = null;
            if (request.getParameterMap().size() > 0) {
                maps = request.getParameterMap();
            }
            //TODO:we have to see if user has choosed a column sorting
            // or text searching... and update the MYSQL DB query String 
            // according to.

            String[] columnNames = new String[]{
                "Categoria",
                "Codice",
                "Descrizione",};
            String orderColumn = columnNames[Integer.valueOf(maps.get("order[0][column]")[0])];
            String orderDir = maps.get("order[0][dir]")[0];
            StringBuilder extSearch = new StringBuilder(2000);
            String extSearchLimit = maps.get("length")[0];
            String startSearchLimit = maps.get("start")[0];
            String praticaId = maps.get("praticaId")[0];
            String categoriaId = maps.get("categoriaId")[0];
            extSearch.append("");
            int i = 0;
            String colNameVal = "";
            String json = null;
            JSONObject obj = null;
            Query q = null;
            for(Entry<String,String[]> e : maps.entrySet()){
                if(e.getKey().contains("columns["+i+"][search][value]")){
                    if(e.getValue()[0].equalsIgnoreCase("") || e.getValue()[0].equalsIgnoreCase("null"))
                        extSearch.append("");
                    else{                        
                        extSearch.append(" and ").
                                append(i!=0?columnNames[i]:"nome").
                                append(" = ").
                                append("\"").
                                append(e.getValue()[0]).
                                append("\"");
                    }
                    i++;
                }
            }
            Session s = SessionUtils.getCiclopeSession();
            Transaction t = s.getTransaction();
            t.begin();
            q = s.createSQLQuery("Select "
                    + "ciclope.categoriatipolavoro.nome as Categoria,\n"
                    + "ciclope.tipolavoro.codice as Codice,\n"
                    + "ciclope.tipolavoro. descrizione as Descrizione\n"
                    + "from\n"
                    + "ciclope.tipolavoro join ciclope.categoriatipolavoro\n"
                    + "on ciclope.tipolavoro.categoria = ciclope.categoriatipolavoro.idCategoriaTipoLavoro\n"
                    + "where ciclope.tipolavoro.categoria = "+categoriaId+" and \n"
                    + "ciclope.tipolavoro.idTipoLavoro\n"
                    + "not in \n"
                    + "(\n"
                    + "Select distinct\n"
                    + "ciclope.tipolavoro.idTipoLavoro\n"
                    + "from ciclope.tipolavoro join ciclope.lavoripratichestandard\n"
                    + "on  ciclope.tipolavoro.idTipoLavoro = ciclope.lavoripratichestandard.tipolavoro \n"
                    + (praticaId.contains("-1")?"":"where ciclope.lavoripratichestandard.pratica = "+ praticaId+"\n")
                    + ")\n"
                    + extSearch
                    + " ORDER BY "+ orderColumn + " " + orderDir + " LIMIT " + extSearchLimit + " OFFSET " + startSearchLimit 
            );

            List<Object[]> aicrecs = q.list();
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

            //Finally we build the datatables format response.
            JSONObject jo1 = new JSONObject();
            JSONArray row;
            JSONArray arraytop = new JSONArray();
            jo1.put("draw", draw);
            jo1.put("recordsTotal", Integer.toString(aicrecs.size()));
            jo1.put("recordsFiltered", Integer.toString(aicrecs.size()));
            for (Object[] ob : aicrecs) {
                row = new JSONArray();
                row.add(ob[0].toString());
                row.add(ob[1].toString());
                row.add(ob[2].toString());
                arraytop.add(row);
            }
            jo1.put("data", arraytop);
            out.println(jo1.toJSONString());
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
