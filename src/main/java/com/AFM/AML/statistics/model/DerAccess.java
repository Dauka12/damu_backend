package com.AFM.AML.statistics.model;

import com.AFM.AML.User.models.Der;
import jakarta.persistence.*;

@Entity
@Table(name = "der_access")  // Создайте таблицу в БД, если ещё нет
public class DerAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "der_id")
    private Der der;

    private boolean canViewAll;

    public DerAccess() {
    }

    public DerAccess(Long userId, String derName, boolean canViewAll) {
        this.userId = userId;
        this.der = der;
        this.canViewAll = canViewAll;
    }

    // Getters / setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Der getDerName() {
        return der;
    }

    public void setDerName(Der der) {
        this.der = der;
    }

    public boolean isCanViewAll() {
        return canViewAll;
    }

    public void setCanViewAll(boolean canViewAll) {
        this.canViewAll = canViewAll;
    }
}
