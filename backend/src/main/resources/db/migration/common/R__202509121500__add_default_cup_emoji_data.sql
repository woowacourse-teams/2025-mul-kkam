/* -----------------------------------------------------------
 * 1) default.coffee
 * ----------------------------------------------------------- */
-- 1-1. 구 URL 행을 새로운 URL, code로 업데이트 (활성 행만 고려)
UPDATE cup_emoji
SET url  = 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871',
    code = 'default.coffee'
WHERE url = 'https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c'
  AND deleted_at IS NULL;

-- 1-2. 이미 새로운 URL 행이 존재하되 code 가 비어있다면 보정
UPDATE cup_emoji
SET code = 'default.coffee'
WHERE url  = 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871'
  AND code IS NULL
  AND deleted_at IS NULL;

-- 1-3. 위 두 단계로도 새로운 URL 행이 생성되지 않았다면 새로 삽입
INSERT INTO cup_emoji (url, code, created_at, deleted_at)
SELECT
    'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871',
    'default.coffee',
    CURRENT_TIMESTAMP,
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM cup_emoji
    WHERE url = 'https://github.com/user-attachments/assets/c045e7ff-96b4-4c14-83bc-706112c1e871'
);

/* -----------------------------------------------------------
 * 2) default.water
 * ----------------------------------------------------------- */
-- 2-1. 구 URL 행을 새로운 URL, code로 업데이트 (활성 행만 고려)
UPDATE cup_emoji
SET url  = 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5',
    code = 'default.water'
WHERE url = 'https://github.com/user-attachments/assets/393fc8f9-bc46-4856-bfbe-889efc97151e'
  AND deleted_at IS NULL;

-- 2-2. 이미 새로운 URL 행이 존재하되 code 가 비어있다면 보정
UPDATE cup_emoji
SET code = 'default.water'
WHERE url  = 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5'
  AND code IS NULL
  AND deleted_at IS NULL;

-- 2-3. 위 두 단계로도 새로운 URL 행이 생성되지 않았다면 새로 삽입
INSERT INTO cup_emoji (url, code, created_at, deleted_at)
SELECT
    'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5',
    'default.water',
    CURRENT_TIMESTAMP,
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM cup_emoji
    WHERE url = 'https://github.com/user-attachments/assets/e096b351-c8c1-494e-abad-5c9881af7ef5'
);
