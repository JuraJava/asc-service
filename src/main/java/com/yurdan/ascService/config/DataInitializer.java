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
import java.util.*;
import java.util.stream.Collectors;


/**
 * Этот класс служит инициализатором тестовых данных при запуске Spring Boot-приложения.
 * Он автоматически наполняет базу данных фейковыми устройствами, запчастями и сотрудниками,
 * если в application.yaml включена настройка app.init-data:true
 */

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

    /**
     * Основной метод инициализации
     * т.к. помечен @PostConstruct, выполнится один раз при старте приложения,
     * создаётся 50 фейковых устройств и сохраняются в БД.
     */

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
        sparePartRepository.saveAll(spareParts);

        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder()
                .fullName("Инженеров Инженер")
                .patronymic("Инженерович")
                .userId(UUID.fromString("9343d3da-836a-476c-bf97-476f700110be"))
                .role(RoleOfEmployee.ENGINEER)
                .shareOfWork(0.4F)
                .build()
        );
        employees.add(Employee.builder()
                .fullName("Админский Админ")
                .patronymic("Админович")
                .userId(UUID.fromString("78067406-4136-4284-9086-bd995b60acbe"))
                .role(RoleOfEmployee.ADMIN)
                .shareOfWork(0.3F)
                .build()
        );
        employees.add(Employee.builder()
                .fullName("Администраторский Администр")
                .patronymic("Админович")
                .userId(UUID.fromString("937b3975-c60d-4436-81c7-708d6bc39afa"))
                .role(RoleOfEmployee.ADMINISTRATOR)
                .shareOfWork(0.3F)
                .build()
        );
        employees.add(Employee.builder()
                .fullName("Приёмова Приемщица")
                .patronymic("Приёмовна")
                .userId(UUID.fromString("28fc7f1b-1b32-44f0-9ec9-66b0c1fdc392"))
                .role(RoleOfEmployee.RECEIVER)
                .shareOfWork(0.2F)
                .build()
        );
        employees.add(Employee.builder()
                .fullName("Складских Кладовщик")
                .patronymic("Коробович")
                .userId(UUID.fromString("a86e009d-e7c6-427f-82c6-9fb9942671ab"))
                .role(RoleOfEmployee.STOREKEEPER)
                .shareOfWork(0.4F)
                .build()
        );
        employees.add(Employee.builder()
                .fullName("Главнов Дирик")
                .patronymic("Начальникевич")
                .userId(UUID.fromString("1b23b18d-ce45-4d30-888f-3dd524d337e7"))
                .role(RoleOfEmployee.DIRECTOR)
                .shareOfWork(0.4F)
                .build()
        );
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

    /**
     * Запчасти группируются по типам, определяемым этим методом
     */

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
