package ru.geekbrains.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addresses", schema = "geo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "house")
    String house;

    @Column(name = "floors_count")
    Integer floorsCount;

    @ManyToOne
    @JoinColumn(name = "street_id")
    Street street;
}
