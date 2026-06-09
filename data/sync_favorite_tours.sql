USE webdulich_db;

CREATE TABLE IF NOT EXISTS favorite_tours (
  user_id BIGINT NOT NULL,
  tour_id BIGINT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, tour_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (tour_id) REFERENCES properties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT COUNT(*) AS favorite_tour_count
FROM favorite_tours;
