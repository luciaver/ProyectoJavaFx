package org.example.dao;

import org.example.model.Pista;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PistaDAO {

    public boolean guardar(Pista pista) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(pista);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Pista pista) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(pista);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Pista p = session.get(Pista.class, id);
            if (p != null) session.remove(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Pista> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Pista p ORDER BY p.deporte.nombre, p.nombre", Pista.class).list();
        }
    }

    public List<Pista> obtenerPorDeporte(String nombreDeporte) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Pista p WHERE p.deporte.nombre = :nombre ORDER BY p.nombre",
                            Pista.class)
                    .setParameter("nombre", nombreDeporte)
                    .list();
        }
    }

    public Pista obtenerPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Pista.class, id);
        }
    }

    public long contarTotal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long c = session.createQuery(
                    "SELECT COUNT(p) FROM Pista p", Long.class).uniqueResult();
            return c != null ? c : 0;
        }
    }

    public long contarLibres() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long c = session.createQuery(
                    "SELECT COUNT(p) FROM Pista p WHERE p.estado = 'LIBRE'",
                    Long.class).uniqueResult();
            return c != null ? c : 0;
        }
    }

    public List<Object[]> pistasAgrupadasPorDeporte() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT p.deporte.nombre, COUNT(p) FROM Pista p GROUP BY p.deporte.nombre",
                    Object[].class).list();
        }
    }
}