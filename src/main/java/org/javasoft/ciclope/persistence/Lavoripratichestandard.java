package org.javasoft.ciclope.persistence;
// Generated 10-nov-2016 22.50.10 by Hibernate Tools 4.3.1

/**
 * Lavoripratichestandard generated by hbm2java
 */
public class Lavoripratichestandard  implements java.io.Serializable {


     private Integer id;
     private Pratica pratica;
     private Tipolavoro tipolavoro;

    public Lavoripratichestandard() {
    }

    public Lavoripratichestandard(Pratica pratica, Tipolavoro tipolavoro) {
       this.pratica = pratica;
       this.tipolavoro = tipolavoro;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Pratica getPratica() {
        return this.pratica;
    }
    
    public void setPratica(Pratica pratica) {
        this.pratica = pratica;
    }
    public Tipolavoro getTipolavoro() {
        return this.tipolavoro;
    }
    
    public void setTipolavoro(Tipolavoro tipolavoro) {
        this.tipolavoro = tipolavoro;
    }




}


