package com.example.jwt.auth.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/*
* Para manejar detalles de la autenticacion del usuario debemos implementar
* la interfaz UserDetails
* */
@Table(name = "users")
@Entity
public class User implements UserDetails, Comparable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    /*
    * Retorna los roles del usuario,
    * retornamos vacio porque no se cubrira un control
    * de acceso basado en roles
    * */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
    * ========== COMPARABLE (Ordenamiento natural de la clase) ==========
    * - Se implementa en la propia clase
    * - Solo puede haber UN ordenamiento natural por clase
    * - Se usa con: Collections.sort(lista) o lista.sort(null)
    * - En este caso el ordenamiento natural es por fullName
    */
    @Override
    public int compareTo(User otro) {
        return this.fullName.compareToIgnoreCase(otro.fullName);
    }

    /*
    * ========== COMPARATORS (Ordenamientos alternativos externos) ==========
    * - Se definen como objetos externos
    * - Puedes tener MUCHOS comparators diferentes
    * - Se usan con: Collections.sort(lista, comparator) o lista.sort(comparator)
    * - Útil cuando necesitas ordenar de diferentes formas sin modificar la clase
    */

    // Ordenar por email (alfabético)
    public static final Comparator<User> POR_EMAIL = Comparator.comparing(User::getEmail);

    // Ordenar por fecha de creación (más reciente primero)
    public static final Comparator<User> POR_FECHA_CREACION = Comparator.comparing(User::getCreatedAt).reversed();

    // Ordenar por ID (numérico)
    public static final Comparator<User> POR_ID = Comparator.comparingInt(User::getId);

    // Ordenar por nombre, luego por email si hay empate
    public static final Comparator<User> POR_NOMBRE_Y_EMAIL =
            Comparator.comparing(User::getFullName)
                      .thenComparing(User::getEmail);
}
