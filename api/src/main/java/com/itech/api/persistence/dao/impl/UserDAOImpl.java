package com.itech.api.persistence.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.itech.api.persistence.dao.UserDAO;
import com.itech.api.persistence.entity.User;

import jakarta.persistence.Query;

@Repository
public class UserDAOImpl implements UserDAO{
    public static final String SELECT_HQL = "SELECT u "
            + "FROM User u "
            + "WHERE u.delFlag <> 1 ";
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @SuppressWarnings("deprecation")
    @Override
    public User getUserByEmail(String email) {
        StringBuffer hql = new StringBuffer(SELECT_HQL);
        hql.append("AND u.email = :email");
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setParameter("email", email);
        
        return (User) query.getResultList().get(0);
    }

}
