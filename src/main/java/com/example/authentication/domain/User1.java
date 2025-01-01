package com.example.authentication.domain;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data  //this is for getter and setters lombok
@NoArgsConstructor
@AllArgsConstructor
public class User1{
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    @ManyToMany(fetch= FetchType.EAGER)
    private Collection<Role> roles=new ArrayList<>();
    


}