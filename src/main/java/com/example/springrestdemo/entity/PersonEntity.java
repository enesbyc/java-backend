package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "persons")
public class PersonEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 128)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PersonRole role;

    protected PersonEntity() {}

    public PersonEntity(String id, String name, String surname, PersonRole role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public PersonRole getRole() {
        return role;
    }
}
