use simulation;

truncate table regional_scalling_fraction;

insert into regional_scalling_fraction (region, year, fraction) VALUES
("US", 2015, 0.1832),
("US", 2020, 0.1740),
("US", 2030, 0.1592),
("US", 2040, 0.1464),
("US", 2050, 0.1348),

("EU", 2015, 0.1346),
("EU", 2020, 0.1273),
("EU", 2030, 0.1135),
("EU", 2040, 0.1023),
("EU", 2050, 0.0925),

("Other developed", 2015, 0.1018),
("Other developed", 2020, 0.0974),
("Other developed", 2030, 0.0911),
("Other developed", 2040, 0.0854),
("Other developed", 2050, 0.0803),

("China", 2015, 0.2453),
("China", 2020, 0.2616),
("China", 2030, 0.2858),
("China", 2040, 0.3042),
("China", 2050, 0.3199),

("India", 2015, 0.0611),
("India", 2020, 0.0651),
("India", 2030, 0.0704),
("India", 2040, 0.0736),
("India", 2050, 0.0753),

("Other developing", 2015, 0.2740),
("Other developing", 2020, 0.2747),
("Other developing", 2030, 0.2801),
("Other developing", 2040, 0.2881),
("Other developing", 2050, 0.2973);
