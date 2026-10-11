INSERT INTO region (code, name) VALUES ('ALA', 'Almaty') ON CONFLICT (code) DO NOTHING;
INSERT INTO region (code, name) VALUES ('AST', 'Astana') ON CONFLICT (code) DO NOTHING;
INSERT INTO region (code, name) VALUES ('SHY', 'Shymkent') ON CONFLICT (code) DO NOTHING;
INSERT INTO region (code, name) VALUES ('TEST', 'Test Region') ON CONFLICT (code) DO NOTHING;
