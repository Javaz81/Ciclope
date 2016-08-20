<%-- 
    Document   : nuova_pratica
    Created on : 10-ago-2016, 10.59.23
    Author     : andrea
--%>

<%@page import="java.util.List"%>
<%@page import="org.javasoft.ciclope.amministrazione.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
    public List<CompletePraticaInfo> praticaInfos;

    public void jspInit() {
        System.out.println("jspInit");
    }

    public void jspDestroy() {
        System.out.println("jspDestroy");
    }
%>
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

        <link href="js/libs/jqueryui/jquery-ui.css" rel="stylesheet" type="text/css"/>

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
        <!-- JQuery UI -->
        <script src="js/libs/jqueryui/jquery-ui.js" type="text/javascript"></script>
        <!-- Custom theme JS -->
        <script src="js/libs/startbootstrap-sb-admin-2/js/sb-admin-2.js"></script>
        <script>
            // declarations
            var STANDARD_JOBS_DATATABLE;
            var CATEGORIA_SELEZIONATA = "-1";
            var PRATICA_SELEZIONATA = "-1";
            var LAVORO_SELEZIONATO = "-1";
            var DATATABLE_DATA = {};
            // functions
            function getParameterByName(name, url) {
                if (!url)
                    url = window.location.href;
                name = name.replace(/[\[\]]/g, "\\$&");
                var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                        results = regex.exec(url);
                if (!results)
                    return null;
                if (!results[2])
                    return '';
                return decodeURIComponent(results[2].replace(/\+/g, " "));
            }
            PRATICA_SELEZIONATA = getParameterByName("praticaId");
            // events
            $(document).ready(function () {
                $.datepicker.setDefaults({
                    dayNames: ["Domenica", "Lunedi", "Martedi", "Mercoledi", "Giovedi", "Venerdi", "Sabato"],
                    dayNamesMin: ["Do", "Lu", "Ma", "Me", "Gi", "Ve", "Sa"],
                    monthNames: ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
                    dateFormat: "DD dd-mm-yy",
                    showAnim: "slide"
                });

                $(".addStandardJobAction").click(function () {
                    STANDARD_JOBS_DATATABLE.clear();
                    STANDARD_JOBS_DATATABLE.ajax.reload();
                    $("#add_standard_job").modal('show');
                });
                STANDARD_JOBS_DATATABLE = $('#standardJobTableSelection').DataTable({
                    responsive: true,
                    processing: true,
                    serverSide: true,
                    ajax: {
                        url: "GetStandardJobsDataTable",
                        type: "POST",
                        data: {praticaId: PRATICA_SELEZIONATA, categoriaId: CATEGORIA_SELEZIONATA}
                    },
                    sDom: 'lrtip', //to hide global search input box.
                    initComplete: function () {
                        this.api().columns().every(function () {
                            var column = this;
                            var select = $('<select><option value=""></option></select>')
                                    .appendTo($(column.footer()).empty())
                                    .on('change', function () {
                                        var val = $.fn.dataTable.util.escapeRegex(
                                                $(this).val()
                                                );
                                        column
                                                .search(val)
                                                .draw();
                                    });
                            column.data().unique().sort().each(function (d, j) {
                                select.append('<option value="' + d + '">' + d + '</option>');
                            });
                        });
                    }
                });
                $('#standardJobTableSelection')
                        .on('preXhr.dt', function (e, settings, data) {
                            data.praticaId = PRATICA_SELEZIONATA;
                            data.categoriaId = CATEGORIA_SELEZIONATA;
                        });
                $(".categoria *").on("click", function () {
                    var categoryParent = $(this).parents(".categoria");
                    var classList = categoryParent.attr('class').split(/\s+/);
                    $.each(classList, function (index, item) {
                        if (item.startsWith("categoria_")) {
                            var sp = item.split("_");
                            CATEGORIA_SELEZIONATA = sp[1];
                        }
                    });
                });
                $('#standardJobTableSelection tbody').on('click', 'tr', function () {
                    $(this).toggleClass('selected');
                });
            });
        </script>
    </head>
    <body>
        <%
            praticaInfos = AmministrazioneUtils.GetAllPraticaInformation(Integer.parseInt(request.getParameter("praticaId")));
            //categoriaInfos = AmministrazioneUtils.GetAllCategoryInformation(Integer.parseInt(request.getParameter("praticaId")));
        %>
        <div id="wrapper">

            <!-- Navigation -->
            <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-brand" href="index.html"> Ciclope - SuperAssistenza</a>
                </div>
                <!-- /.navbar-top-links -->
                <div class="navbar-default sidebar" role="navigation">
                    <div class="sidebar-nav navbar-collapse">
                        <ul class="nav" id="side-menu">
                            <li>
                                <a href="amministrazione.html">
                                    <i class="fa fa-archive fa-fw"></i>
                                    Pratiche
                                    <span class="fa arrow"></span>
                                </a>
                                <ul class="nav nav-second-level">
                                    <li>
                                        <a href="#"><i class="fa fa-folder fa-fw"></i>Pratiche chiuse</a>
                                    </li>
                                </ul>
                            </li>

                            <li>
                                <a href="amministrazione.html"><i class="fa fa-clock-o fa-fw"></i> Ore lavorate</a>
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
                        <h1 class="page-header">Dati Pratica</h1>
                    </div>
                </div>
                <form role="form" method="POST">
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="btn-group" style="margin-bottom:1em">
                                <button type="submit" class="btn btn-primary">Salva modifiche</button>
                                <button type="submit" class="btn btn-primary">Esporta pratica</button>
                                <button type="submit" class="btn btn-primary">Stampa pratica</button>
                                <%
                                    if (request.getParameter("newPratice").equalsIgnoreCase("true")) {
                                        out.println("<button type=\"submit\" class=\"btn btn-primary\" disabled>Elimina pratica</button>");
                                    } else {
                                        out.println("<button type=\"submit\" class=\"btn btn-primary\">Elimina pratica</button>");
                                    }
                                %>

                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-file fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#collapseIntestazionePrincipale">Intestazione principale</a>
                                    </div>                                    

                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse in" id="collapseIntestazionePrincipale">
                                    <div class="panel-body">
                                        <div class="row">

                                            <div class="form-group">
                                                <div class="col-lg-6">
                                                    <input type="hidden" id="idPratica" name="idPratica" value=""/>
                                                    <label>Codice Arrivo</label>
                                                    <input class="form-control" id="arrivo" <% out.print("value=\"" + praticaInfos.get(0).getArrivo() + "\""); %> >
                                                </div>
                                                <!-- /.col-lg-5 -->
                                                <div class="col-lg-6">
                                                    <label>Data Arrivo</label>
                                                    <input class="form-control" id="data_arrivo"  <% out.print("value=\"" + praticaInfos.get(0).getData_arrivo() + "\""); %> >
                                                    <script>
                                                        $("#data_arrivo").datepicker();
                                                    </script>
                                                </div>                                            
                                            </div>
                                            <!-- /.row -->
                                        </div>                                 
                                    </div>
                                </div>
                                <!-- /.panel-body -->
                            </div>                           
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-car fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#collapseOggettoRiparazione">Oggetto della riparazione</a>
                                    </div>                                    
                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse out" id="collapseOggettoRiparazione">
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <input type="hidden" id="idVeicolo"  <% out.print("value=\"" + praticaInfos.get(0).getIdVeicolo() + "\""); %>>
                                            <label>Marca</label>
                                            <input class="form-control" id="marca"  <% out.print("value=\"" + praticaInfos.get(0).getMarcaVeicolo() + "\""); %> >
                                            <label>Modello</label>
                                            <input class="form-control" id="modello"  <% out.print("value=\"" + praticaInfos.get(0).getModelloVeicolo() + "\""); %> >
                                            <label>Targa</label>
                                            <input class="form-control" id="targa"  <% out.print("value=\"" + praticaInfos.get(0).getTargaVeicolo() + "\""); %> >
                                            <label>Kilometraggio</label>
                                            <input class="form-control" id="kilometraggio"  <% out.print("value=\"" + praticaInfos.get(0).getKilometraggioVeicolo() + "\""); %> >
                                            <label>Anno</label>
                                            <input class="form-control" id="anno"  <% out.print("value=\"" + praticaInfos.get(0).getAnnoVeicolo() + "\""); %> >
                                            <script>
                                                $("#anno").datepicker({
                                                    changeYear: true,
                                                    yearRange: "1930:2150",
                                                    dateFormat: "yy"
                                                });
                                            </script>
                                            <div class="form-group" style="margin-top:1em; margin-bottom: 1em">
                                                <label>Tipo:</label>
                                                <label class="radio-inline">
                                                    <input type="radio" name="tipo" id="tipo_ple" value="PLE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PLE") ? "checked" : "")); %> >PLE
                                                </label>
                                                <label class="radio-inline">
                                                    <input type="radio"  name="tipo" id="tipo_pv" value="PV" <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PV") ? "checked" : "")); %>>PV
                                                </label>
                                                <label class="radio-inline">
                                                    <input type="radio" name="tipo" id="tipo_autogru" value="AUTOGRU" <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("AUTOGRU") ? "checked" : "")); %> >AUTOGRU
                                                </label>
                                            </div>
                                            <label>Matricola</label>
                                            <input class="form-control" id="matricola" <% out.print("value=\"" + praticaInfos.get(0).getMatricolaVeicolo() + "\""); %> >
                                            <label>Ore</label>
                                            <input class="form-control" id="ore" <% out.print("value=\"" + praticaInfos.get(0).getOreVeicolo() + "\""); %> >
                                        </div>
                                        <!-- /.row -->
                                    </div> 
                                </div>                                                              
                            </div>
                        </div>                        
                        <div class="col-lg-6">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-user fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"                                             
                                           href="#collapseCliente">Informazioni cliente</a>
                                    </div>                                   
                                </div>
                                <!-- /.panel-heading -->
                                <div id="collapseCliente" class="panel-collapse collapse out">
                                    <div class="panel-body">
                                        <div class="form-group">
                                            <input type="hidden" name="idCliente" id="idCliente" <% out.print("value=\"" + praticaInfos.get(0).getIdCliente() + "\""); %>>
                                            <label>Nome</label>
                                            <input class="form-control" id="nome" <% out.print("value=\"" + praticaInfos.get(0).getNomeCliente() + "\""); %> >
                                            <label>Cognome</label>
                                            <input class="form-control" id="cognome" <% out.print("value=\"" + praticaInfos.get(0).getCognomeCliente() + "\""); %> >
                                            <label>Celluare</label>
                                            <input class="form-control" id="cellulare" <% out.print("value=\"" + praticaInfos.get(0).getCellulareCliente() + "\""); %> >
                                            <label>Localita</label>
                                            <input class="form-control" id="localita" <% out.print("value=\"" + praticaInfos.get(0).getLocalitaCliente() + "\""); %> >
                                        </div>
                                        <!-- /.row -->
                                    </div>
                                </div>               
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-check fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"                                            
                                           href="#collapseControlliAccettazione">Controlli ed Accettazione</a>
                                    </div>
                                </div>
                                <!-- /.panel-heading -->
                                <div id="collapseControlliAccettazione" class="panel-collapse collapse out">
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Preventivo Lavori</label>
                                                    <select class="form-control" id='preventivo_lavori'>
                                                        <%
                                                            if (praticaInfos.get(0).getPreventivo_lavori().equalsIgnoreCase("si")) {
                                                                out.println("<option selected>Si</option>");
                                                                out.println("<option>No</option>");
                                                            } else {
                                                                out.println("<option>Si</option>");
                                                                out.println("<option selected>No</option>");
                                                            }
                                                        %>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="preventivo_lavori_data"  <% out.print("value=\"" + praticaInfos.get(0).getPreventivo_lavori_data() + "\""); %>>
                                                    <script>
                                                        $("#preventivo_lavori_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Revisione MCTC</label>
                                                    <select class="form-control" id='revisione_mctc' value="">
                                                        <%
                                                            if (praticaInfos.get(0).getRevisione_mctc().equalsIgnoreCase("si")) {
                                                                out.println("<option selected>Si</option>");
                                                                out.println("<option>No</option>");
                                                            } else {
                                                                out.println("<option>Si</option>");
                                                                out.println("<option selected>No</option>");
                                                            }
                                                        %>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="revisione_mctc_data" <% out.print("value=\"" + praticaInfos.get(0).getRevisione_mctc_data() + "\""); %>>
                                                    <script>
                                                        $("#revisione_mctc_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Collaudo USL</label>
                                                    <select class="form-control" id='collaudo_usl' value="">
                                                        <%
                                                            if (praticaInfos.get(0).getCollaudo_usl().equalsIgnoreCase("si")) {
                                                                out.println("<option selected>Si</option>");
                                                                out.println("<option>No</option>");
                                                            } else {
                                                                out.println("<option>Si</option>");
                                                                out.println("<option selected>No</option>");
                                                            }
                                                        %>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="collaudo_usl_data"  <% out.print("value=\"" + praticaInfos.get(0).getCollaudo_usl_data() + "\""); %> >
                                                    <script>
                                                        $("#collaudo_usl_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Registro di Controllo</label>
                                                    <select class="form-control" id='registro_di_controllo'>
                                                        <%
                                                            if (praticaInfos.get(0).getRegistro_di_controllo().equalsIgnoreCase("si")) {
                                                                out.println("<option selected>Si</option>");
                                                                out.println("<option>No</option>");
                                                            } else {
                                                                out.println("<option>Si</option>");
                                                                out.println("<option selected>No</option>");
                                                            }
                                                        %>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Fattura N:</label>
                                                    <input class="form-control" id='fattura' <% out.print("value=\"" + praticaInfos.get(0).getNumero_fattura() + "\""); %>>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="fattura_data" <% out.print("value=\"" + praticaInfos.get(0).getData_fattura() + "\""); %>>
                                                    <script>
                                                        $("#fattura_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-12'>
                                                    <label>Lavori segnalati all'avvio:</label>
                                                    <textarea id="lavori_segnalati" class="form-control" rows="6">
                                                        <% out.println(praticaInfos.get(0).getLavori_segnalati());%>
                                                    </textarea>
                                                    <!-- 
                                                    Questo pezzetto di javascript è un trim sulla stringa dei lavori segnalati,
                                                    poichè schioccandola con l'out.println il jspengine mi da degli spazi prima e 
                                                    dopo che mi danno fastidio. Mi tocca farla quindi quando il DOM è ready, ma mettendola
                                                    qui nel <body> direttamente anzichè tra le funzioni dell'<head> essendo un workaround
                                                    -->
                                                    <script>
                                                        (function () {
                                                            var txt = $("#lavori_segnalati").text();
                                                            var txt2 = $.trim(txt);
                                                            $("#lavori_segnalati").text(txt2);
                                                        })();
                                                    </script>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.row -->
                                    </div> 
                                </div>                                                               
                            </div>
                        </div>                        
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="panel panel-default panel-group">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-check-square fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#lavoriDaEffettuare">Lavori da Effettuare</a>
                                    </div>
                                </div>
                                <!-- /.panel-heading -->
                                <div id="lavoriDaEffettuare" class="panel-collapse collapse in">

                                    <div class="panel-body">
                                        <%
                                            for (CompleteCategoriaInfo cci : AmministrazioneUtils.GetAllCategoriaLavori()) {
                                                out.println("<div class='panel panel-default categoria categoria_" + cci.getIdCategoria() + "'>");
                                                out.println("   <div class='panel-heading'>");
                                                out.println("       <div class='panel-title'>");
                                                out.println("           <a style='margin-left:1em' data-toggle='collapse' href='#categoria_" + cci.getIdCategoria() + "'>" + cci.getNome() + "</a>");
                                                out.println("               <div class='pull-right'>");
                                                out.println("                   <div class='btn-group'>");
                                                out.println("                       <button type='button' class='btn btn-default btn-xs dropdown-toggle' data-toggle='dropdown'>");
                                                out.println("                           Azioni");
                                                out.println("                           <span class='caret'></span>");
                                                out.println("                       </button>");
                                                out.println("                       <ul class='dropdown-menu pull-right' role='menu'>");
                                                out.println("                           <li>");
                                                out.println("                               <a href='#' class='addStandardJobAction'>Aggingi lavoro standard</a>");
                                                out.println("                           </li>");
                                                out.println("                           <li>");
                                                out.println("                               <a href='#'>Aggingi lavoro personalizzato</a>");
                                                out.println("                           </li>");
                                                out.println("                           <li class='divider'></li>");
                                                out.println("                           <li>");
                                                out.println("                               <a href='#'>Elimina tutto</a>");
                                                out.println("                           </li>");
                                                out.println("                       </ul>");
                                                out.println("                   </div>");
                                                out.println("               </div>");
                                                out.println("           </div>");
                                                out.println("       </div>");
                                                out.println("       <div id='categoria_" + cci.getIdCategoria() + "' class='panel-collapse collapse in'>");
                                                out.println("           <div class='panel-body'>");
                                                out.println("               <div class='list-group'>");
                                                for (TipoLavoroPratica tlp
                                                        : AmministrazioneUtils.GetAllLavori(Integer.parseInt(request.getParameter("praticaId")), Integer.parseInt(cci.getIdCategoria()))) {                                                    
                                                    if(tlp.getTipo().equalsIgnoreCase("S")){
                                                        //item standard template
                                                        out.println("<a href='#' class='list-group-item'>");
                                                        out.println("   <i class='fa fa-toggle-right fa-fw'></i>");
                                                        out.println(        tlp.getDescrizione());
                                                        out.println("   <span class='pull-right'>");
                                                        out.println("       <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='standard_" + tlp.getIdLavoro() + "' data-target='#delete_job'>");
                                                        out.println("           <i class='fa fa-times-circle fa-fw'></i>");
                                                        out.println("       </button>");
                                                        out.println("   </span>");
                                                        out.println("</a>");
                                                    }else{
                                                        //item custom template
                                                        out.println("<a href='#' class='list-group-item'>");
                                                        out.println("   <i class='fa fa-toggle-right fa-fw'></i>");
                                                        out.println("   "+tlp.getDescrizione());
                                                        out.println("   <span class='pull-right'>");
                                                        out.println("       <button type='button' class='btn-xs btn-primary'  data-toggle='modal' id='custom_"+tlp.getIdLavoro()+"' data-target='#edit_job'>");
                                                        out.println("           <i class='fa fa-edit fa-fw'></i>");
                                                        out.println("       </button>");
                                                        out.println("       <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='custom_"+tlp.getIdLavoro()+"' data-target='#delete_job'>");
                                                        out.println("           <i class='fa fa-times-circle fa-fw'></i>");
                                                        out.println("       </button>");
                                                        out.println("   </span>");
                                                        out.println("</a>");
                                                    }
                                                }
                                                //end category panel
                                                out.println("</div>"
                                                        + "</div>"
                                                        + "</div>"
                                                );
                                                out.println("</div>");
                                            }
                                        %>
                                        <!-- <div class='panel panel-default categoria categoria_1'>
                                            <div class='panel-heading'>                                                
                                                <div class='panel-title'>
                                                    <a style='margin-left:1em' data-toggle='collapse'
                                                       href='#categoria_1'>CONTROLLI ARRIVO</a>
                                                    <div class='pull-right'>
                                                        <div class='btn-group'>
                                                            <button type='button' class='btn btn-default btn-xs dropdown-toggle' data-toggle='dropdown'>
                                                                Azioni
                                                                <span class='caret'></span>
                                                            </button>
                                                            <ul class='dropdown-menu pull-right' role='menu'>
                                                                <li>
                                                                    <a href='#' id='addStandardJobAction'>Aggingi lavoro standard</a>                                                                   
                                                                </li>
                                                                <li><a href='#'>Aggingi lavoro personalizzato</a>
                                                                </li>
                                                                <li class='divider'></li>
                                                                <li><a href='#'>Elimina tutto</a>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>                                               
                                            </div>
                                            <div id='categoria_1' class='panel-collapse collapse in'>
                                                <div class='panel-body'>
                                                    <div class='list-group'>
                                                        <a href='#' class='list-group-item'>
                                                            <i class='fa fa-comment fa-fw'></i> New Comment
                                                            <span class='pull-right text-muted small'><em>4 minutes ago</em>
                                                            </span>
                                                        </a>
                                                        <a href='#' class='list-group-item'>
                                                            <i class='fa fa-twitter fa-fw'></i>
                                                            3 New Followers
                                                            <span class='pull-right'>
                                                                <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='standard_1' data-target='#delete_job'>
                                                                    <i class='fa fa-times-circle fa-fw'></i></button>
                                                            </span>
                                                        </a>
                                                        <a href='#' class='list-group-item'>
                                                            <i class='fa fa-twitter fa-fw'></i>
                                                            Borda
                                                            <span class='pull-right'>
                                                                <button type='button' class='btn-xs btn-primary'  data-toggle='modal' id='custom_1' data-target='#edit_job'>
                                                                    <i class='fa fa-edit fa-fw'></i></button>
                                                                <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='custom_1' data-target='#delete_job'>
                                                                    <i class='fa fa-times-circle fa-fw'></i></button>
                                                            </span>
                                                        </a>
                                                    </div>
                                                </div>

                                            </div>                                            
                                        </div>
                                        -->
                                    </div> 
                                </div>                                                               
                            </div>
                        </div>    
                    </div>
                </form>
            </div>
        </div>
        <!-- /#page-wrapper -->
        <!-- MODAL per
         EDIT_JOB,  
         DELETE_JOB,
         ADD_CUSTOM_JOB,
         ADD_STANDARD_JOB,
         SAVE,
         EXPORT,
         DELETE
        -->
        <div class="modal fade" id="delete_job" tabindex="-1" role="dialog" aria-labelledby="deleteJobLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="deleteJobLabel">Eliminazione lavoro</h4>
                    </div>
                    <div class="modal-body">
                        Vuoi togliere questo lavoro dalla lista?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Togli pure</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
        <div class="modal fade" id="edit_job" tabindex="-1" role="dialog" aria-labelledby="editJobLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="editJobLabel">Modifica lavoro personalizzato</h4>
                    </div>
                    <div class="modal-body">
                        <label for="edit">Inserisci la nuova descrizione:</label>
                        <input class="form-control" id="editbox_job" value="">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Salva modifica</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
        <div class="modal fade" id="add_custom_job" tabindex="-1" role="dialog" aria-labelledby="addCustomJobLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="addCustomJobLabel">Inserimento nuovo lavoro personalizzato</h4>
                    </div>
                    <div class="modal-body">
                        <label for="customJobCategory">Categoria</label>
                        <select id="categoria_job" value="">
                            <option>Categoria 1</option>
                        </select>
                        <label for="edit">Modifica il testo</label>
                        <input class="form-control" id="editbox_job" value="">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Aggiungi</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <!-- /.modal -->
        <div class="modal fade" id="add_standard_job" role="dialog">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Inserimento nuovo lavoro standard</h4>
                    </div>
                    <div class="modal-body">
                        <table id="standardJobTableSelection" class="table table-striped table-bordered" cellspacing='0' width="50%">
                            <thead>
                                <tr>
                                    <th>Categoria</th>
                                    <th>Codice</th>
                                    <th>Descrizione</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                            <tfoot>
                                <tr>
                                    <th>Categoria</th>
                                    <th>Codice</th>
                                    <th>Descrizione</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Aggiungi</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
        <div id="dialog-message" title="Azione eseguita">
            <p id="messaggio_pratica">

            </p>
        </div>
    </div>
</body>
</html>
