package LinkerBell.campus_market_spring.global.config;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Value("${path.school_email}")
    private String schoolEmailDataPath;

    private final CampusRepository campusRepository;

    @Override
    public void run(String... args) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(schoolEmailDataPath))){
            if (campusRepository.count() > 0) {
                return ;
            }
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                String universityName = record[0];
                String region = record[1];
                String email = record[2];
                Campus campus = Campus.builder()
                    .universityName(universityName)
                    .region(region)
                    .email(email).build();

                campusRepository.save(campus);
            }
        }
    }
}
