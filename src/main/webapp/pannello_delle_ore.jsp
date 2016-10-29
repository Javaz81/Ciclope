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
            var GIORNATE_NON_COPERTE_DATATABLE;
            var OPERATORE_SELEZIONATO, GIORNO_SELEZIONATO, FILTRO_ATTIVATO;
            OPERATORE_SELEZIONATO = -1;
            GIORNO_SELEZIONATO = -1;
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
            function updateData(opId, giorno, nome_operatore) {
                OPERATORE_SELEZIONATO = opId;
                GIORNO_SELEZIONATO = giorno;
                $("#nome_operatore").empty();
                $("#giorno_pratica").empty();
                $("#nome_operatore").text(nome_operatore);
                $("#giorno_pratica").text(giorno);
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
                            $("#editing_ore_lavorate").append('<div class="list-group" id="lista_editing">');
                            for (var i = 0; i < data.length; i++) {
                                $("#lista_editing").append('<a data-toggle="modal" data-target="#add_new_pratiche" onClick="updateData(' + data[i].operatoreId + ',\'' + data[i].day + '\',\''+data[i].operatore+'\')" href="" class="list-group-item">\n\
                                <span class="operatore" style="display:none">' + data[i].operatoreId + '</span><span class="giornata" style="display: none">' + data[i].day + '</span>\n\
                <span style="font-weight:bold">' + data[i].operatore + '</span><span style="margin-left:2em">' + data[i].day + '</span>\n\
<span class="pull-right text-muted small">\n\
<em>' + data[i].hours + '</em>\n\
</span>\n\
</a>');
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
            function filterOperatore(opId){
                if(OPERATORE_SELEZIONATO === opId)
                    OPERATORE_SELEZIONATO = '-1';
                else
                    OPERATORE_SELEZIONATO = opId;
                aggiornaDettaglioGiorniNonCompleti(OPERATORE_SELEZIONATO);
            }
            $(document).ready(function () {
                aggiornaSommario();
                aggiornaDettaglioGiorniNonCompleti();
                $("#giorni_selezionati").change(function () {
                    aggiornaSommario();
                    aggiornaDettaglioGiorniNonCompleti();
                });
               
                //Giornate NON coperte DataTable section
                $('#giornateNonCoperteTable tfoot th').each(function () {
                    var title = $(this).text();
                    $(this).html('<input type="text" placeholder="Search ' + title + '" />');
                });
                GIORNATE_NON_COPERTE_DATATABLE = $('#giornateNonCoperteTable').DataTable({
                    language: {
                        lengthMenu: "Mostra _MENU_ elementi per pagina",
                        zeroRecords: "Nessuna corrispondenza - spiacente",
                        info: "Pagina numero _PAGE_ di _PAGES_",
                        infoEmpty: "Nessun elemento presente",
                        infoFiltered: "(filtrato da _MAX_ elementi totali)"
                    },
                    responsive: true,
                    processing: true,
                    serverSide: true,
                    ajax: {
                        url: "GetPraticheLavorabiliCoperturaOrario",
                        type: "POST",
                        data: {id_operatore: OPERATORE_SELEZIONATO, data_selezionata: GIORNO_SELEZIONATO}
                    },
                    sDom: 'lrtip', //to hide global search input box.
                    initComplete: function () {
                        this.api().columns().every(function () {
                            var that = this;
                            $('input', this.footer()).on('keyup change', function () {
                                if (that.search() !== this.value) {
                                    that.search(this.value).draw();
                                }
                            });
                        });
                    }
                });
                $('#giornateNonCoperteTable tbody').on('click', 'tr', function () {
                    $("#giornateNonCoperteTable tbody tr.selected").each(function (index, value) {
                        $(value).removeClass('selected');
                    });
                    $(this).toggleClass('selected');
                });
                /*
                 $("a.list-group-item").click(function(){
                 OPERATORE_SELEZIONATO = $(this).find("span.operatore").text();
                 GIORNO_SELEZIONATO = $(this).find("span.giornata").text();
                 });
                 */
                $("#add_new_pratiche").on('shown.bs.modal', function () {
                    GIORNATE_NON_COPERTE_DATATABLE.clear();
                    GIORNATE_NON_COPERTE_DATATABLE.ajax.reload();
                });
                $('#giornateNonCoperteTable').on('preXhr.dt', function (e, settings, data) {
                    data.id_operatore = OPERATORE_SELEZIONATO;
                    data.data_selezionata = GIORNO_SELEZIONATO;
                });
                $("#addNewPraticaButton").on('click', function () {
                    praticaId = "-1";
                    operatore = OPERATORE_SELEZIONATO.toString(); //serve perchè senno la servlet lo interpreta come long
                    ore = $("#ore_assengate").val();
                    $("#giornateNonCoperteTable tbody tr.selected").each(function (index, value) {
                        praticaId = value.childNodes[0].innerText;
                    });
                    if (praticaId === "-1") {
                        return;
                    } else {
                        
                        var p = {"praticaId": praticaId, "giornata": GIORNO_SELEZIONATO, "personale": operatore, "ore":ore};
                        requestJson = JSON.stringify(p);
                        $.ajax({
                            url: "AddPraticaLavorataOdierna",
                            cache: false,
                            data: requestJson,
                            dataType: 'json',
                            type: 'POST',
                            async: true,
                            success: function (data) {
                                //...passa direttamente alla callback 'complete'
                            },
                            error: function (xhttpjqr, err, data) {
                               alert("Errore: "+err.toLocaleString());
                            },
                            complete: function (xhttpjqr, evtobj, data) {
                                 aggiornaSommario();
                                 aggiornaDettaglioGiorniNonCompleti();
                            }
                        });
                    }
                });
            });

        </script>
        <style>
            tfoot input {
                width: 100%;
                box-sizing: border-box;
            }
            body.modal-open {
                overflow: visible;
            }
        </style>
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
                                <a href="pannello_delle_ore.jsp">
                                    <i class="fa fa-dashboard fa-fw"></i>
                                    Pannello delle ore
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
                        <h1 class="page-header">Pannello delle ore</h1>
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
                        <table id="giornateNonCoperteTable" class="table table-striped table-bordered" cellspacing='2' width="100%">
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
                        <label>Assegna le ore:</label><input id="ore_assengate" type="number" style="display: inline-block" class="form-control" value="1.0"/>
                    </div>
                    <div class="modal-footer">
                        <div class="row">
                            <div class="col-lg-12">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                                <button type="button" id="addNewPraticaButton" class="btn btn-primary" data-dismiss="modal">Assegna</button>
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
