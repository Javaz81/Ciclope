<%-- 
    Document   : nuova_pratica
    Created on : 10-ago-2016, 10.59.23
    Author     : andrea
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
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

                $("#addStandardJobAction").click(function () {
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
                                <button type="submit" class="btn btn-primary" disabled>Elimina pratica</button>
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
                                                    <input class="form-control" id="arrivo">
                                                </div>
                                                <!-- /.col-lg-5 -->
                                                <div class="col-lg-6">
                                                    <label>Data Arrivo</label>
                                                    <input class="form-control" id="data_arrivo">
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
                                            <input type="hidden" id="idVeicolo" value="">
                                            <label>Marca</label>
                                            <input class="form-control" id="marca">
                                            <label>Modello</label>
                                            <input class="form-control" id="modello">
                                            <label>Targa</label>
                                            <input class="form-control" id="targa">
                                            <label>Kilometraggio</label>
                                            <input class="form-control" id="kilometraggio">
                                            <label>Anno</label>
                                            <input class="form-control" id="anno">
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
                                                    <input type="radio" name="tipo" id="tipo_ple" value="PLE" checked>PLE
                                                </label>
                                                <label class="radio-inline">
                                                    <input type="radio"  name="tipo" id="tipo_pv" value="PV">PV
                                                </label>
                                                <label class="radio-inline">
                                                    <input type="radio" name="tipo" id="tipo_autogru" value="AUTOGRU">AUTOGRU
                                                </label>
                                            </div>
                                            <label>Matricola</label>
                                            <input class="form-control" id="matricola">
                                            <label>Ore</label>
                                            <input class="form-control" id="ore">
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
                                            <input type="hidden" name="idCliente" id="idCliente" value="">
                                            <label>Nome</label>
                                            <input class="form-control" id="nome">
                                            <label>Cognome</label>
                                            <input class="form-control" id="cognome">
                                            <label>Celluare</label>
                                            <input class="form-control" id="cellulare">
                                            <label>Localita</label>
                                            <input class="form-control" id="localita">
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
                                                    <select class="form-control" id='preventivo_lavori' value="">
                                                        <option>Si</option>
                                                        <option>No</option>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="preventivo_lavori_data">
                                                    <script>
                                                        $("#preventivo_lavori_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Revisione MCTC</label>
                                                    <select class="form-control" id='revisione_mctc' value="">
                                                        <option>Si</option>
                                                        <option>No</option>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="revisione_mctc_data">
                                                    <script>
                                                        $("#revisione_mctc_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Collaudo USL</label>
                                                    <select class="form-control" id='collaudo_usl' value="">
                                                        <option>Si</option>
                                                        <option>No</option>
                                                    </select>
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="collaudo_usl_data">
                                                    <script>
                                                        $("#collaudo_usl_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Registro di Controllo</label>
                                                    <select class="form-control" id='registro_di_controllo' value="">
                                                        <option>Si</option>
                                                        <option>No</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-6'>
                                                    <label>Fattura N:</label>
                                                    <input class="form-control" id='fattura' value="">
                                                </div>
                                                <div class='col-lg-6'>
                                                    <label>del</label>
                                                    <input class="form-control" id="fattura_data">
                                                    <script>
                                                        $("#fattura_data").datepicker();
                                                    </script>
                                                </div>
                                            </div>
                                            <div class='row'>
                                                <div class='col-lg-12'>
                                                    <label>Lavori segnalati all'avvio:</label>
                                                    <textarea class="form-control" rows="6"></textarea>
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
                                    <div class="panel-body categoria categoria_1">
                                        <div class="panel panel-default">
                                            <div class="panel-heading">
                                                <div class="panel-title">
                                                    <a style="margin-left:1em" data-toggle="collapse"
                                                       href="#categoria_1">CONTROLLI ARRIVO</a>
                                                    <div class="pull-right">
                                                        <div class="btn-group">
                                                            <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                                                Azioni
                                                                <span class="caret"></span>
                                                            </button>
                                                            <ul class="dropdown-menu pull-right" role="menu">
                                                                <li>
                                                                    <a href="#" id="addStandardJobAction">Aggingi lavoro standard</a>
                                                                    <!--<a href="#" data-toggle="modal" id="addStandardJobAction" data-target="#add_standard_job">Aggingi lavoro standard</a>-->
                                                                </li>
                                                                <li><a href="#">Aggingi lavoro personalizzato</a>
                                                                </li>
                                                                <li class="divider"></li>
                                                                <li><a href="#">Elimina tutto</a>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>                                               
                                            </div>
                                            <div id="categoria_1" class="panel-collapse collapse in">
                                                <div class="panel-body">
                                                    <div class="list-group">
                                                        <a href="#" class="list-group-item">
                                                            <i class="fa fa-comment fa-fw"></i> New Comment
                                                            <span class="pull-right text-muted small"><em>4 minutes ago</em>
                                                            </span>
                                                        </a>
                                                        <a href="#" class="list-group-item">
                                                            <i class="fa fa-twitter fa-fw"></i>
                                                            3 New Followers
                                                            <span class="pull-right">
                                                                <button type="button" class="btn-xs btn-danger" data-toggle="modal" id="standard_1" data-target="#delete_job">
                                                                    <i class="fa fa-times-circle fa-fw"></i></button>
                                                            </span>
                                                        </a>
                                                        <a href="#" class="list-group-item">
                                                            <i class="fa fa-twitter fa-fw"></i>
                                                            Borda
                                                            <span class="pull-right">
                                                                <button type="button" class="btn-xs btn-primary"  data-toggle="modal" id="custom_1" data-target="#edit_job">
                                                                    <i class="fa fa-edit fa-fw"></i></button>
                                                                <button type="button" class="btn-xs btn-danger" data-toggle="modal" id="custom_1" data-target="#delete_job">
                                                                    <i class="fa fa-times-circle fa-fw"></i></button>
                                                            </span>
                                                        </a>
                                                    </div>
                                                </div>

                                            </div>                                            
                                        </div>

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
