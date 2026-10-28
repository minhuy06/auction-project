ALTER TABLE users
    ADD COLUMN balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT 'Số dư khả dụng (có thể dùng đặt giá hoặc rút)',
    ADD COLUMN locked_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT 'Số dư đang bị khóa khi dẫn đầu phiên đấu giá',
    ADD CONSTRAINT chk_positive_balance CHECK (balance >= 0.00 AND locked_balance >= 0.00);

CREATE TABLE IF NOT EXISTS wallet_transactions (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   user_id BIGINT NOT NULL,
                                                   auction_id BIGINT NULL COMMENT 'Lưu ID phiên đấu giá nếu giao dịch liên quan đến đấu giá',
                                                   amount DECIMAL(15, 2) NOT NULL COMMENT 'Số tiền biến động (luôn lưu số dương)',
    type ENUM(
                 'DEPOSIT',          -- Nạp tiền vào ví
                 'WITHDRAW',         -- Rút tiền khỏi ví
                 'BID_LOCK',         -- Tạm khóa tiền khi đặt giá thành công
                 'BID_UNLOCK',       -- Hoàn lại tiền khóa khi có người khác trả giá cao hơn
                 'AUCTION_PAYMENT',  -- Trừ tiền thật từ locked_balance khi thắng đấu giá
                 'SELLER_PAYOUT'     -- Cộng tiền cho người bán khi phiên kết thúc
             ) NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    description VARCHAR(255) COMMENT 'Nội dung chi tiết giao dịch',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE SET NULL,
    CONSTRAINT chk_positive_amount CHECK (amount > 0.00)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wallet_tx_user_date ON wallet_transactions(user_id, created_at DESC);