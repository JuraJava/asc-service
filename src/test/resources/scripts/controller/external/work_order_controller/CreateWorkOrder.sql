INSERT INTO asc_service.service_center (id, service_center_name, service_center_phone_number, service_center_address)
VALUES (1, 'IP Zaharyan E.A. ''Samsung Mobile Equipment Service Center', '89180038448', 'Sochi, Severnaya str., 6');

INSERT INTO asc_service.device (id, device_name, device_color)
VALUES (2, 'SM-R630', 'BROWN');

INSERT INTO asc_service.employee (id, full_name_employee, patronymic, user_id, role, share_of_completed_work)
VALUES (7, 'Иванов Иван', null, 'c6200c05-2281-409f-861c-9796bae93fa5', 'ENGINEER', null);

INSERT INTO asc_service.employee (id, full_name_employee, patronymic, user_id, role, share_of_completed_work)
VALUES (8, 'Тен Александр', null, '0b853e91-0768-48e0-8905-e5a512db65dd', 'ENGINEER', 0.4);

INSERT INTO asc_service.spare_part (id, batch_number, remaining_quantity, reserve_quantity, cost, device_id, description)
VALUES (11, 'GH82-265849B', 3, 0, 742.47, 2, 'Дисплей');

INSERT INTO asc_service.spare_part (id, batch_number, remaining_quantity, reserve_quantity, cost, device_id, description)
VALUES (10, 'GH82-173072B', 4, 0, 140.72, 2, 'Проклейка');

INSERT INTO asc_service.completed_work (id, name_work, cost_work)
VALUES (3, 'REPLACEMENT_ON_PAID_BASIS', 3500);

INSERT INTO asc_service.completed_work (id, name_work, cost_work)
VALUES (4, 'ADDITIONAL_WORK', 4500);

INSERT INTO asc_service.repair_request (id, date_creation, type_repair, request_status, device_id, id_service_center, serial_number, date_sale, reported_defect, device_appearance, full_name_consumer, patronymic_consumer, address_consumer, phone_number_consumer, id_employee_who_accepted)
VALUES (2, '2025-07-26 16:11:46.102000', 'NON_WARRANTY', 'ACCEPTED', 2, 1, 'R2000121DTLK', null, 'The screen is broken', 'No visible damage', 'Petrova Inna', null, 'Moscow, Prospekt Mira, house 12, apartment 24', '89253456002', 7);