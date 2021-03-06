/*
 * JTSage-DateBox-4.0.0
 * For: {"jqm":"1.4.5","bootstrap":"3.3.5"}
 * Date: Mon Jan 11 2016 16:09:26 UTC
 * http://dev.jtsage.com/DateBox/
 * https://github.com/jtsage/jquery-mobile-datebox
 *
 * Copyright 2010, 2016 JTSage. and other contributors
 * Released under the MIT license.
 * https://github.com/jtsage/jquery-mobile-datebox/blob/master/LICENSE.txt
 *
 */
$.extend( $.mobile.datebox.prototype.options.lang, { "it" : {
        "setDateButtonLabel": "Imposta data",
	"setTimeButtonLabel": "Imposta ora",
	"setDurationButtonLabel": "Setta Durata",
	"todayButtonLabel": "Vai ad oggi",
	"titleDateDialogLabel": "Scegli data",
	"titleTimeDialogLabel": "Scegli ora",
	"daysOfWeek": [
		"Domenica",
		"Lunedì",
		"Martedì",
		"Mercoledì",
		"Giovedì",
		"Venerdì",
		"Sabato"
	],
	"daysOfWeekShort": [
		"Do",
		"Lu",
		"Ma",
		"Me",
		"Gi",
		"Ve",
		"Sa"
	],
	"monthsOfYear": [
		"Gennaio",
		"Febbraio",
		"Marzo",
		"Aprile",
		"Maggio",
		"Giugno",
		"Luglio",
		"Agosto",
		"Settembre",
		"Ottobre",
		"Novembre",
		"Dicembre"
	],
	"monthsOfYearShort": [
		"Gen",
		"Feb",
		"Mar",
		"Apr",
		"Mag",
		"Giu",
		"Lug",
		"Ago",
		"Set",
		"Ott",
		"Nov",
		"Dic"
	],
	"durationLabel": [
		"Giorni",
		"Ore",
		"Minuti",
		"Secondi"
	],
	"durationDays": [
		"Giorno",
		"Giorni"
	],
	"tooltip": "Apri Selettore Data",
	"nextMonth": "Mese successivo",
	"prevMonth": "Mese precedente",
	"timeFormat": 12,
	"headerFormat": "%A %-d %B %Y",
	"dateFieldOrder": [
		"d",
		"m",
		"y"
	],
	"timeFieldOrder": [
		"h",
		"i",
		"a"
	],
	"slideFieldOrder": [
		"y",
		"m",
		"d"
	],
	"dateFormat": "%d/%m/%Y",
	"useArabicIndic": false,
	"isRTL": false,
	"calStartDay": 0,
	"clearButton": "Pulisci",
	"durationOrder": [
		"d",
		"h",
		"i",
		"s"
	],
	"meridiem": [
		"AM",
		"PM"
	],
	"timeOutput": "%l:%M %p",
	"durationFormat": "%Dd %DA, %Dl:%DM:%DS",
	"calDateListLabel": "Altre date",
	"calHeaderFormat": "%B %Y",
	"tomorrowButtonLabel": "Vai a domani"
}});
$.extend( $.mobile.datebox.prototype.options, {
    useLang: "it"
});
