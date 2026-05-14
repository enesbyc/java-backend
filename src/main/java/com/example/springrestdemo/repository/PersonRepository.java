package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.PersonEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, String> {

    List<PersonEntity> findAllByOrderByIdAsc();
}
