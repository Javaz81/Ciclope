package org.javasoft.ciclope.persistence;
// Generated 27-apr-2016 0.09.05 by Hibernate Tools 4.3.1



/**
 * LavoropraticaId generated by hbm2java
 */
public class LavoropraticaId  implements java.io.Serializable {


     private int pratica;
     private int tipolavoro;

    public LavoropraticaId() {
    }

    public LavoropraticaId(int pratica, int tipolavoro) {
       this.pratica = pratica;
       this.tipolavoro = tipolavoro;
    }
   
    public int getPratica() {
        return this.pratica;
    }
    
    public void setPratica(int pratica) {
        this.pratica = pratica;
    }
    public int getTipolavoro() {
        return this.tipolavoro;
    }
    
    public void setTipolavoro(int tipolavoro) {
        this.tipolavoro = tipolavoro;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof LavoropraticaId) ) return false;
		 LavoropraticaId castOther = ( LavoropraticaId ) other; 
         
		 return (this.getPratica()==castOther.getPratica())
 && (this.getTipolavoro()==castOther.getTipolavoro());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getPratica();
         result = 37 * result + this.getTipolavoro();
         return result;
   }   


}

