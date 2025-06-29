package com.yurdan.ascService.config;

import com.yurdan.ascService.model.entity.Device;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.SparePart;
import com.yurdan.ascService.model.enums.DeviceColor;
import com.yurdan.ascService.model.enums.RoleOfEmployee;
import com.yurdan.ascService.repository.DeviceRepository;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.SparePartRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", value = "init-data", havingValue = "true")
public class DataInitializer {
    private static final Random RANDOM = new Random();
    private final DeviceRepository deviceRepository;
    private final SparePartRepository sparePartRepository;
    private final EmployeeRepository employeeRepository;

    private static final String[] sparePartsLetters = {"A", "B", "C"};
    private static final String[] deviceLetters = {"G", "R", "N"};

    @PostConstruct
    public void init() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            devices.add(deviceGenerator());
        }
        List<Device> savedDevices = deviceRepository.saveAll(devices);

        List<SparePart> spareParts = new ArrayList<>();
        for (int i = 0; i < 450; i++) {  // Generate 450 spare parts
            spareParts.add(sparePartGenerator());
        }

        // Связываем устройства и запчасти: каждое устройство получает по одной запчасти каждого типа
        Map<String, List<SparePart>> sparePartsByType = spareParts.stream()
                .collect(Collectors.groupingBy(sp -> getSparePartTypeFromBatch(sp.getBatchNumber())));

        // Получаем уникальные типы из сгенерированных запчастей
        List<String> allTypes = new ArrayList<>(sparePartsByType.keySet());

        while (!sparePartsByType.isEmpty()) {
            for (Device device : savedDevices) {
                // Для каждого доступного типа
                for (String type : allTypes) {
                    if (sparePartsByType.get(type) != null && !sparePartsByType.get(type).isEmpty()) {
                        SparePart sparePart = sparePartsByType.get(type).remove(0);
                        sparePart.setDevice(device);
                        sparePartRepository.save(sparePart);
                    } else {
                        sparePartsByType.remove(type);
                    }
                }
            }
        }
        List<SparePart> savedSpareParts = sparePartRepository.saveAll(spareParts);

        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder().email("engineer@mail.ru").fullName("Инженеров Инженер").patronymic("Инженерович")
                .role(RoleOfEmployee.ENGINEER).shareOfWork(0.4F).build());
//        employees.add(Employee.builder().email("engineer@mail.ru").fullName("Мастеров Мастер").patronymic("Мастерович")
//                .role(RoleOfEmployee.ENGINEER).shareOfWork(0.4F).build());
        employees.add(Employee.builder().email("admin@mail.ru").fullName("Админский Админ").patronymic("Админович")
                .role(RoleOfEmployee.ADMIN).shareOfWork(0.3F).build());
        employees.add(Employee.builder().email("administrator@mail.ru").fullName("Администраторский Администр").patronymic("Админович")
                .role(RoleOfEmployee.ADMINISTRATOR).shareOfWork(0.3F).build());
        employees.add(Employee.builder().email("receiver@mail.ru").fullName("Приёмова Приемщица").patronymic("Приёмовна")
                .role(RoleOfEmployee.RECEIVER).shareOfWork(0.2F).build());
        employees.add(Employee.builder().email("storekeeper@mail.ru").fullName("Складских Кладовщик").patronymic("Коробович")
                .role(RoleOfEmployee.STOREKEEPER).shareOfWork(0.4F).build());
        employees.add(Employee.builder().email("director@mail.ru").fullName("Главнов Дирик").patronymic("Начальникевич")
                .role(RoleOfEmployee.DIRECTOR).shareOfWork(0.4F).build());
        employeeRepository.saveAll(employees);
    }

    private BigDecimal bigDecimalGenerator(int min, int max) {
        return BigDecimal.valueOf((Math.random() * (max - min + 1)) + min)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String deviceNameGenerator() {
        return "SM-" + letterGenerator(Device.class) + numberGenerator(100, 995, 5);
    }

    private String letterGenerator(Class<?> clazz) {
        int randomIndex = RANDOM.nextInt(deviceLetters.length);
        if (clazz != null && clazz.equals(Device.class)) {
            return deviceLetters[randomIndex];
        }
        return sparePartsLetters[randomIndex];
    }

    private int numberGenerator(int min, int max, int step) {
        int count = (max - min) / step + 1;
        int randomIndex = RANDOM.nextInt(count);
        return min + randomIndex * step;
    }

    private DeviceColor colorGenerator() {
        DeviceColor[] colors = DeviceColor.values();
        int randomIndex = RANDOM.nextInt(colors.length);
        return colors[randomIndex];
    }

    private Device deviceGenerator() {
        return Device.builder()
                .deviceName(deviceNameGenerator())
                .deviceColor(colorGenerator())
                .build();
    }

    private String getSparePartTypeFromBatch(String batchNumber) {
        try {
            // Извлекаем часть после дефиса
            String[] parts = batchNumber.split("-");
            if (parts.length < 2) {
                return "Unknown";
            }
            String numberPart = parts[1];

            // Извлекаем только цифры
            String digits = numberPart.replaceAll("[^0-9]", "");
            if (digits.length() < 6) {
                return "Unknown";
            }
            String firstSixDigits = digits.substring(0, 6);
            int num = Integer.parseInt(firstSixDigits);

            // Определяем тип по диапазону
            if (num <= 199999) return "1";
            if (num <= 299999) return "2";
            if (num <= 399999) return "3";
            if (num <= 499999) return "4";
            if (num <= 599999) return "5";
            if (num <= 699999) return "6";
            if (num <= 799999) return "7";
            if (num <= 899999) return "8";
            return "9"; // 900000-999999
        } catch (Exception e) {
            return "10";
        }
    }

    private SparePart sparePartGenerator() {
        String number = batchNumberGenerator();

        int numberInt = Integer.parseInt(number.substring(5, 11));
        String description;
        BigDecimal cost;
        int hundredThousands = numberInt / 100000;
        switch (hundredThousands) {
            case 0, 1 -> { // 0-199999
                description = "Проклейка";
                cost = bigDecimalGenerator(50, 200);
            }
            case 2 -> { // 200000-299999
                description = "Динамик";
                cost = bigDecimalGenerator(300, 1200);
            }
            case 3 -> { // 300000-399999
                description = "Микрофон";
                cost = bigDecimalGenerator(200, 600);
            }
            case 4 -> { // 400000-499999
                description = "Дополнительная плата";
                cost = bigDecimalGenerator(800, 3200);
            }
            case 5 -> { // 500000-599999
                description = "Аккумулятор";
                cost = bigDecimalGenerator(1500, 6800);
            }
            case 6 -> { // 600000-699999
                description = "Камера основная";
                cost = bigDecimalGenerator(2000, 8000);
            }
            case 7 -> { // 700000-799999
                description = "Дисплей";
                cost = bigDecimalGenerator(4000, 20000);
            }
            case 8 -> { // 800000-899999
                description = "Плата";
                cost = bigDecimalGenerator(10000, 30000);
            }
            case 9 -> { // 900000-999999
                description = "Камера фронтальная";
                cost = bigDecimalGenerator(1700, 2600);
            }
            default -> { // 1000000 и выше
                description = "Запчасть";
                cost = bigDecimalGenerator(100, 10000);
            }
        }

        return SparePart.builder()
                .batchNumber(number)
                .description(description)
                .remainingQuantity(RANDOM.nextLong(10) + 1)
                .reserveQuantity(0L)
                .cost(cost)
                .build();
    }

    private String batchNumberGenerator() {
        return "GH82-" + numberGenerator(100000, 999999, 1) + letterGenerator(SparePart.class);
    }

}
