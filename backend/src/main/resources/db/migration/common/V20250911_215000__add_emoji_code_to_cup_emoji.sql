ALTER TABLE cup_emoji
    ADD COLUMN `code` VARCHAR(64) NULL;

-- default coffee
INSERT INTO cup_emoji (url, code, created_at, deleted_at)
SELECT 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871',
       'default.coffee',
       CURRENT_TIMESTAMP,
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM cup_emoji
                  WHERE url = 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871');

-- default water
INSERT INTO cup_emoji (url, code, created_at, deleted_at)
SELECT 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5',
       'default.water',
       CURRENT_TIMESTAMP,
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM cup_emoji
                  WHERE url = 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5');
