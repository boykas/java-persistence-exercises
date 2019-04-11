package ua.procamp.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO.setScale(2);

//    @OneToOne(mappedBy = "holder")
//    private Card card;

    @OneToMany(mappedBy = "holder")
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        getCards().add(card);
        card.setHolder(this);
    }

    public void removeCard(Card card) {
        getCards().remove(card);
        card.setHolder(null);
    }
}
