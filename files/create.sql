CREATE TABLE users (
    chat_id BIGINT PRIMARY KEY,
    chat_type VARCHAR(10) NOT NULL CHECK (chat_type IN ('private', 'channel', 'group', 'supergroup')),
    chat_name VARCHAR(128),
    user_link VARCHAR(33)
);

CREATE TABLE holidays (
    chat_id BIGINT PRIMARY KEY REFERENCES users(chat_id) ON DELETE CASCADE,
    daily_distribution_time TIME NOT NULL,
    indentation_of_days SMALLINT NOT NULL,
    subscription_is_active BOOLEAN NOT NULL
);

CREATE TABLE custom_holidays (
    custom_holiday_id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL REFERENCES users(chat_id) ON DELETE CASCADE,
    holiday_date SMALLINT NOT NULL,
    holiday_name VARCHAR(50)
);

CREATE INDEX custom_holidays_chat_id_index
ON custom_holidays (chat_id);

CREATE INDEX custom_holidays_holiday_date_index
ON custom_holidays (holiday_date);

CREATE TABLE user_statuses (
    chat_id BIGINT PRIMARY KEY REFERENCES users(chat_id) ON DELETE CASCADE,
    status VARCHAR(64) NOT NULL
);
