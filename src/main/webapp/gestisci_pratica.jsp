<%-- 
    Document   : nuova_pratica
    Created on : 10-ago-2016, 10.59.23
    Author     : andrea
--%>

<%@page import="java.util.ArrayList"%>
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
            var VEICOLO_DATATABLE;
            var CLIENTE_DATATABLE;
            var CATEGORIA_SELEZIONATA = "-1";
            var PRATICA_SELEZIONATA = "-1";
            var LAVORO_SELEZIONATO = "-1";
            var TIPO_LAVORO_SELEZIONATO = "";
            var DATATABLE_DATA = {};
            // functions

            function getRandomArrivoCode() {
                var oggi = new Date();
                var codice = new String();
                codice = "TEMPORANEO_".concat(oggi.getTime().toString());
                return codice;
            }

            function getDataArrivo() {
                var oggi = new Date();
                var day = new String();
                switch (new Date().getDay()) {
                    case 0:
                        day = "Domenica";
                        break;
                    case 1:
                        day = "Lunedi";
                        break;
                    case 2:
                        day = "Martedi";
                        break;
                    case 3:
                        day = "Mercoledi";
                        break;
                    case 4:
                        day = "Giovedi";
                        break;
                    case 5:
                        day = "Venerdi";
                        break;
                    case 6:
                        day = "Sabato";
                }
                day = day.concat(" ").concat(oggi.getDate()).concat("-").concat(oggi.getMonth() + 1).concat("-").concat(oggi.getFullYear());
                return day;
            }
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

                //Standard Jobs DataTable section
                $("#add_standard_job").on('shown.bs.modal', function () {
                    STANDARD_JOBS_DATATABLE.clear();
                    STANDARD_JOBS_DATATABLE.ajax.reload();
                });
                $('#standardJobTableSelection').on('preXhr.dt', function (e, settings, data) {
                    data.praticaId = PRATICA_SELEZIONATA;
                    data.categoriaId = CATEGORIA_SELEZIONATA;
                });
                $('#standardJobTableSelection tbody').on('click', 'tr', function () {
                    $(this).toggleClass('selected');
                });
                $('#standardJobTableSelection tfoot th').each(function () {
                    var title = $(this).text();
                    $(this).html('<input type="text" placeholder="Search ' + title + '" />');
                });
                STANDARD_JOBS_DATATABLE = $('#standardJobTableSelection').DataTable({
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
                        url: "GetStandardJobsDataTable",
                        type: "POST",
                        data: {praticaId: PRATICA_SELEZIONATA, categoriaId: CATEGORIA_SELEZIONATA}
                    },
                    sDom: 'lrtip', //to hide global search input box.
                    initComplete: function () {
                        this.api().columns().every(function () {
                            var that = this;
                            $('input', this.footer()).on('keyup change', function () {
                                if (that.search() !== this.value) {
                                    that
                                            .search(this.value)
                                            .draw();
                                }
                            });
                        });
                    }
                });

                //Veicolo DataTable section
                $("#addNewVeicoloButton").click(function () {
                    var marca = $("#marca1").val();
                    var modello = $("#modello1").val();
                    var targa = $("#targa1").val();
                    var anno = $("#anno1").val();
                    var tipo = $("select[name=tipo1]").val();
                    var matricola = $("#matricola1").val();

                    var p = {
                        marca: marca,
                        modello: modello,
                        targa: targa,
                        anno: anno,
                        tipo: tipo,
                        matricola: matricola
                    };
                    requestJson = encodeURIComponent(JSON.stringify(p));
                    $.ajax({
                        url: "AddVeicolo",
                        cache: false,
                        dataType: 'json',
                        data: requestJson,
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    //compila veicolo
                                    var values = data.veicolo.split("#");
                                    var tipo = values[5];
                                    $("#idVeicolo").val(values[0]);
                                    $("#marca").val(values[1]);
                                    $("#modello").val(values[2]);
                                    $("#targa").val(values[3]);
                                    $("#anno").val(values[4]);
                                    $("select[name='tipo1']").val(tipo).change();
                                    $("#matricola").val(values[6]);
                                } else {
                                    alert("Errore nel DB->" + data.messaggio);
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {
                        }
                    });
                });
                $("#setVeicoloInPraticaButton").click(function (event) {
                    veicoloId = "-1";
                    $("#veicoloTableSelection tbody tr.selected").each(function (index, value) {
                        veicoloId = value.childNodes[0].innerText;
                    });
                    if (veicoloId === "-1") {
                        return;
                    } else {
                        var jobsCode = {vid: veicoloId};
                        requestJson = JSON.stringify(jobsCode);
                        $.ajax({
                            url: "GetVeicoloInfo",
                            cache: false,
                            data: requestJson,
                            dataType: 'json',
                            type: 'POST',
                            async: true,
                            success: function (data) {
                                if (data !== undefined) {
                                    if (data.result === "ok") {
                                        //compila veicolo
                                        var values = data.row.split("#");
                                        var tipo = values[5];
                                        $("#idVeicolo").val(values[0]);
                                        $("#marca").val(values[1]);
                                        $("#modello").val(values[2]);
                                        $("#targa").val(values[3]);
                                        $("#anno").val(values[4]);
                                        $("select[name='tipo']").val(tipo).change();
                                        /*
                                         if (tipo === "PLE") {
                                         $("#tipo_ple").prop('checked', true);
                                         $("#tipo_pv").prop('checked', false);
                                         $("#tipo_autogru").prop('checked', false);
                                         } else if (tipo === "PV") {
                                         $("#tipo_ple").prop('checked', false);
                                         $("#tipo_pv").prop('checked', true);
                                         $("#tipo_autogru").prop('checked', false);
                                         } else {
                                         $("#tipo_ple").prop('checked', false);
                                         $("#tipo_pv").prop('checked', false);
                                         $("#tipo_autogru").prop('checked', true);
                                         }
                                         */
                                        $("#matricola").val(values[6]);
                                    } else {
                                        alert("Errore nel DB->" + data.result);
                                    }
                                }
                            },
                            error: function (xhttpjqr, err, data) {
                                alert(err);
                            },
                            complete: function (xhttpjqr, evtobj, data) {
                            }
                        });
                    }
                });
                $('#veicoloTableSelection tbody').on('click', 'tr', function () {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                    } else {
                        VEICOLO_DATATABLE.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                    }
                });
                $("#search_veicolo").on('shown.bs.modal', function () {
                    VEICOLO_DATATABLE.clear();
                    VEICOLO_DATATABLE.ajax.reload();
                });
                $('#veicoloTableSelection tfoot th').each(function () {
                    var title = $(this).text();
                    $(this).html('<input type="text" placeholder="Search ' + title + '" />');
                });
                VEICOLO_DATATABLE = $('#veicoloTableSelection').DataTable({
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
                        url: "GetVeicoli",
                        type: "POST"
                    },
                    sDom: 'lrtip', //to hide global search input box.

                    initComplete: function () {
                        this.api().columns().every(function () {
                            var that = this;
                            $('input', this.footer()).on('keyup change', function () {
                                if (that.search() !== this.value) {
                                    that
                                            .search(this.value)
                                            .draw();
                                }
                            });
                        });
                    }
                });

                //CLiente Datatable secion
                $("#addNewClienteButton").click(function () {
                    var nome = $("#nome1").val();
                    var cognome = $("#cognome1").val();
                    var cellulare = $("#cellulare1").val();
                    var localita = $("#localita1").val();

                    var p = {
                        nome: nome,
                        cognome: cognome,
                        cellulare: cellulare,
                        localita: localita
                    };
                    requestJson = encodeURIComponent(JSON.stringify(p));
                    $.ajax({
                        url: "AddCliente",
                        cache: false,
                        dataType: 'json',
                        data: requestJson,
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    //compila veicolo
                                    var values = data.cliente.split("#");
                                    $("#idCliente").val(values[0]);
                                    $("#nome").val(values[1]);
                                    $("#cognome").val(values[2]);
                                    $("#cellulare").val(values[3]);
                                    $("#localita").val(values[4]);
                                } else {
                                    alert("Errore nel DB->" + data.messaggio);
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {
                        }
                    });
                });
                $("#setClienteInPraticaButton").click(function (event) {
                    clienteId = "-1";
                    $("#clienteTableSelection tbody tr.selected").each(function (index, value) {
                        clienteId = value.childNodes[0].innerText;
                    });
                    if (clienteId === "-1") {
                        return;
                    } else {
                        var clienteCode = {cid: clienteId};
                        requestJson = JSON.stringify(clienteCode);
                        $.ajax({
                            url: "GetClienteInfo",
                            cache: false,
                            data: requestJson,
                            dataType: 'json',
                            type: 'POST',
                            async: true,
                            success: function (data) {
                                if (data !== undefined) {
                                    if (data.result === "ok") {
                                        //compila veicolo
                                        var values = data.row.split("#");
                                        $("#idCliente").val(values[0]);
                                        $("#nome").val(values[1]);
                                        $("#cognome").val(values[2]);
                                        $("#cellulare").val(values[3]);
                                        $("#localita").val(values[4]);
                                    } else {
                                        alert("Errore nel DB->" + data.result);
                                    }
                                }
                            },
                            error: function (xhttpjqr, err, data) {
                                alert(err);
                            },
                            complete: function (xhttpjqr, evtobj, data) {
                            }
                        });
                    }
                });
                $('#clienteTableSelection tbody').on('click', 'tr', function () {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                    } else {
                        CLIENTE_DATATABLE.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                    }
                });
                $("#search_cliente").on('shown.bs.modal', function () {
                    CLIENTE_DATATABLE.clear();
                    CLIENTE_DATATABLE.ajax.reload();
                });
                $('#clienteTableSelection tfoot th').each(function () {
                    var title = $(this).text();
                    $(this).html('<input type="text" placeholder="Search ' + title + '" />');
                });
                CLIENTE_DATATABLE = $('#clienteTableSelection').DataTable({
                    responsive: true,
                    processing: true,
                    serverSide: true,
                    ajax: {
                        url: "GetClienti",
                        type: "POST"
                    },
                    sDom: 'lrtip', //to hide global search input box.
                    language: {
                        lengthMenu: "Mostra _MENU_ elementi per pagina",
                        zeroRecords: "Nessuna corrispondenza - spiacente",
                        info: "Pagina numero _PAGE_ di _PAGES_",
                        infoEmpty: "Nessun elemento presente",
                        infoFiltered: "(filtrato da _MAX_ elementi totali)"
                    },
                    initComplete: function () {
                        this.api().columns().every(function () {
                            var that = this;
                            $('input', this.footer()).on('keyup change', function () {
                                if (that.search() !== this.value) {
                                    that
                                            .search(this.value)
                                            .draw();
                                }
                            });
                        });
                    }
                });

                //MAin and standard Jobs handlers
                $('#deleteJobButton').click(function (event) {
                    var p = {jobId: LAVORO_SELEZIONATO.split("_")[1], categoria: CATEGORIA_SELEZIONATA, tipo: TIPO_LAVORO_SELEZIONATO};
                    requestJson = JSON.stringify(p);
                    $.ajax({
                        url: "DeleteJob",
                        cache: false,
                        data: requestJson,
                        dataType: 'json',
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    $("#li_" + LAVORO_SELEZIONATO).remove();
                                } else {
                                    alert("Errore con il DB ->\n : [" + data.result + "]");
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {

                        }
                    });
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
                $("[id^='standard_']").click(function (event) {
                    LAVORO_SELEZIONATO = event.target.id;
                    TIPO_LAVORO_SELEZIONATO = "standard";
                });
                $("#addStandardJobsButton").click(function () {
                    if (PRATICA_SELEZIONATA === '-1') {
                        $("#notification_area").prepend(
                                "<div class='alert alert-danger alert-dismissable'>\n\
                                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                                    La pratica deve essere inizializzata. Inserire almeno i <b>dati di Arrivo<b/>.\n\
                                </div>"
                                );
                    }
                    var jobs = [];
                    $("#standardJobTableSelection tbody tr.selected").each(function (index, value) {
                        // Come referenza viene usato il codice SuperAssistenza e 
                        // non il relativo ID interno del DB.
                        jobs.push(value.childNodes[1].innerText);
                    });
                    var jobsCode = {praticaId: PRATICA_SELEZIONATA, codes: jobs};
                    requestJson = JSON.stringify(jobsCode);
                    $.ajax({
                        url: "AddStandardJobs",
                        cache: false,
                        data: requestJson,
                        dataType: 'json',
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    $.each(data.rows, function (index, value) {
                                        var items = value.split("#");
                                        $("#list_categoria_" + CATEGORIA_SELEZIONATA).prepend(
                                                "<div class='list-group-item' id='li_standard_" + items[0] + "' >\n\
                                                    <i class='fa fa-toggle-right fa-fw' id='standard_" + items[0] + "'></i>\n\
                                                    <span id='desc_standard_" + items[0] + "' >" + items[1] + "</span><span class='pull-right' id='standard_" + items[0] + "'>\n\
                                                        <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='standard_" + items[0] + "' data-target='#delete_job'>\n\
                                                            <i class='fa fa-times-circle fa-fw' id='standard_" + items[0] + "'></i>\n\
                                                        </button>\n\
                                                    </span>\n\
                                                </div>"
                                                );
                                        $("button#standard_" + items[0]).click(function (event) {
                                            LAVORO_SELEZIONATO = event.target.id;
                                            TIPO_LAVORO_SELEZIONATO = "standard";
                                        });
                                    });
                                } else {
                                    alert("Errore nel DB->" + data.result);
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {
                        }
                    });
                });

                //custom jobs event handler
                $("[id^='custom_']").click(function (event) {
                    LAVORO_SELEZIONATO = event.target.id;
                    TIPO_LAVORO_SELEZIONATO = "custom";
                    var text = $("#desc_" + LAVORO_SELEZIONATO).text().trim();
                    $("#editbox_customjob").val(text);
                });
                $("#editCustomJobButton").click(function (event) {
                    var eb = $.trim($("#editbox_customjob").val());
                    var cj = $("#desc_" + LAVORO_SELEZIONATO).text().trim().toString();
                    if (eb === cj) {
                        return;
                    }
                    var p = {jobId: LAVORO_SELEZIONATO.split("_")[1], descrizione: $("#editbox_customjob").val().trim()};
                    requestJson = JSON.stringify(p);
                    $.ajax({
                        url: "EditCustomJob",
                        cache: false,
                        data: requestJson,
                        dataType: 'json',
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                $("#desc_" + LAVORO_SELEZIONATO).text(data.descrizione);
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {
                        }
                    });
                });
                $("#addCustomJobButton").click(function (event) {
                    if (PRATICA_SELEZIONATA === '-1') {
                        $("#notification_area").prepend(
                                "<div class='alert alert-danger alert-dismissable'>\n\
                                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                                    La pratica deve essere inizializzata. Inserire almeno i <b>dati di Arrivo<b/>\n\
                                </div>"
                                );
                    }
                    var text = $("#editbox_addcustomjob").val().trim();
                    var p = {praticaId: PRATICA_SELEZIONATA, categoriaId: CATEGORIA_SELEZIONATA, descrizione: text};
                    requestJson = JSON.stringify(p);
                    $.ajax({
                        url: "AddCustomJob",
                        cache: false,
                        data: requestJson,
                        dataType: 'json',
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    //item custom template    
                                    $("#list_categoria_" + CATEGORIA_SELEZIONATA).prepend(
                                            "<div class='list-group-item' id='li_custom_" + data.jobId + "' >\n\
                                            <i class='fa fa-toggle-right fa-fw' id='custom_" + data.jobId + "'></i>\n\
                                                <span id='desc_custom_" + data.jobId + "' >" + data.descrizione + "</span>\n\
                                                <span class='pull-right' id='custom_" + data.jobId + "'>\n\
                                                    <button type='button' class='btn-xs btn-primary'  data-toggle='modal' id='custom_" + data.jobId + "' data-target='#edit_custom_job'>\n\
                                                            <i class='fa fa-edit fa-fw' id='custom_" + data.jobId + "'></i>\n\
                                                        </button>\n\
                                                        <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='custom_" + data.jobId + "' data-target='#delete_job'>\n\
                                                            <i class='fa fa-times-circle fa-fw' id='custom_" + data.jobId + "'></i>\n\
                                                        </button>\n\
                                                    </span>\n\
                                                </div>"
                                            );
                                    $("button#custom_" + data.jobId).click(function (event) {
                                        LAVORO_SELEZIONATO = event.target.id;
                                        TIPO_LAVORO_SELEZIONATO = "custom";
                                        var text = $("#desc_custom_" + LAVORO_SELEZIONATO).text().trim();
                                        $("#editbox_customjob").val(text);
                                    });
                                } else {
                                    alert(data.messaggio);
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {
                        }
                    });
                });

                //Main Buttons handlers.
                $("#button_save").click(function (event) {
                    var praticaId = $("#idPratica").val();
                    var arrivo = $("#arrivo").val();
                    var data_arrivo = $("#data_arrivo").val();
                    /*
                     if (arrivo.trim() === '') {
                     $("#notification_area").prepend(
                     "<div class='alert alert-danger alert-dismissable'>\n\
                     <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                     La pratica deve essere inizializzata. Inserire almeno i <b>dati di Arrivo<b/>.\n\
                     </div>"
                     );
                     return;
                     }
                     if (data_arrivo.trim() === '') {
                     $("#notification_area").prepend(
                     "<div class='alert alert-danger alert-dismissable'>\n\
                     <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                     La pratica deve essere inizializzata. Inserire almeno i <b>dati di Arrivo<b/>.\n\
                     </div>"
                     );
                     return;
                     }
                     */
                    if (arrivo.trim() === '') {
                        arrivo = getRandomArrivoCode();
                        $("#arrivo").val(arrivo);
                    }
                    if (data_arrivo.trim() === '') {
                        data_arrivo = getDataArrivo();
                        $("#data_arrivo").val(data_arrivo);
                    }
                    var uscita = $("#uscita").val();
                    var data_uscita = $("#data_uscita").val();
                    var veicoloId = $("#idVeicolo").val();
                    if (veicoloId.trim() === '-1') {
                        $("#notification_area").prepend(
                                "<div class='alert alert-danger alert-dismissable'>\n\
                                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                                     Deve essere scelto un <b>veicolo</b>.Crealo o sceglilo dalla lista\n\
                                </div>"
                                );
                        return;
                    }
                    var marca = $("#marca").val();
                    var modello = $("#modello").val();
                    var targa = $("#targa").val();
                    var kilometraggio = $("#kilometraggio").val();
                    var anno = $("#anno").val();
                    var tipo = $("select[name='tipo']").val();
                    var matricola = $("#matricola").val();
                    var ore = $("#ore").val();
                    var idCliente = $("#idCliente").val();
                    if (idCliente.trim() === '-1') {
                        $("#notification_area").prepend(
                                "<div class='alert alert-danger alert-dismissable'>\n\
                                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
                                     Deve essere scelto un <b>cliente</b>.Crealo o sceglilo dalla lista.\n\
                                </div>"
                                );
                        return;
                    }
                    var nome = $("#nome").val();
                    var cognome = $("#cognome").val();
                    var cellulare = $("#cellulare").val();
                    var localita = $("#localita").val();
                    var preventivo_lavori = $("#preventivo_lavori").val();
                    var preventivo_lavori_data = $("#preventivo_lavori_data").val();
                    var revisione_mctc = $("#revisione_mctc").val();
                    var revisione_mctc_data = $("#revisione_mctc_data").val();
                    var collaudo_usl = $("#collaudo_usl").val();
                    var collaudo_usl_data = $("#collaudo_usl_data").val();
                    var registro_di_controllo = $("#registro_di_controllo").val();
                    var lavori_segnalati = $("#lavori_segnalati").val();
                    var fattura = $("#fattura").val();
                    var fattura_data = $("#fattura_data").val();
                    var cliente_temp = $("#cliente_temp").val();
                    var veicolo_temp = $("#veicolo_temp").val();

                    var p = {
                        praticaId: praticaId,
                        arrivo: arrivo,
                        data_arrivo: data_arrivo,
                        uscita: uscita,
                        data_uscita: data_uscita,
                        veicoloId: veicoloId,
                        marca: marca,
                        modello: modello,
                        targa: targa,
                        kilometraggio: kilometraggio,
                        cliente_temporaneo: cliente_temp,
                        veicolo_temporaneo: veicolo_temp,
                        anno: anno,
                        tipo: tipo,
                        matricola: matricola,
                        ore: ore,
                        idCliente: idCliente,
                        nome: nome,
                        cognome: cognome,
                        cellulare: cellulare,
                        localita: localita,
                        preventivo_lavori: preventivo_lavori,
                        preventivo_lavori_data: preventivo_lavori_data,
                        revisione_mctc: revisione_mctc,
                        revisione_mctc_data: revisione_mctc_data,
                        collaudo_usl: collaudo_usl,
                        collaudo_usl_data: collaudo_usl_data,
                        registro_di_controllo: registro_di_controllo,
                        fattura: fattura,
                        fattura_data: fattura_data,
                        lavori_segnalati: lavori_segnalati
                    };
                    //l'encoding è necessario a causa del fatto che dei campi
                    //possono presentare dei caratteri mal codificabili.
                    requestJson = encodeURIComponent(JSON.stringify(p));
                    $.ajax({
                        url: "SavePratica",
                        cache: false,
                        data: requestJson,
                        dataType: 'json',
                        type: 'POST',
                        async: true,
                        success: function (data) {
                            if (data !== undefined) {
                                if (data.result === "ok") {
                                    $("#notification_area").prepend("<div class='alert alert-success alert-dismissable'>\n\
                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
" + data.messaggio.toString() + "\n\
</div>"
                                            );
                                    $("#idPratica").val(data.praticaId);
                                } else {
                                    $("#notification_area").prepend("<div class='alert alert-danger alert-dismissable'>\n\
                    <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
" + data.messaggio.toString() + "\n\
</div>"
                                            );
                                }
                            }
                        },
                        error: function (xhttpjqr, err, data) {
                            alert(err);
                        },
                        complete: function (xhttpjqr, evtobj, data) {

                        }
                    });
                });

                $("#button_export_material").click(function (event) {
                    if (PRATICA_SELEZIONATA === "-1") {
                        $("#notification_area").prepend(
                                "<div class='alert alert-danger alert-dismissable'>La pratica non è valida o è vuota.\n\
            <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>\n\
</div>");
                    }
                    ;
                    encodedData = encodeURIComponent(PRATICA_SELEZIONATA);
                    $("#exportInput").val(encodedData);
                    $("#exportForm").submit();
                    return;
                });
                // end $(document).ready
            });
        </script>
        <!-- 
        It needs to let the page scroll down after the BS modal has been
        opened.    
        -->
        <style>
            tfoot input {
                width: 100%;
                box-sizing: border-box;
            }
            body.modal-open {
                overflow: visible;
            }
            #wrapper {
                background-color: #ccccbe
            }
            #page-wrapper{
                background-color: #e9e3e4
            }
        </style>
    </head>
    <body>
        <%
            if (request.getParameter("praticaId").contains("-1")) {
                praticaInfos = new ArrayList<CompletePraticaInfo>();
                praticaInfos.add(CompletePraticaInfo.getEmptyCompletePraticaInfo());
            } else {
                praticaInfos = AmministrazioneUtils.GetAllPraticaInformation(Integer.parseInt(request.getParameter("praticaId")));
            }
            //categoriaInfos = AmministrazioneUtils.GetAllCategoryInformation(Integer.parseInt(request.getParameter("praticaId")));
        %>
        <div id="wrapper">

            <!-- Navigation -->
            <nav id="navigation" class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-brand" href="index.html"> Ciclope - SuperAssistenza</a>
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
                        <h1 class="page-header">Dati Pratica</h1>
                    </div>
                </div>
                <div class="row">
                    <div id="notification_area" class="col-lg-12">

                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-12">
                        <form id="exportForm" action="ExportMaterialePratica" role="form" method="POST">
                            <input id="exportInput" type="hidden" name="praticaId" value="" />
                        </form>
                    </div>                     
                </div>
                <form role="form" method="POST">
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="btn-group" style="margin-bottom:1em">
                                <%
                                    if (request.getParameter("newPratice").equalsIgnoreCase("true")) {
                                        out.println("<button type='button' id='button_save' class='btn btn-primary'>Crea e salva</button>");
                                    } else {
                                        out.println("<button type='button' id='button_save' class='btn btn-primary'>Salva modifiche</button>");
                                    }
                                %>
                                <button  type='button' id='button_export_material' class='btn btn-primary'>Esporta Materiale</button>
                                <!-- 
                                    Basta abilitare questa sezione per abilitare i pulsanti:
                                    gli handlers non ci sono ma potranno essere messi come feature aggiuntive...
                                    concentriamoci sulla parte di magazzino.
                                -->
                                <%--
                                <button type="button" id="button_export" class="btn btn-primary">Esporta pratica</button>
                                <button type="button" id="button_print" class="btn btn-primary">Stampa pratica</button>
                                <%
                                    if (request.getParameter("newPratice").equalsIgnoreCase("true")) {
                                        out.println("<button type=\"button\" id=\"button_delete\" class=\"btn btn-primary\" disabled>Elimina pratica</button>");
                                    } else {
                                        out.println("<button type=\"button\" id=\"button_delete\" class=\"btn btn-primary\">Elimina pratica</button>");
                                    }
                                %>
                                --%>                          

                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 ">
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-file fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#collapseDatiAggiuntiviPratica">Dati del veicolo per questa pratica</a>
                                    </div>                                    
                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse in" id="collapseDatiAggiuntiviPratica">
                                    <div class="panel-body">
                                        <div class="row">
                                            <div class="form-group">
                                                <div class="col-lg-6">
                                                    <label>Kilometraggio</label>
                                                    <input class="form-control" id="kilometraggio"  <% out.print("value=\"" + praticaInfos.get(0).getKilometraggioVeicolo() + "\""); %> >
                                                </div>
                                                <div class="col-lg-6">
                                                    <label>Ore</label>
                                                    <input class="form-control" id="ore" <% out.print("value=\"" + praticaInfos.get(0).getOreVeicolo() + "\""); %> >
                                                </div>                  
                                            </div>
                                            <!-- /.row -->
                                        </div>                                 
                                    </div>
                                </div>
                                <!-- /.panel-body -->
                            </div>                           
                        </div>
                        <div class="col-lg-6 ">
                            <div class="panel panel-warning">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-file fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#collapseDatiTemporaneiPratica">Dati forniti dal magazzino:</a>
                                    </div>                                    
                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse in" id="collapseDatiTemporaneiPratica">
                                    <div class="panel-body">
                                        <div class="row">
                                            <div class="form-group">
                                                <div class="col-lg-6">
                                                    <label>Info sul cliente:</label>
                                                    <input class="form-control" id="cliente_temp"  <% out.print("value=\"" + praticaInfos.get(0).getCliente_temporaneo() + "\""); %> >
                                                </div>
                                                <div class="col-lg-6">
                                                    <label>Info sulla riparazione:</label>
                                                    <input class="form-control" id="veicolo_temp" <% out.print("value=\"" + praticaInfos.get(0).getVeicolo_temporaneo() + "\""); %> >
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
                            <div class="panel panel-primary">
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
                                                    <input type="hidden" id="idPratica" name="idPratica" <% out.print("value=\"" + praticaInfos.get(0).getIdPratica() + "\""); %> />
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
                        <div class="col-lg-6">
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-file fw"></i>
                                        <a style="margin-left:1em"data-toggle="collapse"
                                           href="#collapseDatiChiusuraPratica">Dati di chiusura pratica</a>
                                    </div>                                    
                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse in" id="collapseDatiChiusuraPratica">
                                    <div class="panel-body">
                                        <div class="row">

                                            <div class="form-group">
                                                <div class="col-lg-6">
                                                    <label>Codice Uscita</label>
                                                    <input class="form-control" id="uscita" <% out.print("value=\"" + praticaInfos.get(0).getUscita() + "\""); %> >
                                                </div>
                                                <!-- /.col-lg-5 -->
                                                <div class="col-lg-6">
                                                    <label>Data Uscita</label>
                                                    <input class="form-control" id="data_uscita"  <% out.print("value=\"" + praticaInfos.get(0).getData_uscita() + "\""); %> >
                                                    <script>
                                                        $("#data_uscita").datepicker();
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
                                        <div class='pull-right'>
                                            <div class='btn-group'>
                                                <button type='button' class='btn btn-default btn-xs dropdown-toggle' data-toggle='dropdown'>
                                                    Azioni <span class='caret'></span>
                                                </button>
                                                <ul class='dropdown-menu pull-right' role='menu'>
                                                    <li>
                                                        <a href='' data-toggle='modal' data-target='#add_new_veicolo' >NUOVA piattaforma/veicolo</a>
                                                    </li>
                                                    <li style="margin-top: 0.5em">
                                                        <a href='' data-toggle='modal' data-target='#search_veicolo' >TROVA piattaforma/veicolo</a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>                                    
                                </div>
                                <!-- /.panel-heading -->
                                <div class="panel-collapse collapse in" id="collapseOggettoRiparazione">
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <input type="hidden" id="idVeicolo"  <% out.print("value=\"" + praticaInfos.get(0).getIdVeicolo() + "\""); %>>
                                            <label>Marca</label>
                                            <input class="form-control" id="marca"  <% out.print("value=\"" + praticaInfos.get(0).getMarcaVeicolo() + "\""); %> >
                                            <label>Modello</label>
                                            <input class="form-control" id="modello"  <% out.print("value=\"" + praticaInfos.get(0).getModelloVeicolo() + "\""); %> >
                                            <label>Targa</label>
                                            <input class="form-control" id="targa"  <% out.print("value=\"" + praticaInfos.get(0).getTargaVeicolo() + "\""); %> >                                       
                                            <label>Anno</label>
                                            <input class="form-control" id="anno"  <% out.print("value=\"" + praticaInfos.get(0).getAnnoVeicolo() + "\""); %> >
                                            <script>
                                                $("#anno").datepicker({
                                                    changeYear: true,
                                                    yearRange: "1930:2150",
                                                    dateFormat: "yy"
                                                });
                                            </script>
                                            <div class="form-group" style="margin-top:1em; margin-bottom: 1em" id="tipo">
                                                <label>Tipo:</label>
                                                <select class="form-control" style="margin-top:1em; margin-bottom: 1em" name="tipo">
                                                    <option id="tipo_ple" value="PLE" <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PLE") ? "selected=\"selected\"" : "")); %>>PLE</option>
                                                    <option id="tipo_pv" value="PV"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PL") ? "selected=\"selected\"" : "")); %>>PV</option>
                                                    <option id="tipo_autogru" value="AUTOGRU"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("AUTOGRU") ? "selected=\"selected\"" : "")); %>>AUTOGRU</option>
                                                    <option id="tipo_caricatore" value="CARICATORE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE") ? "selected=\"selected\"" : "")); %>>CARICATORE</option>
                                                    <option id="tipo_scarrabile" value="SCARRABILE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("SCARRABILE") ? "selected=\"selected\"" : "")); %>>SCARRABILE</option>
                                                    <option id="tipo_caricatore_fisso" value="CARICATORE FISSO"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE FISSO") ? "selected=\"selected\"" : "")); %>>CARICATORE FISSO</option>
                                                    <option id="tipo_caricatore_scarrabile" value="CARICATORE SCARRABILE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE SCARRABILE") ? "selected=\"selected\"" : "")); %>>CARICATORE SCARRABILE</option>
                                                    <option id="tipo_compattatore" value="COMPATTATORE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("COMPATTATORE") ? "selected=\"selected\"" : "")); %>>COMPATTATORE</option>
                                                    <option id="tipo_rimorchio" value="RIMORCHIO"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("RIMORCHIO") ? "selected=\"selected\"" : "")); %>>RIMORCHIO</option>
                                                </select>
                                            </div>
                                            <!--                                            <div class="form-group" style="margin-top:1em; margin-bottom: 1em" id="tipo">
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
                                                                                        </div>-->
                                            <label>Matricola</label>
                                            <input class="form-control" id="matricola" <% out.print("value=\"" + praticaInfos.get(0).getMatricolaVeicolo() + "\""); %> >                                                                                   </div>
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
                                        <div class='pull-right'>
                                            <div class='btn-group'>
                                                <button type='button' class='btn btn-default btn-xs dropdown-toggle' data-toggle='dropdown'>
                                                    Azioni <span class='caret'></span>
                                                </button>
                                                <ul class='dropdown-menu pull-right' role='menu'>
                                                    <li>
                                                        <a href='' data-toggle='modal' data-target='#add_new_cliente' >NUOVO cliente</a>
                                                    </li>
                                                    <li style="margin-top:0.5em">
                                                        <a href='' data-toggle='modal' data-target='#search_cliente' >TROVA cliente</a>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>                                   
                                </div>
                                <!-- /.panel-heading -->
                                <div id="collapseCliente" class="panel-collapse collapse in">
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
                                        <a style="margin-left:1em" data-toggle="collapse"                                            
                                           href="#collapseControlliAccettazione">Controlli ed Accettazione</a>
                                    </div>
                                </div>
                                <!-- /.panel-heading -->
                                <div id="collapseControlliAccettazione" class="panel-collapse collapse in">
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
                                                    <label>Eseguito il:</label>
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
                                                    <label>Eseguito il:</label>
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
                                                    <label>Eseguito il:</label>
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
                                                    <label>Eseguito il:</label>
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
                    <!-- Lavori da effettuare --> 
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="panel panel-default panel-group">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <i class="fa fa-check-square fw"></i>
                                        <a style="margin-left:1em" data-toggle="collapse"
                                           href="#lavoriDaEffettuare">Lavori Effettuati</a>
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
                                                out.println("                               <a href='' data-toggle='modal' data-target='#add_standard_job' >Aggingi lavoro standard</a>");
                                                out.println("                           </li>");
                                                out.println("                           <li>");
                                                out.println("                               <a href='' data-toggle='modal' data-target='#add_custom_job' >Aggingi lavoro personalizzato</a>");
                                                out.println("                           </li>");
                                                out.println("                           <li class='divider'></li>");
                                                out.println("                           <li>");
                                                out.println("                               <a href=''>Elimina tutto</a>");
                                                out.println("                           </li>");
                                                out.println("                       </ul>");
                                                out.println("                   </div>");
                                                out.println("               </div>");
                                                out.println("           </div>");
                                                out.println("       </div>");
                                                out.println("       <div id='categoria_" + cci.getIdCategoria() + "' class='panel-collapse collapse in'>");
                                                out.println("           <div class='panel-body'>");
                                                out.println("               <div class='list-group' id='list_categoria_" + cci.getIdCategoria() + "' >");
                                                for (TipoLavoroPratica tlp
                                                        : AmministrazioneUtils.GetAllLavori(Integer.parseInt(request.getParameter("praticaId")), Integer.parseInt(cci.getIdCategoria()))) {
                                                    if (tlp.getTipo().equalsIgnoreCase("S")) {
                                                        //item standard template
                                                        out.println("<div class='list-group-item' id='li_standard_" + tlp.getIdLavoro() + "' >");
                                                        out.println("   <i class='fa fa-toggle-right fa-fw' id='standard_" + tlp.getIdLavoro() + "'></i>");
                                                        out.println("   <span id='desc_standard_" + tlp.getIdLavoro() + "' >" + tlp.getDescrizione() + "</span>");
                                                        out.println("   <span class='pull-right' id='standard_" + tlp.getIdLavoro() + "'>");
                                                        out.println("       <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='standard_" + tlp.getIdLavoro() + "' data-target='#delete_job'>");
                                                        out.println("           <i class='fa fa-times-circle fa-fw' id='standard_" + tlp.getIdLavoro() + "'></i>");
                                                        out.println("       </button>");
                                                        out.println("   </span>");
                                                        out.println("</div>");
                                                    } else {
                                                        //item custom template
                                                        out.println("<div class='list-group-item' id='li_custom_" + tlp.getIdLavoro() + "' >");
                                                        out.println("   <i class='fa fa-toggle-right fa-fw' id='custom_" + tlp.getIdLavoro() + "'></i>");
                                                        out.println("   <span id='desc_custom_" + tlp.getIdLavoro() + "' >" + tlp.getDescrizione() + "</span>");
                                                        out.println("   <span class='pull-right' id='custom_" + tlp.getIdLavoro() + "'>");
                                                        out.println("       <button type='button' class='btn-xs btn-primary'  data-toggle='modal' id='custom_" + tlp.getIdLavoro() + "' data-target='#edit_custom_job'>");
                                                        out.println("           <i class='fa fa-edit fa-fw' id='custom_" + tlp.getIdLavoro() + "'></i>");
                                                        out.println("       </button>");
                                                        out.println("       <button type='button' class='btn-xs btn-danger' data-toggle='modal' id='custom_" + tlp.getIdLavoro() + "' data-target='#delete_job'>");
                                                        out.println("           <i class='fa fa-times-circle fa-fw' id='custom_" + tlp.getIdLavoro() + "'></i>");
                                                        out.println("       </button>");
                                                        out.println("   </span>");
                                                        out.println("</div>");
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
                        <button type="button" id="deleteJobButton" class="btn btn-primary" data-dismiss="modal">Togli pure</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
        <div class="modal fade" id="edit_custom_job" tabindex="-1" role="dialog" aria-labelledby="editJobLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="editJobLabel">Modifica lavoro personalizzato</h4>
                    </div>
                    <div class="modal-body">
                        <label for="edit">Inserisci la nuova descrizione:</label>
                        <input class="form-control" id="editbox_customjob" value="">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="editCustomJobButton" class="btn btn-primary" data-dismiss="modal">Salva modifica</button>
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
                        <label for="edit">Inserisci la descrizione:</label>
                        <input class="form-control" id="editbox_addcustomjob" value="">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="addCustomJobButton" class="btn btn-primary" data-dismiss="modal">Aggiungi</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <!-- /.modal -->
        <div class="modal fade" id="add_standard_job" tabindex="-1" role="dialog" aria-labelledby="addStandardJobLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="addCustomJobLabel" >Inserimento nuovo lavoro standard</h3>
                    </div>
                    <div class="modal-body">
                        <p style="text-align:center; margin:1em " >Seleziona i lavori da aggiungere a questa categoria</p>
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
                        <button type="button" id="addStandardJobsButton" class="btn btn-primary" data-dismiss="modal">Aggiungi</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <div class="modal fade" id="search_veicolo" tabindex="-1" role="dialog" aria-labelledby="searchVeicoloLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="searchVeicoloLabel" >Veicoli in archivio</h3>
                    </div>
                    <div class="modal-body">
                        <p style="text-align:center; margin:1em " >Seleziona il veicolo da inserire nella pratica</p>
                        <table id="veicoloTableSelection" class="table table-striped table-bordered" cellspacing='0' width="100%">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Marca</th>
                                    <th>Modello</th>
                                    <th>Targa</th>
                                    <th>Tipo</th>
                                    <th>Matricola</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                            <tfoot>
                                <tr>
                                    <th>Id</th>
                                    <th>Marca</th>
                                    <th>Modello</th>
                                    <th>Targa</th>
                                    <th>Tipo</th>
                                    <th>Matricola</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="setVeicoloInPraticaButton" class="btn btn-primary" data-dismiss="modal">Seleziona</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <div class="modal fade" id="add_new_veicolo" tabindex="-1" role="dialog" aria-labelledby="addNewVeicoloLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="addNewVeicoloLabel" >Creazione nuova Piattaforma/Veicolo:</h3>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Marca</label>
                            <input class="form-control" id="marca1" val="" >
                            <label>Modello</label>
                            <input class="form-control" id="modello1"  val="" >
                            <label>Targa</label>
                            <input class="form-control" id="targa1" val="" >
                            <label>Anno</label>
                            <input class="form-control" id="anno1"  val="">
                            <script>
                                $("#anno1").datepicker({
                                    changeYear: true,
                                    yearRange: "1930:2150",
                                    dateFormat: "yy"
                                });
                            </script>
                            <div class="form-group" style="margin-top:1em; margin-bottom: 1em" id="tipo">
                                <label>Tipo:</label>
                                <select class="form-control" style="margin-top:1em; margin-bottom: 1em" name="tipo1">
                                    <option id="tipo_ple" value="PLE" <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PLE") ? "selected=\"selected\"" : "")); %>>PLE</option>
                                    <option id="tipo_pv" value="PV"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("PL") ? "selected=\"selected\"" : "")); %>>PV</option>
                                    <option id="tipo_autogru" value="AUTOGRU"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("AUTOGRU") ? "selected=\"selected\"" : "")); %>>AUTOGRU</option>
                                    <option id="tipo_caricatore" value="CARICATORE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE") ? "selected=\"selected\"" : "")); %>>CARICATORE</option>
                                    <option id="tipo_scarrabile" value="SCARRABILE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("SCARRABILE") ? "selected=\"selected\"" : "")); %>>SCARRABILE</option>
                                    <option id="tipo_caricatore_fisso" value="CARICATORE FISSO"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE FISSO") ? "selected=\"selected\"" : "")); %>>CARICATORE FISSO</option>
                                    <option id="tipo_caricatore_scarrabile" value="CARICATORE SCARRABILE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("CARICATORE SCARRABILE") ? "selected=\"selected\"" : "")); %>>CARICATORE SCARRABILE</option>
                                    <option id="tipo_compattatore" value="COMPATTATORE"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("COMPATTATORE") ? "selected=\"selected\"" : "")); %>>COMPATTATORE</option>
                                    <option id="tipo_rimorchio" value="RIMORCHIO"  <% out.print((praticaInfos.get(0).getTipoVeicolo().equalsIgnoreCase("RIMORCHIO") ? "selected=\"selected\"" : ""));%>>RIMORCHIO</option>
                                </select>
                            </div>
                            
                            <label>Matricola</label>
                            <input class="form-control" id="matricola1" value="" >
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="addNewVeicoloButton" class="btn btn-primary" data-dismiss="modal">Crea</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <div class="modal fade" id="search_cliente" tabindex="-1" role="dialog" aria-labelledby="searchClienteLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="searchClienteLabel" >Clienti in archivio</h3>
                    </div>
                    <div class="modal-body">
                        <p style="text-align:center; margin:1em " >Seleziona il cliente da inserire nella pratica</p>
                        <table id="clienteTableSelection" class="table table-striped table-bordered" cellspacing='2' width="100%">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Nome</th>
                                    <th>Cognome</th>
                                    <th>Cellulare</th>
                                    <th>Localita</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                            <tfoot>
                                <tr>
                                    <th>Id</th>
                                    <th>Nome</th>
                                    <th>Cognome</th>
                                    <th>Cellulare</th>
                                    <th>Localita</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="setClienteInPraticaButton" class="btn btn-primary" data-dismiss="modal">Seleziona</button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <div class="modal fade" id="add_new_cliente" tabindex="-1" role="dialog" aria-labelledby="addNewClienteLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h3 class="modal-title" id="addNewClienteLabel" >Creazione nuova cliente:</h3>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Nome</label>
                            <input class="form-control" id="nome1" value="" >
                            <label>Cognome</label>
                            <input class="form-control" id="cognome1" value="" >
                            <label>Celluare</label>
                            <input class="form-control" id="cellulare1" value="" >
                            <label>Localita</label>
                            <input class="form-control" id="localita1" value="" >
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Indietro</button>
                        <button type="button" id="addNewClienteButton" class="btn btn-primary" data-dismiss="modal">Crea</button>
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
    </body>
</html>
