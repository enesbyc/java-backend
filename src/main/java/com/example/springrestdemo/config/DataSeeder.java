package com.example.springrestdemo.config;

import com.example.springrestdemo.entity.PersonEntity;
import com.example.springrestdemo.entity.PersonRole;
import com.example.springrestdemo.entity.TaskEntity;
import com.example.springrestdemo.repository.PersonRepository;
import com.example.springrestdemo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DataSeeder implements CommandLineRunner {

    private final PersonRepository persons;
    private final TaskRepository tasks;

    public DataSeeder(PersonRepository persons, TaskRepository tasks) {
        this.persons = persons;
        this.tasks = tasks;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (persons.count() > 0) {
            return;
        }
        for (int i = 1; i <= 4; i++) {
            String id = "l" + i;
            persons.save(new PersonEntity(id, id, "", PersonRole.lead));
        }
        for (int i = 1; i <= 4; i++) {
            String id = "u" + i;
            persons.save(new PersonEntity(id, id, "", PersonRole.user));
        }

        tasks.save(
                new TaskEntity(
                        "t1",
                        "Login Sayfası Tasarımı",
                        "Kullanıcı giriş ekranının UI tasarımı",
                        "u1",
                        "done",
                        3,
                        4));
        tasks.save(
                new TaskEntity(
                        "t2",
                        "API Entegrasyonu",
                        "Backend servislerine bağlantı kurulması",
                        "u1",
                        "in-progress",
                        5,
                        null));
        tasks.save(
                new TaskEntity(
                        "t3",
                        "Dashboard Grafikleri",
                        "Yönetim paneli grafik bileşenleri",
                        "u2",
                        "todo",
                        8,
                        null));
        tasks.save(
                new TaskEntity(
                        "t4",
                        "Bildirim Sistemi",
                        "Push notification altyapısı",
                        "u2",
                        "done",
                        5,
                        5));
        tasks.save(
                new TaskEntity(
                        "t5",
                        "Veritabanı Optimizasyonu",
                        "Sorgu performans iyileştirmesi",
                        "u3",
                        "in-progress",
                        8,
                        null));
        tasks.save(
                new TaskEntity(
                        "t6",
                        "Unit Test Yazımı",
                        "Kritik modüller için test coverage",
                        "u3",
                        "todo",
                        3,
                        null));
        tasks.save(
                new TaskEntity(
                        "t7",
                        "Responsive Tasarım",
                        "Mobil uyumluluk düzenlemeleri",
                        "u1",
                        "done",
                        2,
                        3));
        tasks.save(
                new TaskEntity(
                        "t8",
                        "CI/CD Pipeline",
                        "Otomatik deploy sürecinin kurulması",
                        "u2",
                        "in-progress",
                        5,
                        null));
    }
}
