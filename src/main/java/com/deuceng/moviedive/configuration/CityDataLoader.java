package com.deuceng.moviedive.configuration;

import com.deuceng.moviedive.entity.City;
import com.deuceng.moviedive.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CityDataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;

    @Override
    public void run(String... args) {

        // Eğer city tablosu doluysa bir daha ekleme
        if (cityRepository.count() > 0) {
            return;
        }

        List<String> cities = List.of(
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı",
                "Amasya", "Ankara", "Antalya", "Artvin",
                "Aydın", "Balıkesir", "Bilecik", "Bingöl",
                "Bitlis", "Bolu", "Burdur", "Bursa",
                "Çanakkale", "Çankırı", "Çorum", "Denizli",
                "Diyarbakır", "Edirne", "Elazığ", "Erzincan",
                "Erzurum", "Eskişehir", "Gaziantep", "Giresun",
                "Gümüşhane", "Hakkari", "Hatay", "Isparta",
                "Mersin", "İstanbul", "İzmir", "Kars",
                "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
                "Kocaeli", "Konya", "Kütahya", "Malatya",
                "Manisa", "Kahramanmaraş", "Mardin", "Muğla",
                "Muş", "Nevşehir", "Niğde", "Ordu",
                "Rize", "Sakarya", "Samsun", "Siirt",
                "Sinop", "Sivas", "Tekirdağ", "Tokat",
                "Trabzon", "Tunceli", "Şanlıurfa", "Uşak",
                "Van", "Yozgat", "Zonguldak",
                "Aksaray", "Bayburt", "Karaman", "Kırıkkale",
                "Batman", "Şırnak", "Bartın", "Ardahan",
                "Iğdır", "Yalova", "Karabük", "Kilis",
                "Osmaniye", "Düzce"
        );

        for (String cityName : cities) {
            if (!cityRepository.existsByName(cityName)) {
                cityRepository.save(new City(null, cityName));
            }
        }

        System.out.println("✅ 81 il başarıyla yüklendi.");
    }
}
