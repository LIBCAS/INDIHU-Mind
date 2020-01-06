--delete from uas_action;
--delete from uas_config_file;
--delete from uas_error;
--delete from uas_sequence;
--delete from uas_soap_conversation;
--delete from uas_notification;
--
--delete from uas_report;
--delete from uas_file;
--
--delete from uas_assigned_role;
--delete from uas_role_permission;
--delete from uas_role;
--
--delete from uas_dictionary_value;
--delete from uas_dictionary;
--
--delete from uas_job_run;
--delete from uas_job;
--
--delete from uas_revision_item;
--delete from uas_revision;
--
--delete from vzb_card_category;
--delete from vzb_card_label;
--delete from vzb_card_linked_card;
--
--delete from vzb_category;
--delete from vzb_label;
--
--delete from vzb_attribute;
--delete from vzb_card_content;
--delete from vzb_card;
--
--delete from vzb_attribute_template;
--delete from vzb_card_template;
--delete from vzb_user;

INSERT INTO public.vzb_user (id, email, password, role, created, updated, deleted) VALUES('135e3cec-818b-47da-bc5c-bb83685fbef1', 'i1@i.invalid', '', 'USER', '2019-04-04 23:41:00', '2019-04-04 23:39:00', null);

INSERT INTO public.vzb_label (id, owner_id, name, color) VALUES('e0166151-918c-41fd-8795-853e0f1c0f22', '135e3cec-818b-47da-bc5c-bb83685fbef1', 'Totálně čarný label', '#000000');
INSERT INTO public.vzb_label (id, owner_id, name, color) VALUES('d1c6bd98-557b-4877-87f7-4e216c2c2fd0', '135e3cec-818b-47da-bc5c-bb83685fbef1', 'Nepěkný lime label', '#00FF00');
INSERT INTO public.vzb_label (id, owner_id, name, color) VALUES('ec137bcb-da4e-4eba-8f2c-df2274b639ab', '135e3cec-818b-47da-bc5c-bb83685fbef1', 'Nepěkný cyanový label', '#00FFFF');

INSERT INTO public.vzb_category (id, name, owner_id, parent_id) VALUES('0f3f3623-097c-4ba0-b6ca-32aab3e9989a', 'Kategorie prvního řádu obsahující subkategorie', '135e3cec-818b-47da-bc5c-bb83685fbef1', null);
INSERT INTO public.vzb_category (id, name, owner_id, parent_id) VALUES('b0370acb-e5e1-4a47-8bb6-6b5821b750cb', 'Subkategorie číslo 1 druhého řádu', '135e3cec-818b-47da-bc5c-bb83685fbef1', '0f3f3623-097c-4ba0-b6ca-32aab3e9989a');
INSERT INTO public.vzb_category (id, name, owner_id, parent_id) VALUES('a838fd24-5322-4702-b537-ee5dbc7a895b', 'Subkategorie číslo 2 třetího řádu', '135e3cec-818b-47da-bc5c-bb83685fbef1', 'b0370acb-e5e1-4a47-8bb6-6b5821b750cb');
INSERT INTO public.vzb_category (id, name, owner_id, parent_id) VALUES('9b5c988c-8a85-47f8-aa42-d47c44a9c1ea', 'Subkategorie číslo 3 druhého řádu', '135e3cec-818b-47da-bc5c-bb83685fbef1', '0f3f3623-097c-4ba0-b6ca-32aab3e9989a');
INSERT INTO public.vzb_category (id, name, owner_id, parent_id) VALUES('0b4edaad-3e7e-498f-9320-2cdc3b406e18', 'Nevětvená kategorie', '135e3cec-818b-47da-bc5c-bb83685fbef1', null);

INSERT INTO public.vzb_card (id, owner_id, created, updated, deleted, pid) VALUES('39b3b925-ab6a-43a0-b66b-c8f70f46fb53', '135e3cec-818b-47da-bc5c-bb83685fbef1', '2019-04-04 23:41:00', '2019-04-04 23:40:00', null, 1);
INSERT INTO public.vzb_card_content (id, previous_version_id, card_id, created, updated, deleted, name, last_version) VALUES('a0b2d288-77c3-4546-a452-3332ccc360b8', null, '39b3b925-ab6a-43a0-b66b-c8f70f46fb53', '2019-04-04 23:41:00', '2019-04-04 23:41:00', null, 'Karta číslo 1 prvního uživatele, první verze', false);
INSERT INTO public.vzb_card_content (id, previous_version_id, card_id, created, updated, deleted, name, last_version) VALUES('54f38d2d-1b5d-42e9-a96b-9bb3d9edeb52', null, '39b3b925-ab6a-43a0-b66b-c8f70f46fb53', '2019-04-04 23:41:00', '2019-04-04 23:42:00', null, 'Karta číslo 2 prvního uživatele, druhá verze', false);
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('98539bea-e7e8-45c8-a117-1c68204916c4', 'a0b2d288-77c3-4546-a452-3332ccc360b8', 0, 'Atribut první', 'STRING', 'obsah prvního atributu první verze první karty');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('7ccd3740-05cc-4af8-a7e4-14bbf5953416', 'a0b2d288-77c3-4546-a452-3332ccc360b8', 1, 'Atribut druhý', 'DOUBLE', '1.11');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('88170539-d9a2-45e3-a7ea-4ba04bc296f4', '54f38d2d-1b5d-42e9-a96b-9bb3d9edeb52', 0, 'Atribut první', 'STRING', 'obsah prvního atributu druhé verze první karty');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('ade90770-7c8b-46d6-8fab-240778e26cb7', '54f38d2d-1b5d-42e9-a96b-9bb3d9edeb52', 1, 'Atribut druhý', 'DOUBLE', '1.11');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('902ab3d4-d15a-4ea3-9996-a83e67668578', '54f38d2d-1b5d-42e9-a96b-9bb3d9edeb52', 2, 'Atribut který nebyl v šabloně', 'DATETIME', 'TODO');

INSERT INTO public.vzb_card_category (card_id, category_id) VALUES('39b3b925-ab6a-43a0-b66b-c8f70f46fb53', '0f3f3623-097c-4ba0-b6ca-32aab3e9989a');
INSERT INTO public.vzb_card_category (card_id, category_id) VALUES('39b3b925-ab6a-43a0-b66b-c8f70f46fb53', 'a838fd24-5322-4702-b537-ee5dbc7a895b');
INSERT INTO public.vzb_card_label (card_id, label_id) VALUES('39b3b925-ab6a-43a0-b66b-c8f70f46fb53', 'ec137bcb-da4e-4eba-8f2c-df2274b639ab');
INSERT INTO public.vzb_card_label (card_id, label_id) VALUES('39b3b925-ab6a-43a0-b66b-c8f70f46fb53', 'e0166151-918c-41fd-8795-853e0f1c0f22');

INSERT INTO public.vzb_card (id, owner_id, created, updated, deleted, pid) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '135e3cec-818b-47da-bc5c-bb83685fbef1', '2019-04-04 23:41:00', '2019-04-04 23:40:00', null, 2);
INSERT INTO public.vzb_card_content (id, previous_version_id, card_id, created, updated, deleted, name, last_version) VALUES('a75bf637-f74d-4c08-9141-cd79f222b43d', null, '9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '2019-04-04 23:41:00', '2019-04-04 23:41:00', null, 'Karta číslo 1 prvního uživatele, první verze', false);
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('aa0de4fa-dcad-4010-bf24-294e08f4a161', 'a75bf637-f74d-4c08-9141-cd79f222b43d', 2, 'Atribut první', 'STRING', 'obsah prvního atributu jediné verze druhé karty');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('b2ca7b1e-adda-445f-a967-c07d7309620a', 'a75bf637-f74d-4c08-9141-cd79f222b43d', 1, 'Atribut druhý', 'DOUBLE', '1.21');
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('1b24e418-641e-4208-969f-c13fc11aedaa', 'a75bf637-f74d-4c08-9141-cd79f222b43d', 0, 'Atribut který nebyl v šabloně', 'DATETIME', 'TODO');

INSERT INTO public.vzb_card_category (card_id, category_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '0f3f3623-097c-4ba0-b6ca-32aab3e9989a');
INSERT INTO public.vzb_card_category (card_id, category_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '9b5c988c-8a85-47f8-aa42-d47c44a9c1ea');
INSERT INTO public.vzb_card_category (card_id, category_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '0b4edaad-3e7e-498f-9320-2cdc3b406e18');
INSERT INTO public.vzb_card_label (card_id, label_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', 'd1c6bd98-557b-4877-87f7-4e216c2c2fd0');
INSERT INTO public.vzb_card_label (card_id, label_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', 'e0166151-918c-41fd-8795-853e0f1c0f22');

INSERT INTO public.vzb_card_linked_card (linking_card_id, linked_card_id) VALUES('9d830831-7ed9-4e12-9cb2-6bd9a59f3d85', '39b3b925-ab6a-43a0-b66b-c8f70f46fb53');

INSERT INTO public.vzb_card (id, owner_id, created, updated, deleted, pid) VALUES('1f1fa094-ddbe-47c4-aef3-7ad77bb20caf', '135e3cec-818b-47da-bc5c-bb83685fbef1', '2019-04-04 23:41:00', '2019-04-04 23:40:00', null, 3);
INSERT INTO public.vzb_card_content (id, previous_version_id, card_id, created, updated, deleted, name, last_version) VALUES('e49a41c7-3a0d-41c2-87fb-754c5d7b21b5', null, '1f1fa094-ddbe-47c4-aef3-7ad77bb20caf', '2019-04-04 23:41:00', '2019-04-04 23:41:00', null, 'Holá karta', false);
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('2e263d54-aad5-48ab-bb90-1487206ce2ac', 'e49a41c7-3a0d-41c2-87fb-754c5d7b21b5', 0, 'Atribut první', 'STRING', null);
INSERT INTO public.vzb_attribute (id, card_content_id, ordinal_number, name, "type", json_value) VALUES('f187f59c-b774-41b1-9cf6-c7f98fb953f1', 'e49a41c7-3a0d-41c2-87fb-754c5d7b21b5', 1, 'Atribut druhý', 'DOUBLE', null);

INSERT INTO public.vzb_card_template (id, owner_id, created, updated, deleted, name) VALUES('1719299c-b138-4768-981f-9a3e045c1242', '135e3cec-818b-47da-bc5c-bb83685fbef1', '2019-04-04 23:41:00', '2019-04-04 23:41:00', null, 'Vzorová šablona');
INSERT INTO public.vzb_attribute_template (id, card_template_id, "type", ordinal_number, name) VALUES('1bfa97e3-0177-4070-b407-ddcf985ade5f', '1719299c-b138-4768-981f-9a3e045c1242', 'STRING', 0, 'Atribut první');
INSERT INTO public.vzb_attribute_template (id, card_template_id, "type", ordinal_number, name) VALUES('33b70bcf-db22-4f09-950d-d72e8943177f', '1719299c-b138-4768-981f-9a3e045c1242', 'DOUBLE', 1, 'Atribut druhý');