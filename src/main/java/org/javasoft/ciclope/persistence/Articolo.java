package org.javasoft.ciclope.persistence;
// Generated 27-apr-2016 0.09.05 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Articolo generated by hbm2java
 */
public class Articolo  implements java.io.Serializable {


     private int idArticolo;
     private String descrizione;
     private int scortaRimanente;
     private int scortaMinima;
     private String unitaDiMisura;
     private Integer approvvigionamento;
     private Set materialepraticas = new HashSet(0);

    public Articolo() {
    }

	
    public Articolo(int idArticolo, String descrizione, int scortaRimanente, int scortaMinima, String unitaDiMisura) {
        this.idArticolo = idArticolo;
        this.descrizione = descrizione;
        this.scortaRimanente = scortaRimanente;
        this.scortaMinima = scortaMinima;
        this.unitaDiMisura = unitaDiMisura;
    }
    public Articolo(int idArticolo, String descrizione, int scortaRimanente, int scortaMinima, String unitaDiMisura, Integer approvvigionamento, Set materialepraticas) {
       this.idArticolo = idArticolo;
       this.descrizione = descrizione;
       this.scortaRimanente = scortaRimanente;
       this.scortaMinima = scortaMinima;
       this.unitaDiMisura = unitaDiMisura;
       this.approvvigionamento = approvvigionamento;
       this.materialepraticas = materialepraticas;
    }
   
    public int getIdArticolo() {
        return this.idArticolo;
    }
    
    public void setIdArticolo(int idArticolo) {
        this.idArticolo = idArticolo;
    }
    public String getDescrizione() {
        return this.descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public int getScortaRimanente() {
        return this.scortaRimanente;
    }
    
    public void setScortaRimanente(int scortaRimanente) {
        this.scortaRimanente = scortaRimanente;
    }
    public int getScortaMinima() {
        return this.scortaMinima;
    }
    
    public void setScortaMinima(int scortaMinima) {
        this.scortaMinima = scortaMinima;
    }
    public String getUnitaDiMisura() {
        return this.unitaDiMisura;
    }
    
    public void setUnitaDiMisura(String unitaDiMisura) {
        this.unitaDiMisura = unitaDiMisura;
    }
    public Integer getApprovvigionamento() {
        return this.approvvigionamento;
    }
    
    public void setApprovvigionamento(Integer approvvigionamento) {
        this.approvvigionamento = approvvigionamento;
    }
    public Set getMaterialepraticas() {
        return this.materialepraticas;
    }
    
    public void setMaterialepraticas(Set materialepraticas) {
        this.materialepraticas = materialepraticas;
    }




}

