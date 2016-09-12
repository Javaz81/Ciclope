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
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author andrea
 */
@WebServlet(name = "GetPraticheAperteDataTables", urlPatterns = {"/GetPraticheAperteDataTables"})
public class GetPraticheAperteDataTables extends HttpServlet {

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
                "idPratica",
                "arrivo",
                "nome",
                "cognome",
                "marca",
                "targa",
                "matricola",
                "data_pratica"
            };
            String orderColumn = null;
            String orderDir = maps.get("order[0][dir]")[0];
            int coln = Integer.valueOf(maps.get("order[0][column]")[0]);
            if (coln < 2)
               orderColumn = columnNames[coln];
            else if( coln == 2)
                orderColumn = columnNames[2].concat(" ").concat(orderDir).concat(",")
                        .concat(columnNames[3]);
            else if ( coln >= 3)
                orderColumn = columnNames[coln++];
           
            StringBuilder extSearch = new StringBuilder(2000);
            String startSearchLimit = maps.get("start")[0];
            String extSearchLimit = maps.get("length")[0];
            extSearch.append("");
            int i = 0;
            int schColIdx = 0;
            String colNameVal = "";
            for(String name: columnNames){
                if( name.equals("cognome")){
                    i++;
                    continue;
                }
                if(!maps.get("columns["+schColIdx+"][search][value]")[0].equals("")){
                    if(maps.get("columns["+schColIdx+"][search][value]")[0].equals("null")){
                        colNameVal = " IS NULL";
                    }else{
                        if(name.equalsIgnoreCase(columnNames[7])){                            
                            colNameVal = " = '"+DateUtils.formatDateMySQL(maps.get("columns["+schColIdx+"][search][value]")[0], Locale.ITALY)+"'";
                        }else{
                            colNameVal = " = '"+maps.get("columns["+schColIdx+"][search][value]")[0]+"'";
                        }
                    }
                    if(columnNames[i].equals("nome")){
                        extSearch.append(" AND (").append(columnNames[i]).append(colNameVal).append(" OR ")
                                .append(columnNames[i+1]).append(colNameVal).append(")");
                    }
                    else{
                        extSearch.append(" AND ").append(columnNames[i].equals("data_pratica")?"data_arrivo":columnNames[i]).append(colNameVal);
                    }
                }
                i++;
                schColIdx++;
            }
            String json = null;
            JSONObject obj = null;
            Query q = null;
            
            //elenca le pratiche chiuse?
            boolean pc;
            if(maps.get("praticaMode")[0].equalsIgnoreCase(""))
                pc = false; // solo pratiche aperte di default
            else
                pc =!maps.get("praticaMode")[0].equalsIgnoreCase("false");
            Session s = SessionUtils.getCiclopeSession();
            Transaction t = s.getTransaction();
            t.begin();
            q = s.createSQLQuery("select "
                    + " ciclope.pratica.idPratica as praticaId,\n"
                    + " ciclope.pratica.arrivo as arrivo,\n"
                    + " ciclope.cliente.nome as nome,\n"
                    + " ciclope.cliente.cognome as cognome,\n"
                    + " ciclope.veicolo.marca as marca,\n"
                    + " ciclope.veicolo.matricola as matricola,\n"
                    + " ciclope.veicolo.targa as targa,\n"
                    + " ciclope.pratica.data_arrivo as data_pratica\n"
                    + " from ciclope.pratica\n"
                    + " left join ciclope.veicolo on ciclope.pratica.Veicolo = ciclope.veicolo.idVeicolo \n"
                    + " left join ciclope.cliente on ciclope.pratica.Cliente_IdCliente = ciclope.cliente.idCliente\n"
                    + " where (ciclope.pratica.uscita is "+(pc?"not null":"null")+" OR ciclope.pratica.data_uscita is "+(pc?"not null":"null")+")"
                    + extSearch.toString()
                    + " ORDER BY " + orderColumn + " " + orderDir + "\n"
                    + " LIMIT "+extSearchLimit+" OFFSET "+ startSearchLimit
            );

            List<Object[]> aicrecs = q.list();
            t.commit();
            JSONObject jo = null;
            JSONObject jo1 = new JSONObject();
            JSONArray row = null;
            JSONArray arraytop = new JSONArray();
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
            jo1.put("draw", draw);
            jo1.put("recordsTotal", Integer.toString(aicrecs.size()));
            jo1.put("recordsFiltered", Integer.toString(aicrecs.size()));
            for (Object[] ob : aicrecs) {
                row = new JSONArray();
                for(int idx = 0; idx < ob.length; idx++){
                    if(ob[idx] == null)
                        ob[idx]="";
                }
                
                row.add(ob[0].toString());
                row.add(ob[1].toString());
                if(ob[3].equals(""))                    
                    row.add(ob[2].toString());
                else
                    row.add(ob[3].toString());
                row.add(ob[4].toString());
                row.add(ob[5].toString());
                row.add(ob[6].toString());
                row.add(DateUtils.isToday((Date) ob[7]) ? "Oggi" : DateUtils.formatDate((Date) ob[7], Locale.ITALY));
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
