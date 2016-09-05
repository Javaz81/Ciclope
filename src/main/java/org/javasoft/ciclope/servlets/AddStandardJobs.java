package org.javasoft.ciclope.servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.servlets.exceptions.UnexceptedResultException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andrea
 */
@WebServlet(urlPatterns = {"/AddStandardJobs"})
public class AddStandardJobs extends HttpServlet {

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
        Transaction t = null;
        try (PrintWriter out = response.getWriter()) {
            try {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.getCurrentSession();
                t = s.getTransaction();
                t.begin();
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String json;
                json = br.readLine();
                JSONObject obj = null;
                JSONParser p = new JSONParser();
                if (json != null) {
                    try {
                        obj = (JSONObject)p.parse(json);
                    } catch (ParseException ex) {
                        Logger.getLogger(GetRifornimentiDaFare.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                JSONArray jobCodes;
                String praticaId;

                jobCodes = (JSONArray) (obj.get("codes"));
                praticaId = obj.get("praticaId").toString();
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT idTipoLavoro FROM ciclope.tipolavoro WHERE ciclope.tipolavoro.codice IN (");
                for(Object job : jobCodes){
                    sb.append("\"").append(job.toString()).append("\",");
                }
                sb.replace(sb.lastIndexOf(","), sb.length(),")");
                Query q = s.createSQLQuery(sb.toString());
                ArrayList<Integer> arr = (ArrayList<Integer>) q.list();
                int rows;
                int i = 0;
                for (Object ob : jobCodes) {
                    q = s.createSQLQuery("INSERT INTO ciclope.lavoripratichestandard (pratica, tipolavoro) VALUES (:praticaId , :job)");
                    q.setInteger("praticaId", Integer.parseUnsignedInt(praticaId));
                    q.setInteger("job", arr.get(i));
                    rows = q.executeUpdate();
                    if(rows != 1 )
                        throw new UnexceptedResultException("Una o più righe non sono state inserite");
                    i+=1;
                }
                StringBuilder qs =new StringBuilder("SELECT ciclope.lavoripratichestandard.id, tipolavoro.descrizione, ciclope.tipolavoro.codice\n" +
"FROM ciclope.lavoripratichestandard INNER JOIN ciclope.tipoLavoro\n" +
"ON ciclope.lavoripratichestandard.tipolavoro = ciclope.tipolavoro.idTipoLavoro\n" +
"WHERE ciclope.lavoripratichestandard.pratica = "+praticaId+" AND ciclope.tipolavoro.codice IN (");
                for (Object ob : jobCodes) {
                    qs.append("\"").append(ob.toString()).append("\",");
                }
                qs.replace(qs.lastIndexOf(","), qs.length(), " ");
                qs.append(") ORDER BY ciclope.lavoripratichestandard.id");
                q = s.createSQLQuery(qs.toString());
                ArrayList<Object[]> objs = (ArrayList<Object[]>) q.list();
                ArrayList<String> objsrows = new ArrayList<String>(objs.size());
                for(Object[] o : objs)
                    objsrows.add(o[0].toString().concat("#").concat(o[1].toString()).concat("#").concat(o[2].toString()));
                t.commit();
                JSONObject jo;
                JSONArray ja = new JSONArray();
                jo = new JSONObject();
                jo.put("result", "ok");
                ja.addAll(objsrows);
                jo.put("rows", ja);
                out.println(jo.toJSONString());
            } catch (HibernateException | IOException | NumberFormatException | UnexceptedResultException ex) {
                JSONObject jo;
                jo = new JSONObject();
                jo.put("result", ex.getMessage());
                t.rollback();
                out.println(jo.toJSONString());
            }
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
