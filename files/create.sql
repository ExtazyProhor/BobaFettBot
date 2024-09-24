CREATE TABLE users (
    chat_id BIGINT PRIMARY KEY,
    chat_type VARCHAR(10) NOT NULL CHECK (chat_type IN ('private', 'channel', 'group', 'supergroup')),
    chat_name VARCHAR(128) NOT NULL,
    user_link VARCHAR(33)
);

CREATE TABLE holidays (
    chat_id BIGINT PRIMARY KEY REFERENCES users(chat_id) ON DELETE CASCADE,
    daily_distribution_time TIME NOT NULL,
    indentation_of_days SMALLINT NOT NULL,
    subscription_is_active BOOLEAN NOT NULL,
    can_share_custom_holidays BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE custom_holidays (
    custom_holiday_id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL REFERENCES users(chat_id) ON DELETE CASCADE,
    holiday_date DATE NOT NULL,
    holiday_name VARCHAR(50)
);

CREATE TABLE chat_owners (
    chat_id BIGINT PRIMARY KEY REFERENCES users(chat_id) ON DELETE CASCADE,
    owner_id BIGINT NOT NULL REFERENCES users(chat_id) ON DELETE CASCADE
);
