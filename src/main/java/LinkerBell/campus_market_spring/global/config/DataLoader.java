package LinkerBell.campus_market_spring.global.config;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.TermsRepository;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Value("${path.school_email}")
    private String schoolEmailDataPath;

    @Value("${path.terms_info}")
    private String termsInfoDataPath;

    private final CampusRepository campusRepository;
    private final TermsRepository termsRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(schoolEmailDataPath))){
            if (campusRepository.count() <= 0) {
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

        try (CSVReader reader = new CSVReader(new FileReader(termsInfoDataPath))){
            if (termsRepository.count() <= 0) {
                List<String[]> records = reader.readAll();
                for (String[] record : records) {
                    String title = record[0];
                    String url = record[1];
                    boolean isRequired = record[2].equals("true");

                    Terms terms = Terms.builder()
                        .title(title)
                        .termsUrl(url)
                        .isRequired(isRequired).build();

                    termsRepository.save(terms);
                }
            }
        }
    }
}
