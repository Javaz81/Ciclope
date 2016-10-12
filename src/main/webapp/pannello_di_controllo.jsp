<%-- 
    Document   : pannello_di_controllo
    Created on : 14-set-2016, 12.45.10
    Author     : andrea
--%>

<%@page import="org.javasoft.ciclope.persistence.Personale"%>
<%@page import="org.javasoft.ciclope.amministrazione.AmministrazioneUtils.DayHours"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="org.javasoft.ciclope.amministrazione.AmministrazioneUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>SuperAssistenza - Ciclope Amministrazione</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=0.9">

        <link href="js/libs/bootstrap-core/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <link href="js/libs/metisMenu/metisMenu.css" rel="stylesheet" type="text/css"/>

        <link href="js/libs/datatables/css/jquery.dataTables.min.css" rel="stylesheet" type="text/css"/>
        <link href="js/libs/datatables/css/dataTables.responsive.css" rel="stylesheet" type="text/css"/>

        <link rel="stylesheet" href="js/libs/startbootstrap-sb-admin-2/css/sb-admin-2.min.css"/>
        <link rel="stylesheet" href="js/libs/startbootstrap-sb-admin-2/css/timeline.min.css"/>

        <link href="js/libs/morris.js/morris.css" rel="stylesheet" type="text/css"/>

        <link href="js/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>

        <!-- jQuery -->
        <script src="js/libs/jquery/jquery.js"></script>
        <!-- Bootstrap Core JavaScript -->
        <script src="js/libs/bootstrap-core/js/bootstrap.js" type="text/javascript"></script>
        <!-- Metis Menu Plugin JavaScript -->
        <script src="js/libs/metisMenu/metisMenu.js" type="text/javascript"></script>
        <!-- DataTables jQuery -->
        <script src="js/libs/datatables/js/jquery.dataTables.js" type="text/javascript"></script>
        <script src="js/libs/datatables/js/dataTables.bootstrap.js" type="text/javascript"></script>
        <script src="js/libs/datatables/js/dataTables.responsive.js" type="text/javascript"></script>
        <!-- Morris js -->
        <script src="js/libs/morris.js/morris.js" type="text/javascript"></script>
        <script src="js/libs/raphael/raphael.js" type="text/javascript"></script>        
        <!--<script src="js/data/morris-data.js"></script>-->
        <!-- CANVASJS -->
        <script src="js/libs/canvasjs/canvasjs.js" type="text/javascript"></script>
        <script src="js/libs/canvasjs/jquery.canvasjs.js" type="text/javascript"></script>
        <!-- Custom theme JS -->
        <script src="js/libs/startbootstrap-sb-admin-2/js/sb-admin-2.js"></script>
    </head>
    <body>
        <div id="wrapper">
            <!-- Navigation -->
            <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-brand" href="index.html"> CICLOPE - SUPERASSISTENZA</a>
                </div>
                <!-- /.navbar-top-links -->
                <div class="navbar-default sidebar" role="navigation">
                    <div class="sidebar-nav navbar-collapse">
                        <ul class="nav" id="side-menu">
                            <li>
                                <a href="amministrazione.html?chiuse=false">
                                    <i class="fa fa-file-archive-o fa-fw"></i>
                                    Pratiche Correnti
                                </a>
                            </li>
                            <li>
                                <a href="amministrazione.html?chiuse=true">
                                    <i class="fa fa-archive fa-fw"></i>
                                    Pratiche Chiuse
                                </a>
                            </li>
                            <li>
                                <a href="pannello_di_controllo.jsp">
                                    <i class="fa fa-dashboard fa-fw"></i>
                                    Pannello di controllo
                                </a>
                            </li>
                        </ul>
                    </div>
                    <!-- /.sidebar-collapse -->
                </div>
                <!-- /.navbar-static-side -->
            </nav>
            <!--  Pages area -->
            <div id="page-wrapper">
                <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header">Pannello di controllo</h1>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-12">
                        <div class="h3">Ore Lavorate</div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-12">
                        <span style="display: inline-block">Ore non segnate negli utlimi:</span>
                        <span style="display: inline-block">
                            <select class="form-control">
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>5</option>  
                                <option>12</option>
                            </select>
                        </span>
                        <span style="display: inline-block">giorni.</span>
                    </div>
                </div>
                <div class="row">                           
                    <%
                        DayHours todayDh = new DayHours(new Date(), 0f);
                        float todayHours = 0f;
                        boolean onlyTodayNotComplete = false;
                        for (Personale personale : AmministrazioneUtils.GetAllOperatori()) {
                            todayHours = 0;
                            List<DayHours> thswpd = AmministrazioneUtils.GetTotalHourWorkedPerDay(personale.getIdPersonale(), 30);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY");
                            int count = 0;
                            for (DayHours d : thswpd) {
                                if (d.getHours() != 8f) {
                                    count++;
                                }
                                if (d.equals(todayDh)) {
                                    todayHours = d.getHours();
                                }
                            }
                            out.println("<div class=\"col-lg-3 col-md-4\">");
                            //Se hai lavorato poco solo oggi allora fai solo un warning
                            onlyTodayNotComplete = todayHours != 8f && count == 1;
                            if (onlyTodayNotComplete) {
                                out.println("<div class=\"panel panel-green\">");
                            } else {
                                out.println("<div class=\"panel " + (count <= 1 ? "panel-green" : "panel-red") + "\">");
                            }
                            out.println("<div class=\"panel-heading\">");
                            out.println("<div class=\"row\">");
                            out.println("<div class=\"col-xs-3\">");
                            out.println("<div>" + personale.getCognome().toUpperCase() + "</div>");
                            out.println("<i class=\"fa fa-user fa-4x\"></i>");
                            out.println("</div>");
                            out.println("<div class=\"col-xs-9 text-right\">");
                            if (onlyTodayNotComplete) {
                                out.println("<div class=\"huge\">" + todayHours + "</div>");
                                out.println("<div>ORE LAVORATE OGGI</div>");
                            } else if (count > 1) {
                                out.println("<div class=\"huge\">" + Integer.toString(count) + "</div>");
                                out.println("<div>GIORNI NON COMPLETATI</div>");
                            } else {
                                out.println("<div class=\"huge\"> OK </div>");
                                out.println("<div>TUTTO SEGNATO</div>");
                            }
                            out.println("</div>");
                            out.println("</div>");
                            out.println("</div>");
                            out.println("<a href=\"#\">");
                            out.println("<div class=\"panel-footer\">");
                            out.println("<span class=\"pull-left\">Vai ai dettagli</span>");
                            out.println("<span class=\"pull-right\"><i class=\"fa fa-arrow-circle-right\"></i></span>");
                            out.println("<div class=\"clearfix\"></div>");
                            out.println("</div>");
                            out.println("</a>");
                            out.println("</div>");
                            out.println("</div>");
                        }
                    %>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <h2 class="h3">Ciao</h2>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
