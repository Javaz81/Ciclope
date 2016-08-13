/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.javasoft.ciclope.servlets.utils.DateUtils;
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
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction t = s.getTransaction();
            Map<String, String[]> maps = null;
            t.begin();
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
            extSearch.append("");
            int i = 0;
            String colNameVal = "";
            for (String name : columnNames) {
                if (!maps.get("columns[" + i + "][search][value]")[0].equals("")) {
                    if (maps.get("columns[" + i + "][search][value]")[0].equals("null")) {
                        colNameVal = " IS NULL";
                    } else if (name.equalsIgnoreCase(columnNames[6])) {
                        colNameVal = " = '" + DateUtils.formatDateMySQL(maps.get("columns[" + i + "][search][value]")[0], Locale.ITALY) + "'";
                    } else {
                        colNameVal = " = '" + maps.get("columns[" + i + "][search][value]")[0] + "'";
                    }
                    extSearch.append(" AND ").append(columnNames[i].equals("data_pratica") ? "data_arrivo" : columnNames[i]).append(colNameVal);
                }
                i++;
            }
            String json = null;
            JSONObject obj = null;
            Query q = null;

            q = s.createSQLQuery("Select ciclope.categoriatipolavoro.nome as Categoria,\n"
                    + "ciclope.tipolavoro.codice as Codice,\n"
                    + "ciclope.tipolavoro. descrizione as Descrizione\n"
                    + "from\n"
                    + "ciclope.tipolavoro join ciclope.categoriatipolavoro\n"
                    + "on ciclope.tipolavoro.categoria = ciclope.categoriatipolavoro.idCategoriaTipoLavoro\n"
                    + "where ciclope.tipolavoro.idTipoLavoro\n"
                    + "not in \n"
                    + "(\n"
                    + "Select distinct\n"
                    + "ciclope.tipolavoro.idTipoLavoro\n"
                    + "from ciclope.tipolavoro join ciclope.lavoripratichestandard\n"
                    + "on  ciclope.tipolavoro.idTipoLavoro = ciclope.lavoripratichestandard.tipolavoro \n"
                    + "where\n"
                    + "ciclope.lavoripratichestandard.pratica = 1\n"
                    + ") ORDER BY "+ orderColumn + " " + orderDir + " LIMIT " + extSearchLimit );

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
                    + extSearch.toString()
                    + " ORDER BY " + orderColumn + " " + orderDir + "\n"
                    + " LIMIT " + extSearchLimit
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
            JSONObject jo = null;
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
                row.add(ob[3].toString());
                row.add(ob[4] == null ? "null" : ob[4].toString());
                row.add(ob[5].toString());
                row.add(DateUtils.isToday((Date) ob[6]) ? "Oggi" : DateUtils.formatDate((Date) ob[6], Locale.ITALY));
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
