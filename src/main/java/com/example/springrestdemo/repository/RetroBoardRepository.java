package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.RetroBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroBoardRepository extends JpaRepository<RetroBoardEntity, String> {}
