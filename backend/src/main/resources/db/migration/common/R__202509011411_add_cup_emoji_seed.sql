INSERT INTO cup_emoji (url, created_at, deleted_at)
SELECT 'https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c',
       CURRENT_TIMESTAMP,
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM cup_emoji
                  WHERE url = 'https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c');

INSERT INTO cup_emoji (url, created_at, deleted_at)
SELECT 'https://github.com/user-attachments/assets/393fc8f9-bc46-4856-bfbe-889efc97151e',
       CURRENT_TIMESTAMP,
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM cup_emoji
                  WHERE url = 'https://github.com/user-attachments/assets/393fc8f9-bc46-4856-bfbe-889efc97151e');
