/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.servlets.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.javasoft.ciclope.persistence.HibernateUtil;

/**
 *
 * @author andrea
 */
public class SessionUtils {
    public static Session getCiclopeSession(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.getCurrentSession();
        if(!s.isOpen()){
            s.getTransaction().rollback();
            s= sf.getCurrentSession();
        }
        return s;
    }
}
