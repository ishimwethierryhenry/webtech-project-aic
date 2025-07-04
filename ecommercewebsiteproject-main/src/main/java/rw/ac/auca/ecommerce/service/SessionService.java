package rw.ac.auca.ecommerce.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionService {

    private static final String CUSTOMER_ID = "customerId";


    public void setCurrentCustomerId(HttpSession session, UUID customerId) {
        session.setAttribute(CUSTOMER_ID, customerId);
    }


    public UUID getCurrentCustomerId(HttpSession session) {
        Object id = session.getAttribute(CUSTOMER_ID);
        if (id instanceof UUID) {
            return (UUID) id;
        }
        return null;
    }

    // Optionally, clear customer ID on logout
    public void clearCurrentCustomerId(HttpSession session) {
        session.removeAttribute(CUSTOMER_ID);
    }
}
