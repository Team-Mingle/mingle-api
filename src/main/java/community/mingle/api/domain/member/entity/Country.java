package community.mingle.api.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "country")
public class Country {
    @Id
    @Size(max = 45)
    @Column(name = "country", nullable = false, length = 45)
    private String country;

}