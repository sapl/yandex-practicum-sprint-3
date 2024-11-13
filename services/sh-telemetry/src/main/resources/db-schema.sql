CREATE TABLE IF NOT EXISTS device_telemetry
(
    id          String,
    device_id   String,
    datetime    DateTime('UTC'),
    sensor_name String,
    value       Float32
) engine = ReplacingMergeTree PARTITION BY toYYYYMMDD(datetime)
      ORDER BY (device_id, sensor_name, datetime);
