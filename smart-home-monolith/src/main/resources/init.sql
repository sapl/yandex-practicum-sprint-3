CREATE TABLE IF NOT EXISTS heating_systems
(
    id                  BIGSERIAL PRIMARY KEY,
    is_on               BOOLEAN          NOT NULL,
    target_temperature  DOUBLE PRECISION NOT NULL,
    current_temperature DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS temperature_sensors
(
    id                  BIGSERIAL PRIMARY KEY,
    current_temperature DOUBLE PRECISION NOT NULL,
    last_updated        TIMESTAMP        NOT NULL
);

-- Migration
-- old telemetry data is already migrated to new databases

DROP TABLE temperature_sensors;
ALTER TABLE heating_systems
    ADD COLUMN IF NOT EXISTS iot_hub_device_id VARCHAR(32) NOT NULL;
ALTER TABLE heating_systems
    DROP COLUMN IF EXISTS is_on;
ALTER TABLE heating_systems
    DROP COLUMN IF EXISTS target_temperature;
ALTER TABLE heating_systems
    DROP COLUMN IF EXISTS current_temperature;

INSERT INTO heating_systems (id, iot_hub_device_id)
VALUES (1, 'aaa-bbb-ccc-1')
ON CONFLICT (id) DO NOTHING;

INSERT INTO heating_systems (id, iot_hub_device_id)
VALUES (2, 'aaa-bbb-ccc-2')
ON CONFLICT (id) DO NOTHING;