/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 *
 * @author andrea
 */
public class TimeStampConverter {
    //giorno

    public static final Pattern COMMONDATE_PATTERN_0 = Pattern.compile("^\\d{1,2}$");
    public static final Pattern COMMONDATE_PATTERN_1 = Pattern.compile("^\\d{1,2}[\\/]$");
    //giorno-mese
    public static final Pattern COMMONDATE_PATTERN_2 = Pattern.compile("^\\d{1,2}[\\/]\\d{1,2}$");
    public static final Pattern COMMONDATE_PATTERN_3 = Pattern.compile("^\\d{1,2}[\\/]\\d{1,2}[\\/]$");
    //giorno-mese-anno(3 cifre)
    public static final Pattern COMMONDATE_PATTERN_4 = Pattern.compile("^\\d{1,2}[\\/]\\d{1,2}[\\/]\\d{1,3}$");
    //Pattern totale
    public static final Pattern COMMONDATE_PATTERN_5 = Pattern.compile("^\\d{1,2}[\\/]\\d{1,2}[\\/]\\d{4}$");
    //Patterns array
    public static final int COMMONDATE_PATTERNS_NUM = 6;
    public static final Pattern[] COMMONDATE_PATTERNS = new Pattern[COMMONDATE_PATTERNS_NUM];
    //MySQL and Common date format 
    public static final SimpleDateFormat MYSQL_COMMONDATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat COMMONDATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    static {
        COMMONDATE_PATTERNS[0] = COMMONDATE_PATTERN_0;
        COMMONDATE_PATTERNS[1] = COMMONDATE_PATTERN_1;
        COMMONDATE_PATTERNS[2] = COMMONDATE_PATTERN_2;
        COMMONDATE_PATTERNS[3] = COMMONDATE_PATTERN_3;
        COMMONDATE_PATTERNS[4] = COMMONDATE_PATTERN_4;
        COMMONDATE_PATTERNS[5] = COMMONDATE_PATTERN_5;
    }

    /**
     * Cerca di creare una stringa rappresentante le condizioni di match in base
     * al parametro commonDate che può essere in un qualsiasi formato. Il
     * parametro commonDate può combaciare con al massimo una sola regola
     *
     * @param commonDate Una stringa rappresentante le condizioni per il match
     * della data passata(se valida).
     * @param mysqlDateColumnName Il nome della colonna rappresentante la data 
     * all'interno della sua tabella nel database MySQL.
     * @return Restituisce le condizioni di matching della data dedotta dal 
     * parametro <em>commonDate</em>.
     */
    public static String commonDateToMySQLDate(String commonDate, String mysqlDateColumnName) {
        int validPattern = -1;
        String invalidCommonDate = mysqlDateColumnName.concat(" LIKE '").concat(commonDate).concat("%'");
        for (int i = 0; i < COMMONDATE_PATTERNS_NUM; i++) {
            if (COMMONDATE_PATTERNS[i].matcher(commonDate).matches()) {
                validPattern = i;
                break;
            }
        }
        if (validPattern == -1) {
            return invalidCommonDate;
        } else {
            int day,month,year;
            String[] spl;
            switch (validPattern) {
                case 0: {
                    day = Integer.parseInt(commonDate);
                    return "DAYOFMONTH(".concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(day));
                }
                case 1: {
                    commonDate = commonDate.replace("/", "");
                    day = Integer.parseInt(commonDate);
                    return "DAYOFMONTH(".concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(day));
                }
                case 2: {
                    spl = commonDate.split("/");
                    day = Integer.parseInt(spl[0]);
                    month = Integer.parseInt(spl[1]);
                    return "DAYOFMONTH(".concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(day)).concat(" AND ")
                            .concat("MONTH(").concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(month));
                }
                case 3: {
                    spl = commonDate.split("/");
                    day = Integer.parseInt(spl[0]);
                    month = Integer.parseInt(spl[1]);
                    return "DAYOFMONTH(".concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(day)).concat(" AND ")
                            .concat("MONTH(").concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(month));
                }
                case 4: {
                    spl = commonDate.split("/");
                    day = Integer.parseInt(spl[0]);
                    month = Integer.parseInt(spl[1]);
                    year = Integer.parseInt(spl[2]);
                    year += 2000;
                    return "DAYOFMONTH(".concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(day)).concat(" AND ")
                            .concat("MONTH(").concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(month)).concat(" AND ")
                            .concat("YEAR(").concat(mysqlDateColumnName).concat(") = ").concat(Integer.toString(year));
                }
                case 5: {
                    try {
                        return mysqlDateColumnName.concat(" LIKE '").concat(MYSQL_COMMONDATE_FORMAT.format(COMMONDATE_FORMAT.parse(commonDate))).concat("'");
                    } catch (ParseException ex) {
                        return invalidCommonDate;
                    }
                }
                default:
                    return invalidCommonDate;
            }
        }
    }
}
