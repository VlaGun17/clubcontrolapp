CREATE TYPE computer_type AS ENUM ('Common', 'VIP');
CREATE TYPE computer_status AS ENUM ('Available', 'Busy', 'Maintenance');
CREATE TYPE payment_method AS ENUM ('Cash', 'Card', 'Balance');

CREATE TABLE admins (
    id            UUID PRIMARY KEY,
    username      VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE clients (
    id                UUID PRIMARY KEY,
    nickname          VARCHAR(64) NOT NULL UNIQUE,
    email             VARCHAR(255) NOT NULL UNIQUE,
    balance           DECIMAL(10, 2) DEFAULT 0.00 CHECK (balance >= 0),
    discount_percent  INTEGER DEFAULT 0 CHECK (discount_percent >= 0 AND discount_percent <= 100),
    visit_count       INTEGER DEFAULT 0 CHECK (visit_count >= 0),
    registration_date DATE DEFAULT CURRENT_DATE,
    CONSTRAINT clients_email_check CHECK (email LIKE '%@%')
);

CREATE TABLE computers (
    id           UUID PRIMARY KEY,
    comp_number  INTEGER NOT NULL UNIQUE CHECK (comp_number > 0),
    type         computer_type NOT NULL,
    status       computer_status NOT NULL DEFAULT 'Available'
);

CREATE TABLE tariffs (
    id             UUID PRIMARY KEY,
    name           VARCHAR(64) NOT NULL,
    price_per_hour DECIMAL(10, 2) NOT NULL CHECK (price_per_hour > 0),
    is_night       BOOLEAN DEFAULT FALSE
);

CREATE TABLE sessions (
    id            UUID PRIMARY KEY,
    client_id     UUID NOT NULL,
    computer_id   UUID NOT NULL,
    tariff_id     UUID NOT NULL,
    start_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time      TIMESTAMP,
    total_cost    DECIMAL(10, 2) DEFAULT 0.00 CHECK (total_cost >= 0),
    is_active     BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (computer_id) REFERENCES computers(id) ON DELETE CASCADE,
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id)
);

CREATE TABLE services (
    id    UUID PRIMARY KEY,
    name  VARCHAR(64) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0)
);

CREATE TABLE session_services (
    session_id UUID NOT NULL,
    service_id UUID NOT NULL,
    quantity   INTEGER DEFAULT 1 CHECK (quantity > 0),
    PRIMARY KEY(session_id, service_id),
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

CREATE TABLE payments (
    id           UUID PRIMARY KEY,
    client_id    UUID NOT NULL,
    session_id   UUID NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    method       payment_method NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);