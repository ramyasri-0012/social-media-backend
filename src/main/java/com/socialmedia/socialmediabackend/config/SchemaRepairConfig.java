package com.socialmedia.socialmediabackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SchemaRepairConfig {

    @Bean
    public CommandLineRunner repairLikesForeignKey(JdbcTemplate jdbcTemplate) {
        return args -> {
            boolean likesExists = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'likes')",
                    Boolean.class
            ));

            if (!likesExists) {
                createLikesTable(jdbcTemplate);
                return;
            }

            String referencedTable = jdbcTemplate.query(
                    """
                    SELECT ccu.table_name
                    FROM information_schema.table_constraints tc
                    JOIN information_schema.key_column_usage kcu
                      ON tc.constraint_name = kcu.constraint_name
                     AND tc.table_schema = kcu.table_schema
                    JOIN information_schema.constraint_column_usage ccu
                      ON ccu.constraint_name = tc.constraint_name
                     AND ccu.table_schema = tc.table_schema
                    WHERE tc.constraint_type = 'FOREIGN KEY'
                      AND tc.table_name = 'likes'
                      AND kcu.column_name = 'post_id'
                    LIMIT 1
                    """,
                    rs -> rs.next() ? rs.getString(1) : null
            );

            if (!"posts".equalsIgnoreCase(referencedTable)) {
                jdbcTemplate.execute("DROP TABLE IF EXISTS likes");
                createLikesTable(jdbcTemplate);
            }
        };
    }

    private void createLikesTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(
                """
                CREATE TABLE IF NOT EXISTS likes (
                    id BIGSERIAL PRIMARY KEY,
                    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                    CONSTRAINT uk_likes_post_user UNIQUE (post_id, user_id)
                )
                """
        );
    }
}
