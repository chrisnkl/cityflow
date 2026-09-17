-- 1. Πίνακας Δικαιωμάτων (Permissions)
CREATE TABLE permissions (
                             id UUID PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE
);

-- 2. Πίνακας Ρόλων (Roles)
CREATE TABLE roles (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- Ενδιάμεσος πίνακας Role <-> Permission (Many-to-Many)
CREATE TABLE role_permissions (
                                  role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                  permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

-- 3. Πίνακας Χρηστών (Users)
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Ενδιάμεσος πίνακας User <-> Role (Many-to-Many)
CREATE TABLE user_roles (
                            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- 4. Πίνακας Κατηγοριών Προβλημάτων (Categories)
CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            tip TEXT,
                            enabled BOOLEAN NOT NULL DEFAULT TRUE,
                            radius DOUBLE PRECISION NOT NULL
);

-- 5. Πίνακας Αναφορών Πολιτών (Reports)
CREATE TABLE reports (
                         id UUID PRIMARY KEY,
                         category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
                         status VARCHAR(50) NOT NULL,
                         comments TEXT,
                         photo_url VARCHAR(512),
                         priority DOUBLE PRECISION,

                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITH TIME ZONE,

                         created_by UUID REFERENCES users(id) ON DELETE SET NULL,
                         updated_by UUID REFERENCES users(id) ON DELETE SET NULL
);

-- 6. Πίνακας Γεγονότων Ειδοποιήσεων (Notifications)
CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               latitude DOUBLE PRECISION NOT NULL,
                               longitude DOUBLE PRECISION NOT NULL,

                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

-- Ενδιάμεσος πίνακας Notification <-> Report (Many-to-Many)
CREATE TABLE notification_reports (
                                      notification_id UUID NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
                                      report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
                                      PRIMARY KEY (notification_id, report_id)
);

-- 7. Πίνακας Προσωπικού Inbox Χρηστών (User Notifications)
CREATE TABLE user_notifications (
                                    id UUID PRIMARY KEY,
                                    notification_id UUID NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
                                    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                    is_read BOOLEAN NOT NULL DEFAULT FALSE,

                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    updated_at TIMESTAMP WITH TIME ZONE
);

-- Δημιουργία Ευρετηρίων (Indexes) για βελτιστοποίηση απόδοσης
CREATE INDEX idx_reports_category ON reports(category_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_user_notifications_user ON user_notifications(user_id);
CREATE INDEX idx_user_notifications_read ON user_notifications(user_id, is_read);