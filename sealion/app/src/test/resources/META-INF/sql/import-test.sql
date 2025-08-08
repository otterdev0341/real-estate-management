-- handle default gender table
INSERT INTO genders (id, detail) VALUES
  (UUID(), 'male'),
  (UUID(), 'female'),
  (UUID(), 'gender');

-- handle default role
INSERT INTO roles (id, detail) VALUES
  (UUID(), 'user'),
  (UUID(), 'admin');

-- create default user
INSERT INTO users (
    id, email, password, username, first_name, last_name, gender, dob, role, create_at, updated_at
) VALUES (
    UUID(),                               -- id
    'test@gmail.com',                            -- email
    '$2a$10$Dow1RLZ8PcrUbLx6ZTOD6uHeLlUjxTjKZ2zF1fn3vJoUoEVzRjTz6', -- hashed password: "password"
    'test_username1',                            -- username
    'John',                                      -- first_name
    'Doe',                                       -- last_name
    (SELECT id FROM genders WHERE detail = 'male'), -- gender FK
    TIMESTAMP '1990-01-15 08:00:00',             -- dob
    (SELECT id FROM roles WHERE detail = 'user'), -- role FK
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
