package com.deuceng.moviedive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String  username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable=false, length=100)
    private String firstname;

    @Column(nullable=false, length=100)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false,length=11)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

//    @ManyToMany
//    @JoinTable(
//            name = "user_follows",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "follower_id")
//    )
//
//    private Set<User> followers = new HashSet<>();
//
//    //cascade: User üzerinde yaptığın işlemleri MovieList’e de uygula
//    //orphanRemoval: Listeden silinince DB’den de sil
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<MovieList> lists = new HashSet<>();
}
