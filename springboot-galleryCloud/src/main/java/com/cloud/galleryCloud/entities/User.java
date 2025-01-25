package com.cloud.galleryCloud.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;

    @NotNull
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = { jakarta.persistence.CascadeType.PERSIST,
            jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REFRESH, jakarta.persistence.CascadeType.REMOVE }, orphanRemoval = true)
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    public User(Long id) {
        this.id = id;
    }

    public User(String username, String password, String email, LocalDateTime lastLogin,
            ArrayList<Image> images) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastLogin = lastLogin;
        this.images = images;
    }

    public User(String username, String password, String email, LocalDateTime lastLogin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastLogin = lastLogin;
    }
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
                + ", createdAt=" + createdAt + ", lastLogin=" + lastLogin + "]";
    }

    
    

}
