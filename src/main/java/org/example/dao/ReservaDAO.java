package org.example.dao;

import org.example.model.Reserva;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaDAO {

    public boolean guardar(Reserva reserva) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(reserva);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Reserva reserva) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(reserva);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Reserva> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.pista " +
                            "ORDER BY r.fecha DESC, r.horaInicio DESC",
                    Reserva.class).list();
        }
    }

    public List<Reserva> obtenerPorUsuario(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.pista " +
                                    "WHERE r.usuario.email = :email " +
                                    "ORDER BY r.fecha DESC, r.horaInicio DESC",
                            Reserva.class)
                    .setParameter("email", email)
                    .list();
        }
    }

    public boolean hayConflicto(int pistaId, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(r) FROM Reserva r " +
                                    "WHERE r.pista.id = :pistaId " +
                                    "AND r.fecha = :fecha " +
                                    "AND r.horaInicio < :fin " +
                                    "AND r.horaFin > :inicio",
                            Long.class)
                    .setParameter("pistaId", pistaId)
                    .setParameter("fecha",   fecha)
                    .setParameter("inicio",  inicio)
                    .setParameter("fin",     fin)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean eliminar(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Reserva r = session.get(Reserva.class, id);
            if (r != null) session.remove(r);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public long contarTotal() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(r) FROM Reserva r", Long.class).uniqueResult();
            return count != null ? count : 0;
        }
    }

    public double ingresosTotales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double sum = session.createQuery(
                    "SELECT SUM(r.total) FROM Reserva r", Double.class).uniqueResult();
            return sum != null ? sum : 0.0;
        }
    }

    public List<Object[]> reservasPorPista() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT r.pista.nombre, COUNT(r) FROM Reserva r " +
                            "GROUP BY r.pista.nombre ORDER BY COUNT(r) DESC",
                    Object[].class).list();
        }
    }
}