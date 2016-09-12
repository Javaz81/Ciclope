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
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.Cliente;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(name = "AddCliente", urlPatterns = {"/AddCliente"})
public class AddCliente extends HttpServlet {

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
                    Logger.getLogger(AddCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String nome = (String) ((JSONObject) obj).get("nome");
            String cognome = (String) ((JSONObject) obj).get("cognome");
            String cellulare = ((String) ((JSONObject) obj).get("cellulare"));
            String localita = (String) ((JSONObject) obj).get("localita");

            //Sanitizing string
            nome = nome.replace("'", "''");
            cognome = cognome.replace("'", "''");
            cellulare = cellulare.replace("'", "''");
            localita = localita.replace("'", "''");

            Session s = SessionUtils.getCiclopeSession();
            Transaction t= s.getTransaction();
            try {
                t.begin();
                String qs = "INSERT INTO ciclope.cliente ( nome, cognome, cellulare, localita) "
                        + "VALUES (" + parseParam(nome) + ","
                        + parseParam(cognome) + ","
                        + parseParam(cellulare) + ","
                        + parseParam(localita)
                        + ")";
                Query q = s.createSQLQuery(qs);
                int rows = q.executeUpdate();
                if (rows != 1) {
                    throw new Exception("Nessun cliente aggiornato. E' già stata cancellato dall'archivio?");
                }
                qs = "SELECT * FROM ciclope.cliente WHERE "
                        + "nome " + parseParamForQuery(nome) + " AND "
                        + "cognome " + parseParamForQuery(cognome) + " AND "
                        + "cellulare " + parseParamForQuery(cellulare) + " AND "
                        + "localita " + parseParamForQuery(localita);
                q = s.createSQLQuery(qs).addEntity(Cliente.class);
                ArrayList<Cliente> cliente = (ArrayList<Cliente>) q.list();
                if (cliente.size() != 1) {
                    throw new Exception("Cliente inserito, ma il DB ha rilevato un problema nella ricerca.");
                }
                t.commit();
                HashMap<String, String> resMap = new HashMap<String, String>();
                resMap.put("result", "ok");
                StringBuilder sb = new StringBuilder();
                Cliente c = cliente.get(0);
                sb.append(c.getIdCliente())
                        .append("#")
                        .append(c.getNome()== null ? "" : c.getNome().replace("''", "'"))
                        .append("#")
                        .append(c.getCognome()== null ? "" : c.getCognome().replace("''", "'"))
                        .append("#")
                        .append(c.getCellulare()== null ? "" : c.getCellulare().replace("''", "'"))
                        .append("#")
                        .append(c.getLocalita()== null ? "" : c.getLocalita().replace("''", "'"));
                resMap.put("cliente", sb.toString());
                out.println(JSONObject.toJSONString(resMap));
            } catch (Exception ex) {
                Logger.getLogger(AddCliente.class.getName()).log(Level.SEVERE, null, ex);
                t.rollback();
                HashMap<String, String> resMap = new HashMap<String, String>();
                resMap.put("result", "ko");
                resMap.put("messaggio", ex.getMessage());
                out.println(JSONObject.toJSONString(resMap));
            }
        }
    }

    private static String parseParam(String param) {
        if (param.trim().equalsIgnoreCase("")) {
            return "NULL";
        } else {
            return "'".concat(param).concat("'");
        }
    }

    private static String parseParamForQuery(String param) {
        if (param.trim().equalsIgnoreCase("")) {
            return "IS NULL";
        } else {
            return "= '".concat(param).concat("'");
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
