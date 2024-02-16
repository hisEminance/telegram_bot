package com.example.tgbot.service;

import com.example.tgbot.dto.Vacancydto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Service
public class VacanciesReaderService {
    public List <Vacancydto> getVacanciesFromFile(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        try (InputStreamReader inputStreamReader = new InputStreamReader (resource.getInputStream(),StandardCharsets.UTF_8))
        {
            CsvToBean<Vacancydto> csvToBean = new CsvToBeanBuilder<Vacancydto>(inputStreamReader)
                    .withType(Vacancydto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from the file" + fileName,e);
        }

    }
}
