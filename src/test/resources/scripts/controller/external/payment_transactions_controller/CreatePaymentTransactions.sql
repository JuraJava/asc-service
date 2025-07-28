INSERT INTO asc_service.service_center (id, service_center_name, service_center_phone_number, service_center_address)
VALUES (1, 'IP Zaharyan E.A. ''Samsung Mobile Equipment Service Center', '89180038448', 'Sochi, Severnaya str., 6');

INSERT INTO asc_service.device (id, device_name, device_color)
VALUES (2, 'SM-R630', 'BROWN');

INSERT INTO asc_service.device (id, device_name, device_color)
VALUES (3, 'SM-N780', 'BROWN');

INSERT INTO asc_service.employee (id, full_name_employee, patronymic, user_id, role, share_of_completed_work)
VALUES (7, 'Иванов Иван', null, 'c6200c05-2281-409f-861c-9796bae93fa5', 'RECEIVER', null);

INSERT INTO asc_service.employee (id, full_name_employee, patronymic, user_id, role, share_of_completed_work)
VALUES (8, 'Тен Александр', null, '0b853e91-0768-48e0-8905-e5a512db65dd', 'ENGINEER', 0.4);

INSERT INTO asc_service.repair_request (id, date_creation, type_repair, request_status, device_id, id_service_center, serial_number, date_sale, reported_defect, device_appearance, full_name_consumer, patronymic_consumer, address_consumer, phone_number_consumer, id_employee_who_accepted)
VALUES (3, '2025-07-27 12:36:58.131513', 'NON_WARRANTY', 'ACCEPTED', 3, 1, 'R3000121DTLK', null, 'The screen is broken', 'No visible damage', 'Petrova Inna', null, 'Moscow, Prospekt Mira, house 12, apartment 24', '89253456003', 7);

INSERT INTO asc_service.work_order (id, id_repair_request, date_creation_work_order, repair_status, payment_status, cost_replaced_part, cost_performed_work, total_cost_work_order, salary_for_this_order, id_employee_who_performed, date_completion_repair, description)
VALUES (5, 3, '2025-07-27 12:41:07.975242', 'CLOSED', 'PAID', 31766.25, 8000.00, 39766.25, 3200.00, 8, '2025-07-27 12:45:32.372248', null);

INSERT INTO asc_service.work_order (id, id_repair_request, date_creation_work_order, repair_status, payment_status, cost_replaced_part, cost_performed_work, total_cost_work_order, salary_for_this_order, id_employee_who_performed, date_completion_repair, description)
VALUES (6, 3, '2025-07-27 12:41:34.788294', 'CLOSED', 'PAID', 883.19, 8000.00, 8883.19, 3200.00, 8, '2025-07-27 12:45:38.085285', null);