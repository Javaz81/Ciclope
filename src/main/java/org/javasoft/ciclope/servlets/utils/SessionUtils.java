/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets.utils;

import com.sun.media.jfxmedia.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;

/**
 *
 * @author andrea
 */
public class SessionUtils {
    public static Session getCiclopeSession(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.getCurrentSession();
        Transaction t = s.getTransaction();
        if(t.isActive()){
            t.rollback();
        }
        if(!s.isOpen()){
            s= sf.getCurrentSession();
        }
        return s;
    }
}
