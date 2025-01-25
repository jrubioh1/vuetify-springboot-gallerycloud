package com.cloud.galleryCloud.entities;

import java.io.File;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String urlString;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, cascade = { jakarta.persistence.CascadeType.PERSIST,
        jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    
    public Image(Long id) {
        this.id = id;
    }
    


    public Image(@NotBlank String name, @NotBlank String urlString, LocalDateTime updatedAt,
            User user) {
        this.name = name;
        this.urlString = urlString;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
        this.user = user;
    }

    @PreUpdate
    public void setUpdatedAt() {
    this.updatedAt = LocalDateTime.now();
    }

    @PreRemove
    public void deleteFileFromMedia() {
    File file = new File(this.urlString);
    if (file.exists()) {
        file.delete();
    }
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
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
        Image other = (Image) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        return true;
    }

    public int getYear() {
        return createdAt.getYear();
    }
    
    public int getMonth() {
        return createdAt.getMonthValue();
    }


    @Override
    public String toString() {
        return "Image [id=" + id + ", name=" + name + ", url=" + urlString + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + ", user=" + user + "]";
    }

   
    
}
