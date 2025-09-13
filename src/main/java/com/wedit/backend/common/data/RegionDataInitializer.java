package com.wedit.backend.common.data;

import com.wedit.backend.api.vendor.entity.Region;
import com.wedit.backend.api.vendor.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionDataInitializer implements ApplicationRunner {

    private final RegionRepository regionRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        // DB에 이미 데이터가 있는지 확인하여, 중복 실행 방지
        if (regionRepository.count() > 0) {
            log.info("Region data already exists. Skipping initialization.");
            return;
        }

        log.info("Starting region data initialization from CSV...");

        // resources/data/regions.csv 파일을 읽어옴
        ClassPathResource resource = new ClassPathResource("data/regions.CSV");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

        // CSV 헤더 스킵
        reader.readLine();

        String line;
        // code를 key로, Region 엔티티를 value로 사용하여 부모를 효율적으로 찾기 위한 맵
        Map<String, Region> regionMap = new HashMap<>();
        List<Region> regionsToSave = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");
            if (columns.length < 2) continue;

            String code = columns[0];
            String fullName = columns[1];

            // " "으로 분리하여 지역 레벨 파싱
            String[] nameParts = fullName.split(" ");

            // 1레벨 (시/도) 처리
            String level1Name = nameParts[0];
            String level1Code = code.substring(0, 2);
            Region level1Region = regionMap.computeIfAbsent(level1Code, k -> {
                Region newRegion = Region.builder()
                        .name(level1Name)
                        .code(level1Code)
                        .level(1)
                        .parent(null) // 최상위 이므로 부모 없음
                        .build();
                regionsToSave.add(newRegion);
                return newRegion;
            });

            // 2레벨 (시/군/구) 처리 (존재하는 경우)
            if (nameParts.length > 1) {
                String level2Name = nameParts[1];
                String level2Code = code.substring(0, 5); // 시/군/구 코드는 5자리
                Region level2Region = regionMap.computeIfAbsent(level2Code, k -> {
                    Region newRegion = Region.builder()
                            .name(level2Name)
                            .code(level2Code)
                            .level(2)
                            .parent(level1Region)
                            .build();
                    regionsToSave.add(newRegion);
                    return newRegion;
                });

                // 3레벨 (읍/면/동) 처리 (존재하는 경우)
                if (nameParts.length > 2) {
                    String level3Name = nameParts[2];
                    String level3Code = code; // 읍/면/동 코드는 전체 코드
                    regionMap.computeIfAbsent(level3Code, k -> {
                        Region newRegion = Region.builder()
                                .name(level3Name)
                                .code(level3Code)
                                .level(3)
                                .parent(level2Region)
                                .build();
                        regionsToSave.add(newRegion);
                        return newRegion;
                    });
                }
            }
        }

        // 중복 제거된 Region 리스트를 DB에 한번에 저장
        regionRepository.saveAll(regionsToSave);
        log.info("Successfully initialized {} regions.", regionsToSave.size());
        reader.close();
    }
}
