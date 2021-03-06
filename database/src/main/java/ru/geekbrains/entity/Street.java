package ru.geekbrains.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "streets", schema = "geo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Street {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    //@OneToMany(mappedBy = "street")
    //@Cascade(CascadeType.ALL)
    //List<GroupAd> groupAds;

    //@OneToMany(mappedBy = "street")
    //@Cascade(CascadeType.ALL)
    //List<Ad> ads;
}
