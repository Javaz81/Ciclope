/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets.utils;

/**
 *
 * @author andrea
 */
public class QuerySupportUtils {
    public static String escapeSQL(String notEscapedSQLString){
        StringBuilder sb = new StringBuilder(notEscapedSQLString.length());
        char [] charArray = notEscapedSQLString.toCharArray();
        for ( int i = 0; i < charArray.length; i++){
            if(isContained(charArray[i])){
                sb.append("\\").append(charArray[i]);
            }else{
                sb.append(charArray[i]);                                        
            }
        }
        return sb.toString() ;
    }
    private static final String ESCAPE_CHARS = "&*@[]{}\\^:=!/<>-()%+?;|,$.#_";
    private static boolean isContained(char c) {
        return ESCAPE_CHARS.contains(Character.toString(c));
    }
}
