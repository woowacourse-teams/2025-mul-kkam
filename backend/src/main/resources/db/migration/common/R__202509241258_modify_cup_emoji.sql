-- 기존에 있던 water와 coffee의 순서 변경


-- 배경이 원이었던 이모지들을 네모로 바꾸는 스크립트

-- water
UPDATE cup_emoji
SET url  = 'https://github.com/user-attachments/assets/e3a29279-8c7c-403a-b19f-3035005b00a2'
WHERE url = 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5'
  AND deleted_at IS NULL;

-- coffee
UPDATE cup_emoji
SET url  = 'https://github.com/user-attachments/assets/9a070802-5309-4508-acdb-faec9375e7c7'
WHERE url = 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871'
  AND deleted_at IS NULL;

-- 텀블러 이모지 url 이 존재하지 않으면 삽입
INSERT INTO cup_emoji (url, code, created_at, deleted_at)
SELECT
    'https://github.com/user-attachments/assets/3e948e3b-15db-444b-a7de-df6ef48c7c7e',
    NULL,
    CURRENT_TIMESTAMP,
    NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM cup_emoji
    WHERE url = 'https://github.com/user-attachments/assets/3e948e3b-15db-444b-a7de-df6ef48c7c7e'
);
