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
        <script type="text/javascript">
            function aggiornaSommario() {
                var value = $("#giorni_selezionati option:selected").text();
                var p = {lastDays: value};
                requestJson = JSON.stringify(p);
                $.ajax({
                    url: "GetLastWorkedDaysStatus",
                    cache: false,
                    dataType: 'json',
                    data: requestJson,
                    type: 'POST',
                    async: true,
                    success: function (data) {
                        $("#worked_days_resume").empty();
                        if (data !== undefined) {
                            for (var i = 0; i < data.length; i++) {
                                var html = $("#worked_days_resume").append(data[i].panel);
                            }
                        }
                    },
                    error: function (xhttpjqr, err, data) {
                        alert(err);
                        $("#worked_days_resume").text("Errore nel reperire i dati");
                    },
                    complete: function (xhttpjqr, evtobj, data) {
                    }
                });
            }
            function aggiornaDettaglioGiorniNonCompleti(opId) {
                var value = $("#giorni_selezionati option:selected").text();
                var operatore = opId === undefined ? '-1' : opId;
                var p = {lastDays: value, idOperatore: operatore};
                requestJson = JSON.stringify(p);
                $.ajax({
                    url: "GetCopertureOrarieNonRegistrate",
                    cache: false,
                    dataType: 'json',
                    data: requestJson,
                    type: 'POST',
                    async: true,
                    success: function (data) {
                        $("#editing_ore_lavorate").empty();
                        if (data !== undefined) {
                            $("#editing_ore_lavorate").append('<div class="list-group">');
                            for (var i = 0; i < data.length; i++) {                                
                                $("#editing_ore_lavorate").html('<li><a href = "#"><div><i class="fa fa-twitter fa-fw"></i>' + data[i].operatore + ' ' + data[i].day);
                                $("#editing_ore_lavorate").append('<span class="pull-right text-muted small" >' + data[i].hours + '</span>');
                                $("#editing_ore_lavorate").append('< /div>< /a>< /li>< li class = "divider" > < /li>');
                            }
                            $("#editing_ore_lavorate").append('</div>');
                        }
                    },
                    error: function (xhttpjqr, err, data) {
                        alert(err);
                        $("#editing_ore_lavorate").append("<div class='alert alert-danger'>Errore nel reperire i dati</div>");
                    },
                    complete: function (xhttpjqr, evtobj, data) {
                    }
                });
            }
            $(document).ready(function () {
                aggiornaSommario();
                aggiornaDettaglioGiorniNonCompleti();
                $("#giorni_selezionati").change(function () {
                    aggiornaSommario();
                    aggiornaDettaglioGiorniNonCompleti();
                });
            });

        </script>
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
                <div class="row" style="margin-bottom:1em">
                    <div class="col-lg-12">
                        <span style="display: inline-block">Ore non segnate negli utlimi:</span>
                        <span style="display: inline-block">
                            <select style="font-weight: bold; display:inline-block" class="form-control" id="giorni_selezionati">
                                <option>1</option>
                                <option selected>7</option>
                                <option>14</option>
                                <option>34</option>  
                                <option>60</option>
                            </select>
                        </span>
                        <span style="display: inline-block">giorni.</span>
                    </div>
                </div>
                <div class="row" id="worked_days_resume">
                </div>
                <div class="row" id="borda">
                    <div class="col-lg-12">
                        <a data-toggle='modal' data-target='#add_new_pratiche' href=""><button type="button" class="btn btn-default btn-outline">Borda</button></a>
                    </div>
                </div>

                <div class="row" >
                    <div class="col-lg-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                Giornate non completate
                            </div>
                            <div class="panel-body" id="editing_ore_lavorate">

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="add_new_pratiche" tabindex="-1" role="dialog" aria-labelledby="addNewPraticheLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="addNewPraticaLabel" >COPERTURA ORARIA di <span id="nome_operatore">NOME COGNOME</span> del <span id="giorno_pratica">DD/MM/YYYY</span></h3>
                    </div>
                    <div class="modal-body">                        
                        <p style="text-align:center; margin:1em " >Seleziona la pratica e inserisci le ore lavorate:</p>
                        <table id="clienteTableSelection" class="table table-striped table-bordered" cellspacing='2' width="100%">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Pratica</th>
                                    <th>Cliente</th>
                                    <th>Marca</th>
                                    <th>Targa</th>
                                    <th>Matricola</th>
                                </tr>
                            </thead>
                            <tfoot>
                                <tr>
                                    <th>ID</th>
                                    <th>Pratica</th>
                                    <th>Cliente</th>
                                    <th>Marca</th>
                                    <th>Targa</th>
                                    <th>Matricola</th>
                                </tr>
                            </tfoot>
                            <tbody>
                                <tr>
                                    <td>NN1</td>
                                    <td>NN2</td>
                                    <td>NN3</td>
                                    <td>NN4</td>
                                    <td>NN5</td>
                                    <td>NN6</td>
                                </tr>
                            </tbody>
                        </table>
                        <label>Assegna le ore:</label><input type="number" style="display: inline-block" class="form-control" value="1.0"/>
                    </div>
                    <div class="modal-footer">
                        <div class="row">
                            <div class="col-lg-12">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                                <button type="button" id="addNewPraticaButton" class="btn btn-primary" data-dismiss="modal">Assegna</button>
                            </div>                          
                        </div>
                        <div class="row">
                            <div class="col-lg-12">

                            </div>                          
                        </div>                       
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
    </body>
</html>
