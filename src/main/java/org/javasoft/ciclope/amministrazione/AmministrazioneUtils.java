/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.amministrazione;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.persistence.Personale;
import org.javasoft.ciclope.servlets.utils.DateUtils;
import org.javasoft.ciclope.servlets.utils.SessionUtils;
import org.json.simple.JSONObject;

/**
 *
 * @author andrea
 */
public class AmministrazioneUtils {

    /**
     * Crea la lista di tutte le informazioni della pratica, clienti, veicoli e
     * lavori annessi compresi.
     *
     * @param pratica_id
     * @return Tutte le informazioni sulla pratica.
     */
    public static List<CompletePraticaInfo> GetAllPraticaInformation(int pratica_id) {
        CompletePraticaInfo c = null;
        Session s = null;
        Transaction t = null;
        ArrayList<CompletePraticaInfo> praticaInfos = null;
        try {
            s = HibernateUtil.getSessionFactory().getCurrentSession();
            t = s.getTransaction();
            t.begin();
            String json = null;
            JSONObject obj = null;
            Query q = null;

            q = s.createSQLQuery("select *\n"
                    + "from ciclope.pratica\n"
                    + "inner join ciclope.cliente on Cliente_idCliente = idCliente\n"
                    + "inner join ciclope.veicolo on Veicolo = idVeicolo\n"
                    + "where ciclope.pratica.idPratica = " + pratica_id
            );

            List<Object[]> aicrecs = q.list();
            t.commit();
            praticaInfos = new ArrayList<CompletePraticaInfo>(aicrecs.size());
            for (Object[] ob : aicrecs) {
                c = new CompletePraticaInfo();
                c.setIdPratica(ob[0].toString());
                c.setArrivo(ob[1].toString());
                c.setData_arrivo(DateUtils.formatDateForAdministration(((Date) ob[2]), Locale.ITALY));
                c.setUscita(ob[3] == null ? "" : ob[3].toString());
                c.setData_uscita(ob[4] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[4]), Locale.ITALY));
                c.setLavori_segnalati(ob[5] == null ? "" : ob[5].toString());
                c.setNumero_fattura(ob[6] == null ? "" : ob[6].toString());
                c.setData_fattura(ob[7] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[7]), Locale.ITALY));
                c.setPreventivo_lavori(ob[8] == null ? "" : ob[8].toString().equals("1") ? "SI" : "NO");
                c.setRevisione_mctc(ob[9] == null ? "NO" : ob[9].toString().equals("1") ? "SI" : "NO");
                c.setCollaudo_usl(ob[10] == null ? "NO" : ob[10].toString().equals("1") ? "SI" : "NO");
                c.setRegistro_di_controllo(ob[11] == null ? "NO" : ob[11].toString().equals("1") ? "SI" : "NO");
                c.setPreventivo_lavori_data(ob[12] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[12]), Locale.ITALY));
                c.setRevisione_mctc_data(ob[13] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[13]), Locale.ITALY));
                c.setCollaudo_usl_data(ob[14] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[14]), Locale.ITALY));
                c.setRegistro_di_controllo_data(ob[15] == null ? "" : DateUtils.formatDateForAdministration(((Date) ob[15]), Locale.ITALY));
                c.setIdCliente(ob[16] == null ? "" : ob[16].toString());
                c.setIdVeicolo(ob[17] == null ? "" : ob[17].toString());
                c.setOreVeicolo(ob[18] == null ? "" : ob[18].toString());
                c.setKilometraggioVeicolo(ob[19] == null ? "" : ob[19].toString());
                c.setCliente_temporaneo(ob[20] == null ? "" : ob[20].toString());
                c.setVeicolo_temporaneo(ob[21] == null ? "" : ob[21].toString());
                c.setNomeCliente(ob[23] == null ? "" : ob[23].toString());
                c.setCognomeCliente(ob[24] == null ? "" : ob[24].toString());
                c.setCellulareCliente(ob[25] == null ? "" : ob[25].toString());
                c.setLocalitaCliente(ob[26] == null ? "" : ob[26].toString());
                c.setMarcaVeicolo(ob[28] == null ? "" : ob[28].toString());
                c.setModelloVeicolo(ob[29] == null ? "" : ob[29].toString());
                c.setTargaVeicolo(ob[30] == null ? "" : ob[30].toString());
                c.setAnnoVeicolo(ob[31] == null ? "" : ob[31].toString());
                c.setTipoVeicolo(ob[32] == null ? "" : ob[32].toString());
                c.setMatricolaVeicolo(ob[33] == null ? "" : ob[33].toString());
                praticaInfos.add(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return praticaInfos;
    }

    public static List<CompleteCategoriaInfo> GetAllCategoriaLavori() {
        CompleteCategoriaInfo c = null;
        Session s = null;
        Transaction t = null;
        ArrayList<CompleteCategoriaInfo> categoriaInfos = null;
        try {
            s = HibernateUtil.getSessionFactory().getCurrentSession();
            t = s.getTransaction();
            t.begin();
            String json = null;
            JSONObject obj = null;
            Query q = null;

            q = s.createSQLQuery("SELECT * "
                    + "FROM ciclope.categoriatipolavoro "
                    + "ORDER BY idCategoriaTipoLavoro"
            );

            List<Object[]> aicrecs = q.list();
            t.commit();
            categoriaInfos = new ArrayList<CompleteCategoriaInfo>(aicrecs.size());
            for (Object[] ob : aicrecs) {
                c = new CompleteCategoriaInfo();
                c.setIdCategoria(ob[0].toString());
                c.setNome(ob[1].toString());
                categoriaInfos.add(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return categoriaInfos;
    }
    public static List<Personale> GetAllOperatori(){
        Personale c = null;
        Session s = null;
        Transaction t = null;
        ArrayList<Personale> personale = null;
        try {
            s = HibernateUtil.getSessionFactory().getCurrentSession();
            t = s.getTransaction();
            t.begin();
            String json = null;
            JSONObject obj = null;
            Query q = null;

            q = s.createSQLQuery("SELECT * "
                    + "FROM ciclope.personale "
                    + "ORDER BY Nome"
            ).addEntity(Personale.class);

            personale = (ArrayList<Personale>) q.list();
            t.commit();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return personale;
    }
    public static List<TipoLavoroPratica> GetAllLavori(int praticaId, int categoriaId) {
        TipoLavoroPratica c = null;
        Session s = null;
        Transaction t = null;
        ArrayList<TipoLavoroPratica> tipoLavoriInfos = null;
        try {
            s = HibernateUtil.getSessionFactory().getCurrentSession();
            t = s.getTransaction();
            t.begin();
            String q1 = "SELECT * \n"
                    + "FROM ciclope.lavoripratichestandard\n"
                    + "INNER JOIN ciclope.tipolavoro\n"
                    + "ON ciclope.lavoripratichestandard.tipolavoro = ciclope.tipolavoro.idTipoLavoro\n"
                    + "WHERE ciclope.lavoripratichestandard.pratica = " + praticaId + " AND ciclope.tipolavoro.categoria = " + categoriaId;
            String q2 = "SELECT * \n"
                    + "FROM ciclope.lavoripratichecustom\n"
                    + "where ciclope.lavoripratichecustom.pratica = " + praticaId + " AND ciclope.lavoripratichecustom.categoria = " + categoriaId;

            Query q = s.createSQLQuery(q1);
            List<Object[]> aicrecs = q.list();

            tipoLavoriInfos = new ArrayList<TipoLavoroPratica>(aicrecs.size());
            for (Object[] ob : aicrecs) {
                c = new TipoLavoroPratica();
                c.setIdLavoro(ob[0].toString());
                c.setDescrizione(ob[6].toString());
                c.setTipo("S");
                tipoLavoriInfos.add(c);
            }
            q = s.createSQLQuery(q2);
            aicrecs = q.list();
            for (Object[] ob : aicrecs) {
                c = new TipoLavoroPratica();
                c.setIdLavoro(ob[0].toString());
                c.setDescrizione(ob[3].toString());
                c.setTipo("C");
                tipoLavoriInfos.add(c);
            }
            t.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return tipoLavoriInfos;
    }

    public static List<DayHours> GetTotalHourWorkedPerDay(int operatore, int ultimi_n_giorni) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        Calendar localCalendar = DateUtils.GetLocalCalendar();
        Date start, stop;
        stop = localCalendar.getTime();
        localCalendar.add(Calendar.DATE, -(ultimi_n_giorni));
        start = localCalendar.getTime();
        String s_start, s_stop;
        s_start = sdf.format(start);
        s_stop = sdf.format(stop);
        Session s;
        Transaction t = null;
        ArrayList<DayHours> workingDays = new ArrayList<DayHours>(0);
        List<Object[]> aicrecs = new ArrayList<Object[]>();
        try {
            s = SessionUtils.getCiclopeSession();
            t = s.getTransaction();
            t.begin();
            String q1 = "SELECT giornata, sum(ore) FROM ciclope.orelavorate\n"
                    + "where \n"
                    + "(giornata between '"+s_start+"' AND curdate())\n"
                    + "and\n"
                    + "personale = "+operatore+" \n"
                    + "group by giornata \n"
                    + "order by \n"
                    + "giornata asc";
            Query q = s.createSQLQuery(q1);
            aicrecs = q.list();
            t.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
                return null;
            }
        }
        for(Object[] objs: aicrecs ){
            workingDays.add(new DayHours((Date)objs[0], ((BigDecimal)objs[1]).floatValue()));
        }
        DayHours dh = null;
        for (Date d : DateUtils.getAllWorkingDaysFrom(start)){
            dh = new DayHours(d, 0);
            if (!workingDays.contains(dh))
                workingDays.add(new DayHours(d, 0f));
        }
        Collections.sort(workingDays);
        return workingDays;
    }

    public static class DayHours implements Comparable {
        
        private Date day;

        /**
         * Get the value of day
         *
         * @return the value of day
         */
        public Date getDay() {
            return day;
        }

        /**
         * Set the value of day
         *
         * @param day new value of day
         */
        public void setDay(Date day) {
            this.day = day;
        }

        private float hours;

        /**
         * Get the value of hours
         *
         * @return the value of hours
         */
        public float getHours() {
            return hours;
        }

        /**
         * Set the value of hours
         *
         * @param hours new value of hours
         */
        public void setHours(float hours) {
            this.hours = hours;
        }

        public DayHours() {
        }

        public DayHours(Date day, float hours) {
            this.day = day;
            this.hours = hours;
        }

        @Override
        public int compareTo(Object o) {
            DayHours dh = (DayHours) o;
            return this.getDay().compareTo(dh.getDay());
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this.day);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DayHours other = (DayHours) obj;
            Calendar thisCal = DateUtils.GetLocalCalendar();
            Calendar otherCal = DateUtils.GetLocalCalendar();
            thisCal.setTime(this.day);
            otherCal.setTime(other.getDay());
            if ( thisCal.get(Calendar.DAY_OF_MONTH) != otherCal.get(Calendar.DAY_OF_MONTH) )
                return false;
            if ( thisCal.get(Calendar.MONTH) != otherCal.get(Calendar.MONTH) )
                return false;
            //finally
            return thisCal.get(Calendar.YEAR) == otherCal.get(Calendar.YEAR);
        }
    }

}
