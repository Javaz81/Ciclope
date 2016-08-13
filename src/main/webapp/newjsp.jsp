<%-- 
    Document   : newjsp
    Created on : 9-ago-2016, 14.52.26
    Author     : andrea
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.Query"%>
<%@page import="org.javasoft.ciclope.persistence.HibernateUtil"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.Transaction"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %> 
<%@ page import="java.io.*" %> 
<%!
    private List<String> Xxx;
    private String connectionURL = "jdbc:mysql://localhost/ciclope";
    private Connection connection = null;

    public void jspInit() {
        Transaction t = null;
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            t = s.getTransaction();
            t.begin();
            Query q = s.createSQLQuery("SELECT descrizione FROM ciclope.articolo");
            List<Object[]> aicrecs = q.list();
            Xxx=new ArrayList<String>(aicrecs.size());
            for(Object ob: aicrecs){
                Xxx.add(ob.toString());
            }
            t.commit();
        } catch (Exception ex) {
            System.out.println("Unable to connect to database.");
            if(t!=null)
                t.rollback();
        }
    }

    public void jspDestroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            System.out.println("Cannot close the db connection");
        }
    }
    public void printAll(){
        for(String s: Xxx)
            System.out.println(s);
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%  
            out.print(Xxx.get(0).toString());
            out.print("<br> L'userID è"+request.getParameter("userid").toString());
        %>
        <h1>Hello World!</h1>
    </body>
</html>
