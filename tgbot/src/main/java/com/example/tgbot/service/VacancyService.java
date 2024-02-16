package com.example.tgbot.service;

import com.example.tgbot.dto.Vacancydto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.HashMap;

@Service
public class VacancyService {
    @Autowired
    private VacanciesReaderService vacanciesReaderService;
    private final Map <String, Vacancydto> vacancies = new HashMap<>();
@PostConstruct
public void init() {
List<Vacancydto> list = vacanciesReaderService.getVacanciesFromFile("vacancies.csv");
    for (Vacancydto vacancy: list) {
vacancies.put(vacancy.getId(),vacancy);
    }
}

public List<Vacancydto> getJuniorVacancies () {
    return vacancies.values () .stream().
            filter(v -> v.getTitle().toLowerCase().contains("junior")).
            toList();
}
public Vacancydto get (String id) {
return vacancies.get(id);
}
}
