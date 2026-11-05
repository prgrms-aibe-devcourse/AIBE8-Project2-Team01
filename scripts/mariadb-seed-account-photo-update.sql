-- 이미지 원본 30개의 실제 업로드 경로를 확인한 추가 적용 SQL입니다. 운영 DB에는 아직 적용하지 않았습니다.
-- #195 기존 EFY 시드의 닉네임·사진·잘못된 해시와 반복 문구만 보완합니다.
-- 빈 DB용 data-mariadb.sql을 다시 실행하지 않습니다. 새 계정·요청·견적·지갑은 만들지 않습니다.
-- 백업 후 쓰기 요청을 멈춘 상태에서 한 연결로 전체 실행합니다. 첫 오류에서 중단하고 ROLLBACK 하세요.
-- mysql/mariadb의 --force 사용 금지. 직접 바꾼 문구·사진·정상 비밀번호는 유지합니다.
SET NAMES utf8mb4;
CREATE TEMPORARY TABLE seed195_previous_member_photos (email VARCHAR(255), path VARCHAR(255), PRIMARY KEY(email,path)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_photo_replacements (post_id BIGINT PRIMARY KEY, seed_id INT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_guard (ok INT CHECK(ok=1)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_names (email VARCHAR(255) PRIMARY KEY, old_name VARCHAR(255), new_name VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_files (email VARCHAR(255), path VARCHAR(255), name VARCHAR(255), mime VARCHAR(100), bytes BIGINT, PRIMARY KEY(email,path)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_member_photos (email VARCHAR(255) PRIMARY KEY, path VARCHAR(255), old_path VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_work (email VARCHAR(255), path VARCHAR(255), description VARCHAR(200), display_order INT, PRIMARY KEY(email,path)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_posts (seed_id INT PRIMARY KEY, email VARCHAR(255), board VARCHAR(20), old_title VARCHAR(255), old_content TEXT, title VARCHAR(255), content TEXT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_comments (seed_id INT, email VARCHAR(255), old_content TEXT, content TEXT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_post_photos (seed_id INT, path VARCHAR(255), sort_order INT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_old_post_photos (seed_id INT, path VARCHAR(255), sort_order INT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_post_targets (seed_id INT, post_id BIGINT PRIMARY KEY) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_member_pending (member_id BIGINT PRIMARY KEY, email VARCHAR(255), path VARCHAR(255), old_image_id BIGINT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_work_pending (profile_id BIGINT PRIMARY KEY, email VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_post_pending (post_id BIGINT PRIMARY KEY, seed_id INT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE TEMPORARY TABLE seed195_needed (email VARCHAR(255), path VARCHAR(255), PRIMARY KEY(email,path)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;


INSERT INTO seed195_names VALUES
('admin11@example.com','팀원1관리자1','다온관리자'),
('admin12@example.com','팀원1관리자2','라온관리자'),
('admin21@example.com','팀원2관리자1','해솔관리자'),
('admin22@example.com','팀원2관리자2','이든관리자'),
('admin31@example.com','팀원3관리자1','가온관리자'),
('admin32@example.com','팀원3관리자2','나래관리자'),
('admin41@example.com','팀원4관리자1','유찬관리자'),
('admin42@example.com','팀원4관리자2','소율관리자'),
('admin51@example.com','팀원5관리자1','하람관리자'),
('admin52@example.com','팀원5관리자2','윤슬관리자'),
('customer11@example.com','팀원1고객1','다민고객'),
('customer12@example.com','팀원1고객2','하린고객'),
('customer19@example.com','팀원1탈퇴테스트','별하고객'),
('customer21@example.com','팀원2고객1','서준고객'),
('customer22@example.com','팀원2고객2','지우고객'),
('customer29@example.com','팀원2탈퇴테스트','로아고객'),
('customer31@example.com','팀원3고객1','유나고객'),
('customer32@example.com','팀원3고객2','도윤고객'),
('customer39@example.com','팀원3탈퇴테스트','여름고객'),
('customer41@example.com','팀원4고객1','수아고객'),
('customer42@example.com','팀원4고객2','시우고객'),
('customer49@example.com','팀원4탈퇴테스트','온유고객'),
('customer51@example.com','팀원5고객1','지안고객'),
('customer52@example.com','팀원5고객2','하준고객'),
('customer59@example.com','팀원5탈퇴테스트','은솔고객'),
('expert11@example.com','팀원1고수1','민서고수'),
('expert12@example.com','팀원1고수2','지호고수'),
('expert21@example.com','팀원2고수1','서연고수'),
('expert22@example.com','팀원2고수2','이준고수'),
('expert31@example.com','팀원3고수1','채원고수'),
('expert32@example.com','팀원3고수2','도현고수'),
('expert41@example.com','팀원4고수1','예린고수'),
('expert42@example.com','팀원4고수2','은우고수'),
('expert51@example.com','팀원5고수1','서아고수'),
('expert52@example.com','팀원5고수2','수호고수');

INSERT INTO seed195_files VALUES
('expert1@example.com','5daa003e-1e20-4848-81d7-c3a418cecf91.jpg','promo-moving-1.jpg','image/jpeg',300000),
('seed-english@example.com','abd56987-aebe-47d2-8f7c-48df5f14bb94.jpg','promo-english-1.jpg','image/jpeg',300000),
('seed-dev@example.com','0f11d56a-67be-49b3-8573-7a97700a7f4c.jpg','promo-dev-1.jpg','image/jpeg',300000),
('expert2@example.com','12904168-fe15-4dc8-9df6-ddc507020606.jpg','promo-aircon-1.jpg','image/jpeg',300000),
('seed-clean@example.com','d8c3a907-ba6a-469d-8093-4e69d9cf5284.jpg','tip-clean.jpg','image/jpeg',300000),
('seed-plumb@example.com','ebea7963-5a18-4ac7-87db-f75fc6cfb701.jpg','tip-boiler.jpg','image/jpeg',300000),
('expert1@example.com','73c6b070-7b0c-493b-8af0-894c14c0c78c.jpg','tip-packing.jpg','image/jpeg',300000),
('seed-interior@example.com','74c3a957-9638-4644-962c-3eb84f6d090b.jpg','tip-floor.jpg','image/jpeg',300000),
('seed-photo@example.com','2f5d05bc-bad6-42c7-bdb6-dbae87d162e8.jpg','tip-wedding.jpg','image/jpeg',300000),
('seed-english@example.com','d40e132c-5fe4-4565-8427-859a847dca8a.jpg','tip-words.jpg','image/jpeg',300000),
('seed-dev@example.com','434ee595-4316-4306-8b89-be7d42425ab9.jpg','tip-docs.jpg','image/jpeg',300000),
('expert1@example.com','50347254-5e8f-4b63-a33b-082d42401d16.jpg','promo-moving-2.jpg','image/jpeg',300000),
('expert2@example.com','797b95bd-3896-4109-bbf3-4912b12e72b1.jpg','tip-aircon.jpg','image/jpeg',300000),
('hangyeol.move@example.com','seed/expert-portraits/fac79094f403daa49b653ca7165425239106e33de7c46cbf9943e0afb06594f2.jpg','expert-101.jpg','image/jpeg',151214),
('sparkle.home@example.com','seed/expert-portraits/3a3b36b5e571b7df3f0cf9f556eeeb547c68adde6d77b47b79f3cdb8ec47b31b.jpg','expert-102.jpg','image/jpeg',170465),
('yoon.english@example.com','seed/expert-portraits/2536fa08e550e96a256fe8288f3d09b17d055949df054a541ef09de97fc31421.jpg','expert-103.jpg','image/jpeg',213066),
('sori.music@example.com','seed/expert-portraits/d01c2618fe2ab4a4b3e5a4788a830ea6cd49651d876de7fea68e776e75397b43.jpg','expert-104.jpg','image/jpeg',188637),
('seed-dev@example.com','seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg','expert-105.jpg','image/jpeg',170844),
('seed-plumb@example.com','seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg','expert-106.jpg','image/jpeg',202546),
('onjip@example.com','seed/expert-portraits/91d49f183011359c7be3278ef88d06a0e2b85ae4f75d28985065fad7c180abfe.jpg','expert-107.jpg','image/jpeg',176534),
('damda.snap@example.com','seed/expert-portraits/bf61066aa0ba76c96797534f2ac306cfdc8e32a7c14b7adc4e052be7744cee4b.jpg','expert-108.jpg','image/jpeg',152721),
('tidyday@example.com','seed/expert-portraits/2c6b30f4271e6fe0de0876b2c58147e797c16951f31db4eb2497a6d68ee8efb6.jpg','expert-109.jpg','image/jpeg',179817),
('seed-clean@example.com','064360f9-82e8-4e0c-b723-a2062cc1a375.jpg','promo-clean-1.jpg','image/jpeg',300000),
('kimcoach@example.com','seed/expert-portraits/9e0a013410c73d2600f30f7764042c664597f65dc75fec784602f011101dc86b.jpg','expert-110.jpg','image/jpeg',153418),
('doorknob@example.com','seed/expert-portraits/730c36a224c75ad6716687b5e3412b10018f5ac5fc95c388df95f7d52852bf55.jpg','expert-111.jpg','image/jpeg',187770),
('haeundae.move@example.com','seed/expert-portraits/8236bea6ade5759f4a467ae33aad1dfb8b0a8497fa0925396e2bea41ef4c56a7.jpg','expert-112.jpg','image/jpeg',223823),
('career.note@example.com','seed/expert-portraits/3dbb32e8d3411c87a6e6a0b69329bfd6beb96b11cc057a9e906c73418b43fa09.jpg','expert-113.jpg','image/jpeg',163175),
('forest.art@example.com','seed/expert-portraits/cd044537eb5b2ec44fa95ecd26f478034191e1add421270a26e57fb771200d50.jpg','expert-114.jpg','image/jpeg',232104),
('saegil@example.com','seed/expert-portraits/61a80e03ceb23a12806b88a229bb551f8049954dbca07e60d4deee5dac2123d2.jpg','expert-115.jpg','image/jpeg',162192),
('mindcomma@example.com','seed/expert-portraits/6a49ba904e011b7af641619aff3198364b1274e647ab0068c9396331f6ec88ab.jpg','expert-116.jpg','image/jpeg',191691),
('hanbit.car@example.com','seed/expert-portraits/7e4aaf88726a0226a2685864377b24821ee4b03de50f4279e7e22745f2ea4a46.jpg','expert-117.jpg','image/jpeg',197828),
('cleargrout@example.com','seed/expert-portraits/51d1c0e5c1940e4304e24b58465dbfc1defd5480104064407d108fd84c73b6d1.jpg','expert-118.jpg','image/jpeg',190855),
('haneul.wed@example.com','seed/expert-portraits/1b8f1285627f6d0db896d8bf6a61d7af770a50b3bc1750080173bae5c0cf1121.jpg','expert-119.jpg','image/jpeg',303645),
('seed-clean@example.com','50f09811-0f6c-4624-a85a-6f2f95294a83.jpg','promo-clean-2.jpg','image/jpeg',300000),
('pilates.today@example.com','seed/expert-portraits/f3f7685df4a9d41458c4e1aac7d34397e6ba4bb301d6d63a6527c02968caeb9b.jpg','expert-120.jpg','image/jpeg',173333),
('hangyeol.move@example.com','seed/expert-work/16de8671c1456f24e95274b607c9bbbe-101-0.jpg','promo-moving-1.jpg','image/jpeg',194916),
('hangyeol.move@example.com','seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-101-1.jpg','promo-moving-2.jpg','image/jpeg',183928),
('sparkle.home@example.com','seed/expert-work/bfd792b1d7fa53d5c2190c964aa29798-102-0.jpg','promo-clean-1.jpg','image/jpeg',142658),
('sparkle.home@example.com','seed/expert-work/ac44f8883c34a4deae39fc8b6cb82da0-102-1.jpg','promo-clean-2.jpg','image/jpeg',138881),
('yoon.english@example.com','seed/expert-work/216cbc29b8a476ed0a26d81d837f46df-103-0.jpg','promo-english-1.jpg','image/jpeg',192334),
('seed-dev@example.com','seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg','promo-dev-1.jpg','image/jpeg',139255),
('seed-plumb@example.com','seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg','promo-boiler-1.jpg','image/jpeg',178861),
('onjip@example.com','seed/expert-work/3d3327ec26e5d74025e457aeca358912-107-0.jpg','promo-interior-1.jpg','image/jpeg',209409),
('onjip@example.com','seed/expert-work/fe32925474575a143b11465fc473b193-107-1.jpg','promo-interior-2.jpg','image/jpeg',126840),
('seed-plumb@example.com','aa76c2cf-be28-4f5c-99cc-197b0e69c1d4.jpg','promo-boiler-1.jpg','image/jpeg',300000),
('damda.snap@example.com','seed/expert-work/e1b4e7d25f66716ef603bffdce076b86-108-0.jpg','promo-snap-1.jpg','image/jpeg',94193),
('damda.snap@example.com','seed/expert-work/e5d6e1eaa9c945df8921783233acafee-108-1.jpg','promo-snap-2.jpg','image/jpeg',89410),
('tidyday@example.com','seed/expert-work/692893d76be445e26009371fd2909ae9-109-0.jpg','promo-aircon-1.jpg','image/jpeg',175712),
('haeundae.move@example.com','seed/expert-work/16de8671c1456f24e95274b607c9bbbe-112-0.jpg','promo-moving-1.jpg','image/jpeg',194916),
('haeundae.move@example.com','seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-112-1.jpg','promo-moving-2.jpg','image/jpeg',183928),
('cleargrout@example.com','seed/expert-work/3d3327ec26e5d74025e457aeca358912-118-0.jpg','promo-interior-1.jpg','image/jpeg',209409),
('cleargrout@example.com','seed/expert-work/fe32925474575a143b11465fc473b193-118-1.jpg','promo-interior-2.jpg','image/jpeg',126840),
('expert1@example.com','seed/accounts/f6e5c71632281a5da3e28abd5163ac7fc5b2382390702d1a741501ae3a685ba2.jpg','expert-101.jpg','image/jpeg',151214),
('expert1@example.com','seed/accounts/6663043958194795b3f3bf764c6c9c45bf96b7e7bf72ecc34466de5ad413c68a.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert1@example.com','seed/accounts/5bfdce0be94405bfdcfb40c459256aebccc84f9246bd46a179b0f2014748a17c.jpg','promo-moving-2.jpg','image/jpeg',183928),
('seed-interior@example.com','83d5c398-973d-497f-b61e-b9e2c0188532.jpg','promo-interior-1.jpg','image/jpeg',300000),
('expert2@example.com','seed/accounts/34aef27b58d794f83dad398391d039494aba403cacfe007bf7d915d8a91980ae.jpg','expert-105.jpg','image/jpeg',170844),
('expert2@example.com','seed/accounts/1a98131222651fa1a1069a3429e2db3b6daf4412dc16c4195999e12bf3cbb55d.jpg','promo-dev-1.jpg','image/jpeg',139255),
('expert11@example.com','seed/accounts/8d787add3b31b9da0c79f0db28fb5e38c88a44ceb09d1351c39b660b14367965.jpg','expert-101.jpg','image/jpeg',151214),
('expert11@example.com','seed/accounts/1bc318f083d1247f4c121e206ee55233a9de868965fb49890f7822e34969cef5.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert11@example.com','seed/accounts/529ab8f7912c5047b2b1801f0df11720249c9c99936c0d605be8fc2421c67c8e.jpg','promo-moving-2.jpg','image/jpeg',183928),
('expert11@example.com','seed/accounts/4ea93c54a0470a6d410969ed4e282b1857b8d5ee39b1bc39497aed69c3b255ef.jpg','promo-clean-1.jpg','image/jpeg',142658),
('expert12@example.com','seed/accounts/e70b2c90bde5da48c107166fcc43f45c89d901a0752323cd1d078d48473b8a8c.jpg','expert-105.jpg','image/jpeg',170844),
('expert12@example.com','seed/accounts/e80f5e65ee4dac108a994b09df8ef512c4a5fe4b84621f7310fa8c82cb67f77f.jpg','promo-dev-1.jpg','image/jpeg',139255),
('expert12@example.com','seed/post-previews/4edd3c547767b74ce5b8500a47f2a5cc45a1a0c7f78ea85452c91137b9c33f04.jpg','post-preview.jpg','image/jpeg',170844),
('expert21@example.com','seed/accounts/891055d72814f52b305ef68907ead3ecaaa610c6470026fce15ceaf3f3f9a355.jpg','expert-101.jpg','image/jpeg',151214),
('seed-interior@example.com','a5cd84ed-18ac-48e1-9a90-71e60abcaf05.jpg','promo-interior-2.jpg','image/jpeg',300000),
('expert21@example.com','seed/accounts/b8ec0bb7ca58c6147dc7a508c238ad9fc6511a3402173a807213ad52b40b11f5.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert21@example.com','seed/accounts/18b6e3471ef9f9bd83fed561c1df7f84c6fb48c420d7806f2f87f2ea96197bed.jpg','promo-moving-2.jpg','image/jpeg',183928),
('expert21@example.com','seed/post-previews/d66799a8c5f442fc5050a24e44ffd58014e11221e5ca7449ea8e6fcddf086b9e.jpg','post-preview.jpg','image/jpeg',138881),
('expert22@example.com','seed/accounts/2319b7f87ff6decd4cb119215d4c8d2724f1328d2be11374aafbd23461e3197e.jpg','expert-105.jpg','image/jpeg',170844),
('expert22@example.com','seed/accounts/fc86f1b5a60b0d8faa97de25dfc583048d6de10815722587cb2961fe69ed81e6.jpg','promo-dev-1.jpg','image/jpeg',139255),
('expert31@example.com','seed/accounts/6af64aa4c438ee2b70dfa9c05f29c5bfabb0d15fcab9cddd1aab3aa5355d59b7.jpg','expert-101.jpg','image/jpeg',151214),
('expert31@example.com','seed/accounts/fcceae98a52d7b28ed6e7b6258a85e9144977ac247c91faaae851b4196414b90.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert31@example.com','seed/accounts/02acf0e4c33e04bf6be318444416c6b9b0c7753a0e0b0614430731eeca43ba78.jpg','promo-moving-2.jpg','image/jpeg',183928),
('expert31@example.com','seed/post-previews/9de869d4366bf5b3c157c0a589f35583ba4ec6d3585ade73adcd933660e4b079.jpg','post-preview.jpg','image/jpeg',170465),
('expert32@example.com','seed/accounts/a0ec1c0db10d72d255d24964764910c4b87dd46f87cbd3c39aec31fa6e8dd7ed.jpg','expert-105.jpg','image/jpeg',170844),
('seed-photo@example.com','10cd17fb-a6bc-456e-919d-3f8270397af3.jpg','promo-snap-1.jpg','image/jpeg',300000),
('expert32@example.com','seed/accounts/8b86c275ce6b7ad07e155462ffa33b672553fb4dad2910535f0349c61fb3abdd.jpg','promo-dev-1.jpg','image/jpeg',139255),
('expert41@example.com','seed/accounts/6784d3b303865177f03afd7a1b3a7bc461c568cf927df6a5d2d196c9472d15ad.jpg','expert-101.jpg','image/jpeg',151214),
('expert41@example.com','seed/accounts/e0b9a48ba99ea0fdab51f073fe5c17b1aadda6058dc873b83846689fdbf16342.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert41@example.com','seed/accounts/3e75e6866e2a65ed8ac86e8271015ab80a107261f21618d78db349687aa11730.jpg','promo-moving-2.jpg','image/jpeg',183928),
('expert41@example.com','seed/post-previews/1ff3186710886e31e53e44b8c6cc649f9c4db1dc858134bf38a21d46656b53b2.jpg','post-preview.jpg','image/jpeg',179817),
('expert42@example.com','seed/accounts/fd31c6d9904f7977af33c34b65ad09e898c64894178dc942773ae4d38f649594.jpg','expert-105.jpg','image/jpeg',170844),
('expert42@example.com','seed/accounts/d2398d0ca1f40c7d45fe00ccea3cc3b86e8a6ea76f1982292f94fe0d55a2c1d8.jpg','promo-dev-1.jpg','image/jpeg',139255),
('expert51@example.com','seed/accounts/13558b062e6eda5178b4f995bad393f52f1f1cc1b78c5fb4f0b1098130baae52.jpg','expert-101.jpg','image/jpeg',151214),
('expert51@example.com','seed/accounts/87c049648877fddc62072f492135a948b34154947fba9cdb061c046f5e0b2562.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert51@example.com','seed/accounts/ea0c2a75636e77d9f034aaa6028e6cef5b658f7e8f19fe93ce5e86cc11bf2b6a.jpg','promo-moving-2.jpg','image/jpeg',183928),
('seed-photo@example.com','ab8834a5-3e84-4704-8069-ea8b9233458e.jpg','promo-snap-2.jpg','image/jpeg',300000),
('expert51@example.com','seed/post-previews/c989c59da9f6b43d3388ae983b4179e61351280272ed803e0bba226702c7a4d3.jpg','post-preview.jpg','image/jpeg',209409),
('expert52@example.com','seed/accounts/57834dd3f824816583300b0d7b594d79cf4de16512d96d99cbb93fa26ee4c820.jpg','expert-105.jpg','image/jpeg',170844),
('expert52@example.com','seed/accounts/c91458d9ec331ccac201adcde487fff49023e92275c39ae7d9d23bae11f2fd19.jpg','promo-dev-1.jpg','image/jpeg',139255),
('codemate@example.com','seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg','expert-105.jpg','image/jpeg',170844),
('bareun.fix@example.com','seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg','expert-106.jpg','image/jpeg',202546),
('codemate@example.com','seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg','promo-dev-1.jpg','image/jpeg',139255),
('bareun.fix@example.com','seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg','promo-boiler-1.jpg','image/jpeg',178861),
('expert@example.com','seed/presentation-profiles/d09431ee697649ae6a059995d9b03eb0069d86a3cd788b5e83c28ac8be9c1cee.png','expert01.png','image/png',2516830),
('expert@example.com','seed/accounts/b012c926b84cf2ee2a937c177623ab226b4488a88bfdc3c6c56beea9758a996a.jpg','promo-moving-1.jpg','image/jpeg',194916),
('expert@example.com','seed/accounts/b6e088cfef152f5ec185163f26e75a3d76f716d10f4056e84d994187b77aa6c7.jpg','promo-moving-2.jpg','image/jpeg',183928),
('seojun.move@example.com','seed/presentation-profiles/816310cf78c8685bc578edcce6dbfa93006ca553ec39927d11adc18f3329e688.png','expert02.png','image/png',3121523),
('seojun.move@example.com','seed/accounts/9aa6442eda00a350e89f8c1dede7dfa374ccc7be0121f79e38866d076eeb5550.jpg','promo-moving-1.jpg','image/jpeg',194916),
('seojun.move@example.com','seed/accounts/de9119d5860ef6201aa97df9dd2712c92f03bfd8bf9c2da8cfa55a80fbcd26ff.jpg','promo-moving-2.jpg','image/jpeg',183928),
('doyoon.move@example.com','seed/presentation-profiles/c002942b04c1c93a16833549018ac701f37ab755b019724cc8939833b041f29c.png','expert03.png','image/png',2579092),
('doyoon.move@example.com','seed/accounts/635432912a9746c58549034e8731f640cad6edf99d75487d1904da587f8bb18b.jpg','promo-moving-1.jpg','image/jpeg',194916),
('doyoon.move@example.com','seed/accounts/210cc985df03b8cab78690e5f80c51d93cea536a526e533d31024db98b9faf95.jpg','promo-moving-2.jpg','image/jpeg',183928),
('subin.clean@example.com','seed/presentation-profiles/3a55a6d8ce7cd546f333debd38cd2c8b406bf5293b134b08011246c756c56a02.png','expert04.png','image/png',2116477),
('subin.clean@example.com','seed/accounts/549fa5cdae210603006333b0e15da64284deea0779ef56315d62bda8439eebd0.jpg','promo-clean-1.jpg','image/jpeg',142658),
('subin.clean@example.com','seed/accounts/54cee6db588cb25f542ddef6f875e5ccaae9af6c539b632d3a8919eb81ba1795.jpg','promo-clean-2.jpg','image/jpeg',138881),
('jian.clean@example.com','seed/presentation-profiles/4446b1ab54cd891731dd0f5b2b3f52296ba34bb249d1da53b5568d48e780340e.png','expert05.png','image/png',2352282),
('jian.clean@example.com','seed/accounts/b3feea8bc591fc46962067a6d9af6da5f65a90d6a4aa2da601c968ed25e7d3be.jpg','promo-clean-1.jpg','image/jpeg',142658),
('jian.clean@example.com','seed/accounts/016609033f8011e1203ebd6343ad395e636b110bd68aba4b5da17326d31e7532.jpg','promo-clean-2.jpg','image/jpeg',138881),
('hayoon.clean@example.com','seed/presentation-profiles/e550aedb1375b19f2a9a3adf6ac87333c3368d11938456a6ac2932837773be23.png','expert06.png','image/png',2258333),
('hayoon.clean@example.com','seed/accounts/3961ddbd344a2fce7e47d282672f39282c303ade835c7bc3bfb7c068c0550fc0.jpg','promo-clean-1.jpg','image/jpeg',142658),
('hayoon.clean@example.com','seed/accounts/2c3ae4a8b6be8e0507f61a7fb968c11d56c9401d924050cc73c1f8bc6772964e.jpg','promo-clean-2.jpg','image/jpeg',138881),
('jio.web@example.com','seed/presentation-profiles/ac87b492952a1e806cfd07c8b34d28036c3eaac9882f10ed109eb4c1ee12cd4e.png','expert07.png','image/png',2218128),
('jio.web@example.com','seed/accounts/fdd784d615f30ce8fd509a65507304a5faafbb180b686f2d188c4ec0343b406f.jpg','promo-dev-1.jpg','image/jpeg',139255),
('hyunwoo.dev@example.com','seed/presentation-profiles/eb98873dad0c9294e1e20be39ad20bfe1221e64fb07bab9d478455f71f412833.png','expert08.png','image/png',2033824),
('hyunwoo.dev@example.com','seed/accounts/2fc29d0f0ea4964e0500616fd9683dedbf5dfbb1f2188a70d973cddae6bf92b8.jpg','promo-dev-1.jpg','image/jpeg',139255),
('seoa.design@example.com','seed/presentation-profiles/ba4360ddbbd45220d6b76ae0bf036a745fc9e5209880534584c5b2733d112b68.png','expert09.png','image/png',2184096),
('seoa.design@example.com','seed/accounts/29dbe8f14c71b14762bab6d01ec486000ac2ea1c4efdb13c2c935afc45100b6f.jpg','promo-dev-1.jpg','image/jpeg',139255),
('yunseo.web@example.com','seed/presentation-profiles/710d56a3e6311981449bab84ec340139a77e7a6d6813c2d00282c72e2a65c032.png','expert10.png','image/png',2582851),
('yunseo.web@example.com','seed/accounts/7cd1d484abf3836fe0041ff7085555aa73b2bcfbab3a7039672952eb567e4b23.jpg','promo-dev-1.jpg','image/jpeg',139255);

INSERT INTO seed195_member_photos VALUES
('expert2@example.com','seed/accounts/34aef27b58d794f83dad398391d039494aba403cacfe007bf7d915d8a91980ae.jpg','seed/accounts/f1339da8ce1cd8c6daf70acae2713900db423f1b830b982508ea6d866b235081.jpg'),
('mindcomma@example.com','seed/expert-portraits/6a49ba904e011b7af641619aff3198364b1274e647ab0068c9396331f6ec88ab.jpg','seed/expert-profiles/c898b516436f0720d021db2b5ab3021a033f26ccc490c82a93bf01403b30ce3a.jpg'),
('kimcoach@example.com','seed/expert-portraits/9e0a013410c73d2600f30f7764042c664597f65dc75fec784602f011101dc86b.jpg','seed/expert-profiles/d6df33572f3f4c3a8168af7114cc989710c9f32d62f10d5238be01f235b8542e.jpg'),
('yoon.english@example.com','seed/expert-portraits/2536fa08e550e96a256fe8288f3d09b17d055949df054a541ef09de97fc31421.jpg','seed/expert-profiles/7a298dc757041f1c6cbf78295263cc0e25c906a28a86e0c10b0dc9381a68b098.jpg'),
('onjip@example.com','seed/expert-portraits/91d49f183011359c7be3278ef88d06a0e2b85ae4f75d28985065fad7c180abfe.jpg','seed/expert-profiles/5c8c6e0c8a0ad75febadbc88b59c9a13ae1525e06a564c21ac368085f840b557.jpg'),
('saegil@example.com','seed/expert-portraits/61a80e03ceb23a12806b88a229bb551f8049954dbca07e60d4deee5dac2123d2.jpg','seed/expert-profiles/e863f6c662a4aacfc0a8615e58956e0c14fa0f3fadbd49205a4b133b585f5064.jpg'),
('seed-plumb@example.com','seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg','seed/expert-profiles/11971b4bd9d62252bb8fb03646c3bbe99cfe86660147b984c65ac082d5acbfb8.jpg'),
('hanbit.car@example.com','seed/expert-portraits/7e4aaf88726a0226a2685864377b24821ee4b03de50f4279e7e22745f2ea4a46.jpg','seed/expert-profiles/7cdeb1c265b796af7accd53ebc2b95e23f15951040c23fa2660042a9c2f66511.jpg'),
('haeundae.move@example.com','seed/expert-portraits/8236bea6ade5759f4a467ae33aad1dfb8b0a8497fa0925396e2bea41ef4c56a7.jpg','seed/expert-profiles/add13d2afd287a6e256ff4a48bbd03790caf5439232620b17e44f7460541c5ad.jpg'),
('tidyday@example.com','seed/expert-portraits/2c6b30f4271e6fe0de0876b2c58147e797c16951f31db4eb2497a6d68ee8efb6.jpg','seed/expert-profiles/f0282303613ec56e913cf46ea95548d59df5102b0d368d8e9c3be025e32af0d7.jpg'),
('damda.snap@example.com','seed/expert-portraits/bf61066aa0ba76c96797534f2ac306cfdc8e32a7c14b7adc4e052be7744cee4b.jpg','seed/expert-profiles/0b9ff5c15aa191c59cdb086076497351af362e3c358f63eb6e255730e3e8db7f.jpg'),
('forest.art@example.com','seed/expert-portraits/cd044537eb5b2ec44fa95ecd26f478034191e1add421270a26e57fb771200d50.jpg','seed/expert-profiles/a09c9e34c10da8def776d94ac1f0b0d677124888accc6086e99f55914cf794f8.jpg'),
('pilates.today@example.com','seed/expert-portraits/f3f7685df4a9d41458c4e1aac7d34397e6ba4bb301d6d63a6527c02968caeb9b.jpg','seed/expert-profiles/56fb326aeb68bf352bafa49e107b0a5294f796ecc5e87c89eda1bbaca96a1866.jpg'),
('sparkle.home@example.com','seed/expert-portraits/3a3b36b5e571b7df3f0cf9f556eeeb547c68adde6d77b47b79f3cdb8ec47b31b.jpg','seed/expert-profiles/4d9e4f1b2017b2029e351c445dec43f44864b4da4be785c88dc7d95e5210d202.jpg'),
('sori.music@example.com','seed/expert-portraits/d01c2618fe2ab4a4b3e5a4788a830ea6cd49651d876de7fea68e776e75397b43.jpg','seed/expert-profiles/7ea0e5cf6d09e16e8f06e4e328e3a816d3b767a3f84bfba5eb33fe86025fb87a.jpg'),
('doorknob@example.com','seed/expert-portraits/730c36a224c75ad6716687b5e3412b10018f5ac5fc95c388df95f7d52852bf55.jpg','seed/expert-profiles/e6c78e0d7451448ed80ea8b6c53b8642838b6c93c77b8cf55af4abd470351533.jpg'),
('seed-dev@example.com','seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg','seed/expert-profiles/31055f32bcdc8c99452edee7f3b116878cba7d3369df93d3c90c765e92b3e99f.jpg'),
('haneul.wed@example.com','seed/expert-portraits/1b8f1285627f6d0db896d8bf6a61d7af770a50b3bc1750080173bae5c0cf1121.jpg','seed/expert-profiles/c0c93434c9ea6be62031a86f8b56ddb7d5eeb245a31b2aff54859409483b7e1f.jpg'),
('cleargrout@example.com','seed/expert-portraits/51d1c0e5c1940e4304e24b58465dbfc1defd5480104064407d108fd84c73b6d1.jpg','seed/expert-profiles/cac85a3ea10030c3209c530950e0af9b8fcec2e578fd0ef0c2ac9df7ed101551.jpg'),
('hangyeol.move@example.com','seed/expert-portraits/fac79094f403daa49b653ca7165425239106e33de7c46cbf9943e0afb06594f2.jpg','seed/expert-profiles/4bcfa1945637f3065184a2298d80337309f6ec70e83c0dc2e3e3257b341fd18d.jpg'),
('career.note@example.com','seed/expert-portraits/3dbb32e8d3411c87a6e6a0b69329bfd6beb96b11cc057a9e906c73418b43fa09.jpg','seed/expert-profiles/51a78cd14d3261add2fad59530b0856e14c62824e4e85577e0d91879bb25f0c7.jpg'),
('expert1@example.com','seed/accounts/f6e5c71632281a5da3e28abd5163ac7fc5b2382390702d1a741501ae3a685ba2.jpg','seed/accounts/27c4cfd39805c310ae8c00fe30b8419a7ad85ca6337bed2f0d30d211f47c9da6.jpg'),
('expert31@example.com','seed/accounts/6af64aa4c438ee2b70dfa9c05f29c5bfabb0d15fcab9cddd1aab3aa5355d59b7.jpg','seed/accounts/84f00997838ef713893c14c9c404a26b2650f235c0b18a51e9109fbaf08db1b5.jpg'),
('expert32@example.com','seed/accounts/a0ec1c0db10d72d255d24964764910c4b87dd46f87cbd3c39aec31fa6e8dd7ed.jpg','seed/accounts/58dc7391fea808471231ecfbc1f66498b43d7b5534d8476debb5810c6e27db83.jpg'),
('expert51@example.com','seed/accounts/13558b062e6eda5178b4f995bad393f52f1f1cc1b78c5fb4f0b1098130baae52.jpg','seed/accounts/42a71e95ac658972b4104b3a65e685501da66ee681a01f87529f9d01977ba319.jpg'),
('expert52@example.com','seed/accounts/57834dd3f824816583300b0d7b594d79cf4de16512d96d99cbb93fa26ee4c820.jpg','seed/accounts/052e2c51e0b4a5f1c3f0626ab73b3fd3768ba296f3e299b83f32ab07e92bada4.jpg'),
('expert21@example.com','seed/accounts/891055d72814f52b305ef68907ead3ecaaa610c6470026fce15ceaf3f3f9a355.jpg','seed/accounts/bac441a78bb7454428c3b59c2f37b5f304dddd75e9ab3b09ac183f76eac284f0.jpg'),
('expert22@example.com','seed/accounts/2319b7f87ff6decd4cb119215d4c8d2724f1328d2be11374aafbd23461e3197e.jpg','seed/accounts/bc3c4aeed30e7e75e8084af4e85e620b856fe5ea69e7c4a6803b56db3bf27572.jpg'),
('expert11@example.com','seed/accounts/8d787add3b31b9da0c79f0db28fb5e38c88a44ceb09d1351c39b660b14367965.jpg','seed/accounts/c65e8077012f84f40dd159979ca2b957886bc41a93f159794869109248ebe7a2.jpg'),
('expert12@example.com','seed/accounts/e70b2c90bde5da48c107166fcc43f45c89d901a0752323cd1d078d48473b8a8c.jpg','seed/accounts/80cbdf6357c5a9de9e6cd7bfbd3810f30e0ddf30e29d0f74c171eeb3f434bbd5.jpg'),
('expert42@example.com','seed/accounts/fd31c6d9904f7977af33c34b65ad09e898c64894178dc942773ae4d38f649594.jpg','seed/accounts/44d9339fec698f229d1f6bc2ba8124db6947a12a9fffbb13ac08a027d8555315.jpg'),
('expert41@example.com','seed/accounts/6784d3b303865177f03afd7a1b3a7bc461c568cf927df6a5d2d196c9472d15ad.jpg','seed/accounts/2e7b71e9b67f7c3785c7a0c566f7fc10460881d87307086f3aaeff7e9160d4b4.jpg'),
('expert@example.com','seed/presentation-profiles/d09431ee697649ae6a059995d9b03eb0069d86a3cd788b5e83c28ac8be9c1cee.png',NULL),
('seojun.move@example.com','seed/presentation-profiles/816310cf78c8685bc578edcce6dbfa93006ca553ec39927d11adc18f3329e688.png',NULL),
('doyoon.move@example.com','seed/presentation-profiles/c002942b04c1c93a16833549018ac701f37ab755b019724cc8939833b041f29c.png',NULL),
('subin.clean@example.com','seed/presentation-profiles/3a55a6d8ce7cd546f333debd38cd2c8b406bf5293b134b08011246c756c56a02.png',NULL),
('jian.clean@example.com','seed/presentation-profiles/4446b1ab54cd891731dd0f5b2b3f52296ba34bb249d1da53b5568d48e780340e.png',NULL),
('hayoon.clean@example.com','seed/presentation-profiles/e550aedb1375b19f2a9a3adf6ac87333c3368d11938456a6ac2932837773be23.png',NULL),
('jio.web@example.com','seed/presentation-profiles/ac87b492952a1e806cfd07c8b34d28036c3eaac9882f10ed109eb4c1ee12cd4e.png',NULL),
('hyunwoo.dev@example.com','seed/presentation-profiles/eb98873dad0c9294e1e20be39ad20bfe1221e64fb07bab9d478455f71f412833.png',NULL),
('seoa.design@example.com','seed/presentation-profiles/ba4360ddbbd45220d6b76ae0bf036a745fc9e5209880534584c5b2733d112b68.png',NULL),
('yunseo.web@example.com','seed/presentation-profiles/710d56a3e6311981449bab84ec340139a77e7a6d6813c2d00282c72e2a65c032.png',NULL),
('codemate@example.com','seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg','seed/expert-profiles/31055f32bcdc8c99452edee7f3b116878cba7d3369df93d3c90c765e92b3e99f.jpg'),
('bareun.fix@example.com','seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg','seed/expert-profiles/11971b4bd9d62252bb8fb03646c3bbe99cfe86660147b984c65ac082d5acbfb8.jpg');

INSERT INTO seed195_previous_member_photos VALUES
('haneul.wed@example.com','seed/expert-portraits/23fc54320fddad75b686b37b70e15606ce25e3bed139a01d0765ba581b96465d.jpg'),
('expert@example.com','seed/accounts/160ca0178ba218aa5430172258e81061c7543a82f6e9b241215f393846800ccd.jpg'),
('expert@example.com','seed/accounts/913170626994d25953e84b0cdb16af935fbeda347e0294e7f24342e9cbe1dd7a.jpg'),
('seojun.move@example.com','seed/accounts/999816323094679630b9f1439e5882cff7aed0361628a7ed318937b2d6cc9541.jpg'),
('seojun.move@example.com','seed/accounts/d6d6005bb00feb81e764b81e387f3df92f04a427b5a28ba4d7b0b4824048621e.jpg'),
('doyoon.move@example.com','seed/accounts/941ef02196ec8967be5c2be1652afc998146c22dc7489dc7e9d7962f8a52c558.jpg'),
('doyoon.move@example.com','seed/accounts/ca09ee29d35aa8de2e1c6dd9567468500a07255a43c9e793f9dc2e06a5757366.jpg'),
('subin.clean@example.com','seed/accounts/5319fc6d71e5f917095557135d5a256a27d8467c3b0ef69105493ef542f184f1.jpg'),
('subin.clean@example.com','seed/accounts/79fe70d2bd5ad5d12c88bfd69df1427e87a627e862c2a6a15e87b67aa7e75375.jpg'),
('jian.clean@example.com','seed/accounts/07de837d4dceed8e21851ac042bf070d40d826326d200f7617e6fed02375e153.jpg'),
('jian.clean@example.com','seed/accounts/d81a7440f7065da8d497ca1c98114c943975236516ac6d7e5787c0e04f407b73.jpg'),
('hayoon.clean@example.com','seed/accounts/4e1412568928287ee5e01c30aa16453a8aa0b2cc5845d72c61963d37bdcb44a9.jpg'),
('hayoon.clean@example.com','seed/accounts/17bd3823d519798342f704326763b51e3a440ad3ba1ec022bda367adfe9809b8.jpg'),
('jio.web@example.com','seed/accounts/b0173a8b0100f6e484d7c4585364b573260256dd5c02cb8ddc8cd9c6586d058a.jpg'),
('jio.web@example.com','seed/accounts/6cc59e6ed919474fdaa72c5e19414a465429733a5809dca074674c624abd837a.jpg'),
('hyunwoo.dev@example.com','seed/accounts/baeb12b874aa93d437ed7180c985344c805497274ed66088ede8b8bdf3eb11ca.jpg'),
('hyunwoo.dev@example.com','seed/accounts/88a7e3a20e13f3f96363dc88c6db975f78a105081c3d17e643a6c0da3a8b03a1.jpg'),
('seoa.design@example.com','seed/accounts/2b796d43b228aa1b13ce8861b9371ef74324cb2c97bb473f1fa546085d069325.jpg'),
('seoa.design@example.com','seed/accounts/71c81703cc1941522a23839853b6637f5b6a89d1e714bb5e1af1faf605e81363.jpg'),
('yunseo.web@example.com','seed/accounts/a62d50b22e7026e476a23be3bc1fb6fc4f15c01b9dfe7461a1685e0b9e73c19a.jpg'),
('yunseo.web@example.com','seed/accounts/9e020f232f0dcd1c38bd8213cd7cb8bd940a6d41c9e3ea308131db55caaa260e.jpg');

INSERT INTO seed195_work VALUES
('seed-plumb@example.com','seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg','보일러 설비 작업 사례',0),
('onjip@example.com','seed/expert-work/3d3327ec26e5d74025e457aeca358912-107-0.jpg','부분 인테리어 작업 사례',0),
('cleargrout@example.com','seed/expert-work/3d3327ec26e5d74025e457aeca358912-118-0.jpg','부분 인테리어 작업 사례',0),
('expert1@example.com','seed/accounts/6663043958194795b3f3bf764c6c9c45bf96b7e7bf72ecc34466de5ad413c68a.jpg','서비스 작업 사례 사진',0),
('expert42@example.com','seed/accounts/d2398d0ca1f40c7d45fe00ccea3cc3b86e8a6ea76f1982292f94fe0d55a2c1d8.jpg','서비스 작업 사례 사진',0),
('expert51@example.com','seed/accounts/87c049648877fddc62072f492135a948b34154947fba9cdb061c046f5e0b2562.jpg','서비스 작업 사례 사진',0),
('expert52@example.com','seed/accounts/c91458d9ec331ccac201adcde487fff49023e92275c39ae7d9d23bae11f2fd19.jpg','서비스 작업 사례 사진',0),
('expert2@example.com','seed/accounts/1a98131222651fa1a1069a3429e2db3b6daf4412dc16c4195999e12bf3cbb55d.jpg','서비스 작업 사례 사진',0),
('expert11@example.com','seed/accounts/1bc318f083d1247f4c121e206ee55233a9de868965fb49890f7822e34969cef5.jpg','서비스 작업 사례 사진',0),
('expert12@example.com','seed/accounts/e80f5e65ee4dac108a994b09df8ef512c4a5fe4b84621f7310fa8c82cb67f77f.jpg','서비스 작업 사례 사진',0),
('expert21@example.com','seed/accounts/b8ec0bb7ca58c6147dc7a508c238ad9fc6511a3402173a807213ad52b40b11f5.jpg','서비스 작업 사례 사진',0),
('expert22@example.com','seed/accounts/fc86f1b5a60b0d8faa97de25dfc583048d6de10815722587cb2961fe69ed81e6.jpg','서비스 작업 사례 사진',0),
('expert31@example.com','seed/accounts/fcceae98a52d7b28ed6e7b6258a85e9144977ac247c91faaae851b4196414b90.jpg','서비스 작업 사례 사진',0),
('expert32@example.com','seed/accounts/8b86c275ce6b7ad07e155462ffa33b672553fb4dad2910535f0349c61fb3abdd.jpg','서비스 작업 사례 사진',0),
('expert41@example.com','seed/accounts/e0b9a48ba99ea0fdab51f073fe5c17b1aadda6058dc873b83846689fdbf16342.jpg','서비스 작업 사례 사진',0),
('expert1@example.com','seed/accounts/5bfdce0be94405bfdcfb40c459256aebccc84f9246bd46a179b0f2014748a17c.jpg','서비스 작업 사례 사진',1),
('expert51@example.com','seed/accounts/ea0c2a75636e77d9f034aaa6028e6cef5b658f7e8f19fe93ce5e86cc11bf2b6a.jpg','서비스 작업 사례 사진',1),
('expert11@example.com','seed/accounts/529ab8f7912c5047b2b1801f0df11720249c9c99936c0d605be8fc2421c67c8e.jpg','서비스 작업 사례 사진',1),
('expert21@example.com','seed/accounts/18b6e3471ef9f9bd83fed561c1df7f84c6fb48c420d7806f2f87f2ea96197bed.jpg','서비스 작업 사례 사진',1),
('expert31@example.com','seed/accounts/02acf0e4c33e04bf6be318444416c6b9b0c7753a0e0b0614430731eeca43ba78.jpg','서비스 작업 사례 사진',1),
('expert41@example.com','seed/accounts/3e75e6866e2a65ed8ac86e8271015ab80a107261f21618d78db349687aa11730.jpg','서비스 작업 사례 사진',1),
('damda.snap@example.com','seed/expert-work/e1b4e7d25f66716ef603bffdce076b86-108-0.jpg','스냅 촬영 작업 사례',0),
('onjip@example.com','seed/expert-work/fe32925474575a143b11465fc473b193-107-1.jpg','실내 시공 작업 사례',1),
('cleargrout@example.com','seed/expert-work/fe32925474575a143b11465fc473b193-118-1.jpg','실내 시공 작업 사례',1),
('tidyday@example.com','seed/expert-work/692893d76be445e26009371fd2909ae9-109-0.jpg','에어컨 청소 작업 사례',0),
('yoon.english@example.com','seed/expert-work/216cbc29b8a476ed0a26d81d837f46df-103-0.jpg','영어 수업 진행 사례',0),
('seed-dev@example.com','seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg','웹 개발 작업 사례',0),
('hangyeol.move@example.com','seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-101-1.jpg','이삿짐 운반 작업 사례',1),
('haeundae.move@example.com','seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-112-1.jpg','이삿짐 운반 작업 사례',1),
('damda.snap@example.com','seed/expert-work/e5d6e1eaa9c945df8921783233acafee-108-1.jpg','인물 촬영 작업 사례',1),
('sparkle.home@example.com','seed/expert-work/bfd792b1d7fa53d5c2190c964aa29798-102-0.jpg','입주 청소 작업 사례',0),
('sparkle.home@example.com','seed/expert-work/ac44f8883c34a4deae39fc8b6cb82da0-102-1.jpg','청소 마무리 작업 사례',1),
('hangyeol.move@example.com','seed/expert-work/16de8671c1456f24e95274b607c9bbbe-101-0.jpg','포장이사 작업 사례',0),
('haeundae.move@example.com','seed/expert-work/16de8671c1456f24e95274b607c9bbbe-112-0.jpg','포장이사 작업 사례',0),
('codemate@example.com','seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg','웹 개발 작업 사례',0),
('bareun.fix@example.com','seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg','보일러 설비 작업 사례',0),
('expert@example.com','seed/accounts/b012c926b84cf2ee2a937c177623ab226b4488a88bfdc3c6c56beea9758a996a.jpg','서비스 작업 사례 사진',0),
('expert@example.com','seed/accounts/b6e088cfef152f5ec185163f26e75a3d76f716d10f4056e84d994187b77aa6c7.jpg','서비스 작업 사례 사진',1),
('seojun.move@example.com','seed/accounts/9aa6442eda00a350e89f8c1dede7dfa374ccc7be0121f79e38866d076eeb5550.jpg','서비스 작업 사례 사진',0),
('seojun.move@example.com','seed/accounts/de9119d5860ef6201aa97df9dd2712c92f03bfd8bf9c2da8cfa55a80fbcd26ff.jpg','서비스 작업 사례 사진',1),
('doyoon.move@example.com','seed/accounts/635432912a9746c58549034e8731f640cad6edf99d75487d1904da587f8bb18b.jpg','서비스 작업 사례 사진',0),
('doyoon.move@example.com','seed/accounts/210cc985df03b8cab78690e5f80c51d93cea536a526e533d31024db98b9faf95.jpg','서비스 작업 사례 사진',1),
('subin.clean@example.com','seed/accounts/549fa5cdae210603006333b0e15da64284deea0779ef56315d62bda8439eebd0.jpg','서비스 작업 사례 사진',0),
('subin.clean@example.com','seed/accounts/54cee6db588cb25f542ddef6f875e5ccaae9af6c539b632d3a8919eb81ba1795.jpg','서비스 작업 사례 사진',1),
('jian.clean@example.com','seed/accounts/b3feea8bc591fc46962067a6d9af6da5f65a90d6a4aa2da601c968ed25e7d3be.jpg','서비스 작업 사례 사진',0),
('jian.clean@example.com','seed/accounts/016609033f8011e1203ebd6343ad395e636b110bd68aba4b5da17326d31e7532.jpg','서비스 작업 사례 사진',1),
('hayoon.clean@example.com','seed/accounts/3961ddbd344a2fce7e47d282672f39282c303ade835c7bc3bfb7c068c0550fc0.jpg','서비스 작업 사례 사진',0),
('hayoon.clean@example.com','seed/accounts/2c3ae4a8b6be8e0507f61a7fb968c11d56c9401d924050cc73c1f8bc6772964e.jpg','서비스 작업 사례 사진',1),
('jio.web@example.com','seed/accounts/fdd784d615f30ce8fd509a65507304a5faafbb180b686f2d188c4ec0343b406f.jpg','서비스 작업 사례 사진',0),
('hyunwoo.dev@example.com','seed/accounts/2fc29d0f0ea4964e0500616fd9683dedbf5dfbb1f2188a70d973cddae6bf92b8.jpg','서비스 작업 사례 사진',0),
('seoa.design@example.com','seed/accounts/29dbe8f14c71b14762bab6d01ec486000ac2ea1c4efdb13c2c935afc45100b6f.jpg','서비스 작업 사례 사진',0),
('yunseo.web@example.com','seed/accounts/7cd1d484abf3836fe0041ff7085555aa73b2bcfbab3a7039672952eb567e4b23.jpg','서비스 작업 사례 사진',0);

INSERT INTO seed195_posts VALUES
(1,'seed-clean@example.com','PROMOTION','입주청소 전후 사진 보여드려요','34평 아파트 입주청소 사례예요. 2인 1조로 6시간 작업했습니다.
창틀, 베란다 배수구, 주방 후드 안쪽까지 분해해서 닦아요.
작업 후 사진으로 구석구석 보여 드리고, 하루 안에 다시 봐 드립니다.','입주청소 전후 사진 보여드려요','34평 아파트 입주청소 사례예요. 2인 1조로 6시간 작업했습니다.
창틀, 베란다 배수구, 주방 후드 안쪽까지 분해해서 닦아요.
작업 후 사진으로 구석구석 보여 드리고, 하루 안에 다시 봐 드립니다.'),
(2,'seed-plumb@example.com','PROMOTION','보일러 고장, 방문 진단부터 받아보세요','마포·서대문 지역 보일러, 수도 수리합니다.
에러코드만 알려 주셔도 대략적인 원인과 비용을 미리 말씀드려요.
부품 교체가 필요 없으면 출장비만 받습니다.','보일러 고장, 방문 진단부터 받아보세요','마포·서대문 지역 보일러, 수도 수리합니다.
에러코드만 알려 주셔도 대략적인 원인과 비용을 미리 말씀드려요.
부품 교체가 필요 없으면 출장비만 받습니다.'),
(3,'expert2@example.com','PROMOTION','에어컨 분해청소, 겨울에 미리 하세요','벽걸이, 스탠드, 시스템 에어컨 모두 분해해서 고압 세척해요.
겨울에는 예약이 여유로워서 원하시는 날짜에 맞춰 드릴 수 있어요.
곰팡이 냄새가 났다면 꼭 한번 맡겨 보세요.','에어컨 분해청소, 겨울에 미리 하세요','벽걸이, 스탠드, 시스템 에어컨 모두 분해해서 고압 세척해요.
겨울에는 예약이 여유로워서 원하시는 날짜에 맞춰 드릴 수 있어요.
곰팡이 냄새가 났다면 꼭 한번 맡겨 보세요.'),
(4,'expert1@example.com','PROMOTION','원룸·투룸 이사 전문, 3년간 파손 0건','서초·강남 위주로 원룸, 투룸 이사를 하고 있어요.
가구는 모서리 보호대를 씌우고, 그릇은 한 장씩 따로 싸서 옮깁니다.
평일 오전 이사는 조금 더 저렴하게 진행해요. 요청서 남겨 주시면 사진 보고 정확히 견적 드릴게요.','원룸·투룸 이사 전문, 3년간 파손 0건','서초·강남 위주로 원룸, 투룸 이사를 하고 있어요.
가구는 모서리 보호대를 씌우고, 그릇은 한 장씩 따로 싸서 옮깁니다.
평일 오전 이사는 조금 더 저렴하게 진행해요. 요청서 남겨 주시면 사진 보고 정확히 견적 드릴게요.'),
(5,'seed-english@example.com','PROMOTION','중고등 영어 내신 1:1 관리','송파 지역 중고등학생 영어 내신을 1:1로 봐요.
학교 기출을 분석해서 시험 4주 전부터 맞춤 자료로 준비합니다.
첫 수업은 레벨 확인과 상담으로 진행해요.','중고등 영어 내신 1:1 관리','송파 지역 중고등학생 영어 내신을 1:1로 봐요.
학교 기출을 분석해서 시험 4주 전부터 맞춤 자료로 준비합니다.
첫 수업은 레벨 확인과 상담으로 진행해요.'),
(6,'seed-photo@example.com','PROMOTION','웨딩·가족 스냅 포트폴리오','야외 웨딩 스냅과 가족사진을 주로 찍어요.
촬영 전 장소와 시간대를 같이 정하고, 보정본은 2주 안에 보내 드립니다.
원본 전체도 함께 드려요.','웨딩·가족 스냅 포트폴리오','야외 웨딩 스냅과 가족사진을 주로 찍어요.
촬영 전 장소와 시간대를 같이 정하고, 보정본은 2주 안에 보내 드립니다.
원본 전체도 함께 드려요.'),
(7,'seed-interior@example.com','PROMOTION','15평 구축 부분 리모델링 사례','주방 상판과 욕실 타일, 거실 조명만 바꾼 부분 리모델링이에요.
전체 공사보다 기간은 5일, 비용은 절반 정도였어요.
살면서 공사가 가능한지, 어디까지 바꿀지 현장에서 같이 정해 드립니다.','15평 구축 부분 리모델링 사례','주방 상판과 욕실 타일, 거실 조명만 바꾼 부분 리모델링이에요.
전체 공사보다 기간은 5일, 비용은 절반 정도였어요.
살면서 공사가 가능한지, 어디까지 바꿀지 현장에서 같이 정해 드립니다.'),
(8,'seed-dev@example.com','PROMOTION','MVP 웹·앱 외주 개발 사례','초기 창업팀의 MVP를 6주 안에 만들어 드려요.
기획서가 없어도 괜찮아요. 첫 미팅에서 꼭 필요한 기능만 같이 추립니다.
최근에는 예약 서비스와 사내 관리 도구를 만들었어요.','MVP 웹·앱 외주 개발 사례','초기 창업팀의 MVP를 6주 안에 만들어 드려요.
기획서가 없어도 괜찮아요. 첫 미팅에서 꼭 필요한 기능만 같이 추립니다.
최근에는 예약 서비스와 사내 관리 도구를 만들었어요.'),
(9,'expert22@example.com','PROMOTION','팀원2 개발 상담 안내','개발 외주 작업 범위와 일정을 함께 상담해 드립니다.','예약 흐름부터 정리하는 웹 개발','공방과 소규모 수업의 예약 과정을 설계합니다. 정원과 일정 변경 규칙을 확인하고 운영자가 예약을 관리하기 쉬운 화면을 제안합니다.'),
(10,'expert52@example.com','PROMOTION','팀원5 개발 상담 안내','개발 외주 작업 범위와 일정을 함께 상담해 드립니다.','배포 후 운영까지 생각하는 개발','웹 개발부터 배포 후 기본 점검까지 진행합니다. 관리자 계정과 환경 설정을 구분하고 직접 바꿀 수 있는 내용은 간단한 운영 안내로 남깁니다.'),
(11,'expert12@example.com','PROMOTION','팀원1 개발 상담 안내','개발 외주 작업 범위와 일정을 함께 상담해 드립니다.','가게 소개 웹사이트를 함께 만듭니다','작은 매장의 소개 페이지와 모바일 메뉴 화면을 제작합니다. 처음 상담에서 꼭 필요한 내용만 정리하고 운영자가 수정할 부분을 나누어 안내합니다.'),
(12,'expert42@example.com','PROMOTION','팀원4 개발 상담 안내','개발 외주 작업 범위와 일정을 함께 상담해 드립니다.','휴대폰에서 읽기 편한 화면 제작','작은 화면의 버튼 간격과 글자 크기를 살펴 반응형 화면을 구현합니다. 소개 페이지와 문의 화면을 중심으로 실제 사용 순서에 맞춰 정리합니다.'),
(13,'expert32@example.com','PROMOTION','팀원3 개발 상담 안내','개발 외주 작업 범위와 일정을 함께 상담해 드립니다.','관리 화면 API 연결을 도와드립니다','회원 조회와 주문 상태처럼 반복되는 관리 업무를 웹으로 연결합니다. 기존 화면이 있다면 요청과 응답 형식을 먼저 확인하고 필요한 API부터 개발합니다.'),
(14,'seed-user2@example.com','QUESTION','강아지 산책 도우미 써보신 분?','출근 시간이 길어져서 낮에 산책을 부탁하고 싶어요. 비용이 어느 정도인가요?','강아지 산책 도우미 써보신 분?','출근 시간이 길어져서 낮에 산책을 부탁하고 싶어요. 비용이 어느 정도인가요?'),
(15,'seed-user2@example.com','QUESTION','원룸 도배, 셀프로 해도 될까요?','6평 원룸이에요. 셀프 도배 해보신 분 계신가요? 업체 비용이랑 차이가 궁금해요.','원룸 도배, 셀프로 해도 될까요?','6평 원룸이에요. 셀프 도배 해보신 분 계신가요? 업체 비용이랑 차이가 궁금해요.'),
(16,'seed-user1@example.com','QUESTION','웨딩 스냅 원본도 다 받을 수 있나요?','보정본만 주는 곳이 있다고 들었어요. 보통 어떻게 하나요?','웨딩 스냅 원본도 다 받을 수 있나요?','보정본만 주는 곳이 있다고 들었어요. 보통 어떻게 하나요?'),
(17,'customer1@example.com','QUESTION','앱 외주 견적은 뭘 기준으로 받아요?','예약 기능이 있는 간단한 앱을 만들고 싶어요. 기능 목록만 있으면 될까요?','앱 외주 견적은 뭘 기준으로 받아요?','예약 기능이 있는 간단한 앱을 만들고 싶어요. 기능 목록만 있으면 될까요?'),
(18,'customer2@example.com','QUESTION','초등 영어 과외 몇 학년부터 하나요?','초3 아이인데 학원이 맞을지 1:1 과외가 맞을지 고민이에요.','초등 영어 과외 몇 학년부터 하나요?','초3 아이인데 학원이 맞을지 1:1 과외가 맞을지 고민이에요.'),
(19,'customer2@example.com','QUESTION','입주청소 몇 평부터 2명이 와요?','24평 아파트인데 1명이 와도 괜찮을까요?','입주청소 몇 평부터 2명이 와요?','24평 아파트인데 1명이 와도 괜찮을까요?'),
(20,'customer1@example.com','QUESTION','보일러에 E-03 에러가 계속 떠요','껐다 켜면 잠깐 되다가 다시 에러가 나요. 직접 해볼 수 있는 게 있을까요?','보일러에 E-03 에러가 계속 떠요','껐다 켜면 잠깐 되다가 다시 에러가 나요. 직접 해볼 수 있는 게 있을까요?'),
(21,'customer31@example.com','QUESTION','팀원3 이사 준비 질문','이사 전에 미리 준비할 사항을 알려 주세요.','엘리베이터 없는 3층 이사 준비','대구의 오래된 빌라 3층으로 들어갑니다. 계단 폭이 좁아서 소파가 들어갈지 미리 확인하고 싶어요.'),
(22,'customer51@example.com','QUESTION','팀원5 이사 준비 질문','이사 전에 미리 준비할 사항을 알려 주세요.','이삿날 주차 공간은 어떻게 확보하나요','대전 골목 안쪽 주택으로 이사할 예정입니다. 차량 진입과 잠시 짐을 내릴 공간을 언제 확인해야 할까요?'),
(23,'customer21@example.com','QUESTION','팀원2 이사 준비 질문','이사 전에 미리 준비할 사항을 알려 주세요.','냉장고는 이사 전날 비워야 하나요','부산 중구에서 같은 동네로 이사합니다. 냉동식품과 김치가 남아 있는데 전원은 언제 끄는 게 좋을까요?'),
(24,'customer11@example.com','QUESTION','팀원1 이사 준비 질문','이사 전에 미리 준비할 사항을 알려 주세요.','원룸 이사 박스는 몇 개가 적당할까요','옷과 책이 많은 7평 원룸입니다. 큰 가구는 침대와 책상뿐인데 박스를 어느 정도 준비하면 좋을까요?'),
(25,'customer41@example.com','QUESTION','팀원4 이사 준비 질문','이사 전에 미리 준비할 사항을 알려 주세요.','비 오는 날 이사하면 무엇을 챙기나요','인천으로 이사하는 날 비 예보가 있습니다. 책과 전자기기를 젖지 않게 포장하는 요령이 궁금해요.'),
(26,'seed-user1@example.com','QUESTION','이사 견적이 업체마다 왜 이렇게 달라요?','투룸 이사인데 견적이 45만 원부터 80만 원까지 나왔어요.
어떤 부분을 비교해야 할지 모르겠어요.','이사 견적이 업체마다 왜 이렇게 달라요?','투룸 이사인데 견적이 45만 원부터 80만 원까지 나왔어요.
어떤 부분을 비교해야 할지 모르겠어요.'),
(27,'seed-photo@example.com','RECRUIT','연말 행사 촬영 보조 2명 구해요','12월 둘째 주 토요일, 기업 행사 촬영 보조를 구해요. 장비는 제가 준비해요.','연말 행사 촬영 보조 2명 구해요','12월 둘째 주 토요일, 기업 행사 촬영 보조를 구해요. 장비는 제가 준비해요.'),
(28,'seed-user1@example.com','RECRUIT','평일 저녁 토익 스터디원 구해요','송파역 근처 카페에서 화, 목 저녁 8시에 모여요. 목표 점수는 850점이에요.','평일 저녁 토익 스터디원 구해요','송파역 근처 카페에서 화, 목 저녁 8시에 모여요. 목표 점수는 850점이에요.'),
(29,'seed-clean@example.com','RECRUIT','입주청소 팀원 모집합니다 (경력 무관)','주 3~4회 함께 일할 팀원을 찾아요. 처음이어도 2주 동안 같이 다니며 알려 드려요.','입주청소 팀원 모집합니다 (경력 무관)','주 3~4회 함께 일할 팀원을 찾아요. 처음이어도 2주 동안 같이 다니며 알려 드려요.'),
(30,'customer12@example.com','RECRUIT','팀원1 개발 작업 고수 구해요','작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.','작은 카페 주문 소개 페이지 제작','메뉴와 영업시간을 휴대폰에서 편하게 보여 주는 페이지가 필요합니다. 주문 결제 없이 매장 소개와 지도 연결부터 만들고 싶어요.'),
(31,'customer52@example.com','RECRUIT','팀원5 개발 작업 고수 구해요','작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.','소규모 쇼핑몰 재고 도구 제작','상품명과 옵션별 수량을 기록하고 엑셀로 내려받는 관리 화면이 필요합니다. 결제나 배송 기능은 이번 범위에서 제외하고 싶습니다.'),
(32,'customer22@example.com','RECRUIT','팀원2 개발 작업 고수 구해요','작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.','공방 수업 예약 화면을 만들고 싶어요','요일별 도예 수업의 남은 자리를 보여 주고 신청을 받는 화면을 찾고 있습니다. 우선 예약 확인 문자 없이 운영 가능한 구조면 좋겠습니다.'),
(33,'customer42@example.com','RECRUIT','팀원4 개발 작업 고수 구해요','작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.','사진가 포트폴리오 사이트 의뢰','웨딩과 가족 사진을 구분해 보여 주는 사이트를 만들고 싶습니다. 사진을 직접 교체할 수 있고 문의 폼은 없어도 괜찮습니다.'),
(34,'customer32@example.com','RECRUIT','팀원3 개발 작업 고수 구해요','작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.','동네 체육관 출석 관리 웹 모집','종이 출석부 대신 회원 이름과 방문 날짜를 기록하는 내부 웹이 필요합니다. 직원 두 명이 함께 쓰며 개인정보는 최소한만 저장하려고 합니다.'),
(35,'seed-dev@example.com','TIP','외주 맡기기 전 정리할 문서 3가지','1. 누가, 어떤 순서로 쓰는지 적은 사용 흐름
2. 꼭 있어야 하는 기능과 나중에 넣을 기능 목록
3. 참고하는 서비스 2~3개와 마음에 드는 이유
이 세 가지만 있어도 견적이 훨씬 정확해져요.','외주 맡기기 전 정리할 문서 3가지','1. 누가, 어떤 순서로 쓰는지 적은 사용 흐름
2. 꼭 있어야 하는 기능과 나중에 넣을 기능 목록
3. 참고하는 서비스 2~3개와 마음에 드는 이유
이 세 가지만 있어도 견적이 훨씬 정확해져요.'),
(36,'seed-clean@example.com','TIP','입주청소 전에 꼭 확인할 5가지','1. 청소 범위에 베란다, 창틀, 붙박이장 안쪽이 들어가는지 확인하세요.
2. 새집증후군 시공은 청소와 별도인지 물어보세요.
3. 작업 인원과 예상 시간을 미리 받아 두세요.
4. 끝나고 함께 둘러보는 시간을 꼭 잡으세요.
5. 다시 봐 주는 기간(A/S)이 며칠인지 확인하세요.','입주청소 전에 꼭 확인할 5가지','1. 청소 범위에 베란다, 창틀, 붙박이장 안쪽이 들어가는지 확인하세요.
2. 새집증후군 시공은 청소와 별도인지 물어보세요.
3. 작업 인원과 예상 시간을 미리 받아 두세요.
4. 끝나고 함께 둘러보는 시간을 꼭 잡으세요.
5. 다시 봐 주는 기간(A/S)이 며칠인지 확인하세요.'),
(37,'seed-interior@example.com','TIP','장판 vs 강마루, 이렇게 고르세요','반려동물이 있거나 물을 자주 쓰면 장판이 관리하기 편해요.
찍힘에 강하고 고급스러운 느낌을 원하면 강마루가 좋아요.
비용은 강마루가 평당 2~3배 정도 더 들어요.','장판 vs 강마루, 이렇게 고르세요','반려동물이 있거나 물을 자주 쓰면 장판이 관리하기 편해요.
찍힘에 강하고 고급스러운 느낌을 원하면 강마루가 좋아요.
비용은 강마루가 평당 2~3배 정도 더 들어요.'),
(38,'seed-english@example.com','TIP','영어 단어, 하루 30개 외우는 법','새 단어 30개보다 어제 외운 단어 다시 보기가 먼저예요.
1일, 3일, 7일 뒤에 다시 보는 순서로 복습해 보세요.
예문을 소리 내어 읽으면 오래 기억에 남아요.','영어 단어, 하루 30개 외우는 법','새 단어 30개보다 어제 외운 단어 다시 보기가 먼저예요.
1일, 3일, 7일 뒤에 다시 보는 순서로 복습해 보세요.
예문을 소리 내어 읽으면 오래 기억에 남아요.'),
(39,'seed-plumb@example.com','TIP','보일러 동파 막는 겨울 습관','영하로 내려가는 날에는 외출 모드로 두고 나가세요. 보일러를 끄면 배관이 얼 수 있어요.
베란다에 보일러가 있다면 배관을 보온재로 감싸 두세요.
수도는 아주 가늘게 틀어 두면 동파를 막을 수 있어요.','보일러 동파 막는 겨울 습관','영하로 내려가는 날에는 외출 모드로 두고 나가세요. 보일러를 끄면 배관이 얼 수 있어요.
베란다에 보일러가 있다면 배관을 보온재로 감싸 두세요.
수도는 아주 가늘게 틀어 두면 동파를 막을 수 있어요.'),
(40,'expert1@example.com','TIP','이삿짐 쌀 때 깨지는 물건 포장법','접시는 눕히지 말고 세워서 넣으세요. 세워야 충격에 덜 깨져요.
컵 안에는 신문지를 채우고, 상자 빈 곳은 수건으로 메우세요.
상자 겉에 "깨짐 주의"와 들어갈 방 이름을 적어 두면 정리가 빨라요.','이삿짐 쌀 때 깨지는 물건 포장법','접시는 눕히지 말고 세워서 넣으세요. 세워야 충격에 덜 깨져요.
컵 안에는 신문지를 채우고, 상자 빈 곳은 수건으로 메우세요.
상자 겉에 "깨짐 주의"와 들어갈 방 이름을 적어 두면 정리가 빨라요.'),
(41,'expert2@example.com','TIP','에어컨 청소 주기, 이렇게 보세요','필터는 2주에 한 번 직접 물로 씻어 주세요.
분해청소는 1~2년에 한 번이면 충분해요.
켤 때 쉰내가 나거나 바람이 약해졌다면 그때가 분해청소할 때예요.','에어컨 청소 주기, 이렇게 보세요','필터는 2주에 한 번 직접 물로 씻어 주세요.
분해청소는 1~2년에 한 번이면 충분해요.
켤 때 쉰내가 나거나 바람이 약해졌다면 그때가 분해청소할 때예요.'),
(42,'seed-photo@example.com','TIP','셀프 웨딩 촬영 장소 고르는 법','해가 지기 1시간 전, 골든아워에 찍으면 보정 없이도 색이 예뻐요.
사람이 적은 평일 오전도 좋아요.
공원은 촬영 허가가 필요한 곳이 있으니 미리 확인하세요.','셀프 웨딩 촬영 장소 고르는 법','해가 지기 1시간 전, 골든아워에 찍으면 보정 없이도 색이 예뻐요.
사람이 적은 평일 오전도 좋아요.
공원은 촬영 허가가 필요한 곳이 있으니 미리 확인하세요.'),
(43,'expert51@example.com','TIP','팀원5 입주 청소 준비 팁','청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.','마루 청소 후 물기를 남기지 마세요','마루는 물을 흥건하게 쓰기보다 꼭 짠 걸레로 나누어 닦는 편이 좋습니다. 가구 아래까지 말랐는지 확인하고 작은 흠집은 소재에 맞춰 관리하세요.'),
(44,'expert41@example.com','TIP','팀원4 입주 청소 준비 팁','청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.','창틀 먼지를 덜 날리며 닦는 방법','먼지를 마른 솔로 먼저 털면 실내로 날리기 쉽습니다. 살짝 적신 천으로 큰 먼지를 걷어 낸 뒤 틈새를 닦고 마지막에 물기를 제거하세요.'),
(45,'expert21@example.com','TIP','팀원2 입주 청소 준비 팁','청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.','욕실 물때를 줄이는 환기 습관','샤워 뒤에는 바닥의 큰 물기를 먼저 밀어내고 환풍기를 잠시 더 켜 두세요. 세면대 수전은 마른 천으로 닦아 주면 물자국이 덜 남습니다.'),
(46,'expert11@example.com','TIP','팀원1 입주 청소 준비 팁','청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.','입주 청소 전 수납장 비우는 순서','작은 짐부터 상자에 담고 수납장 문을 모두 열어 두세요. 분리 가능한 선반은 따로 모아 두면 안쪽 먼지를 확인하기 쉽습니다.'),
(47,'expert31@example.com','TIP','팀원3 입주 청소 준비 팁','청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.','주방 후드 필터 세척 전 확인할 것','필터를 분리하기 전에 전원을 끄고 설명서의 세척 가능 표시를 확인하세요. 소재에 따라 강한 세제가 변색을 만들 수 있어 먼저 작은 부분에 시험합니다.'),
(48,'haneul123@example.com','QUESTION','첫 이사 전날, 뭘 먼저 챙기면 좋을까요?','처음 자취방을 옮기는데 어디서부터 짐을 싸야 할지 막막하네요. 옷이랑 책은 상자에 담고 있는데 이사 당일 쓸 물건도 같이 넣어 버릴까 봐 걱정돼요. 먼저 챙겨 두면 좋은 물건이 있을까요?','첫 이사 전날, 뭘 먼저 챙기면 좋을까요?','처음 자취방을 옮기는데 어디서부터 짐을 싸야 할지 막막하네요. 옷이랑 책은 상자에 담고 있는데 이사 당일 쓸 물건도 같이 넣어 버릴까 봐 걱정돼요. 먼저 챙겨 두면 좋은 물건이 있을까요?'),
(49,'expert@example.com','TIP','원룸 이사 전에 챙길 다섯 가지','이사 현장에서 자주 빠뜨리는 준비를 적어 봅니다. ① 관리실에 차량 출입 시간을 알려 주세요. ② 냉장고 안은 미리 비워 주세요. ③ 상자에는 놓을 방을 적어 주세요. ④ 귀중품과 약은 따로 챙겨 주세요. ⑤ 도착한 집에서 쓸 충전기와 세면도구는 마지막까지 손에 두면 편합니다.','원룸 이사 전에 챙길 다섯 가지','이사 현장에서 자주 빠뜨리는 준비를 적어 봅니다. ① 관리실에 차량 출입 시간을 알려 주세요. ② 냉장고 안은 미리 비워 주세요. ③ 상자에는 놓을 방을 적어 주세요. ④ 귀중품과 약은 따로 챙겨 주세요. ⑤ 도착한 집에서 쓸 충전기와 세면도구는 마지막까지 손에 두면 편합니다.'),
(50,'seojun.move@example.com','PROMOTION','원룸·소형 이사 상담받고 있어요','서초·강남·마포 지역의 소형 이사를 진행하고 있습니다. 짐 사진과 출발지·도착지의 층수를 알려 주시면 필요한 차량과 인원을 안내해 드려요. 침대 조립이나 계단 운반이 있다면 처음 상담할 때 같이 말씀해 주세요.','원룸·소형 이사 상담받고 있어요','서초·강남·마포 지역의 소형 이사를 진행하고 있습니다. 짐 사진과 출발지·도착지의 층수를 알려 주시면 필요한 차량과 인원을 안내해 드려요. 침대 조립이나 계단 운반이 있다면 처음 상담할 때 같이 말씀해 주세요.'),
(51,'jiuzzzang@example.com','QUESTION','입주 청소와 가구 배송, 순서가 궁금해요','청소 날짜를 잡으려는데 침대 배송이 먼저 가능하다고 하네요. 가구가 없는 상태에서 청소하는 게 나을 것 같긴 한데 날짜를 바꾸기가 쉽지 않아요. 큰 가구가 한두 개 있어도 청소가 가능할까요?','입주 청소와 가구 배송, 순서가 궁금해요','청소 날짜를 잡으려는데 침대 배송이 먼저 가능하다고 하네요. 가구가 없는 상태에서 청소하는 게 나을 것 같긴 한데 날짜를 바꾸기가 쉽지 않아요. 큰 가구가 한두 개 있어도 청소가 가능할까요?'),
(52,'subin.clean@example.com','TIP','입주 청소 때 놓치기 쉬운 공간','바닥만 깨끗해 보여도 창틀 홈과 수납장 안쪽에는 먼지가 남아 있을 수 있어요. 주방 후드 필터와 욕실 배수구도 같이 확인해 보세요. 상담할 때 사진을 보내 주시면 집 상태에 맞는 작업 범위를 설명드리기 좋습니다.','입주 청소 때 놓치기 쉬운 공간','바닥만 깨끗해 보여도 창틀 홈과 수납장 안쪽에는 먼지가 남아 있을 수 있어요. 주방 후드 필터와 욕실 배수구도 같이 확인해 보세요. 상담할 때 사진을 보내 주시면 집 상태에 맞는 작업 범위를 설명드리기 좋습니다.'),
(53,'jian.clean@example.com','PROMOTION','주방·욕실 청소, 범위부터 안내드립니다','후드에 기름때가 쌓였거나 욕실 물때 때문에 고민이라면 사진을 보내 주세요. 소재와 오염 정도를 보고 가능한 작업부터 말씀드립니다. 청소를 마친 뒤에는 함께 둘러보고 빠진 곳이 없는지 확인합니다.','주방·욕실 청소, 범위부터 안내드립니다','후드에 기름때가 쌓였거나 욕실 물때 때문에 고민이라면 사진을 보내 주세요. 소재와 오염 정도를 보고 가능한 작업부터 말씀드립니다. 청소를 마친 뒤에는 함께 둘러보고 빠진 곳이 없는지 확인합니다.'),
(54,'minseo.park@example.com','RECRUIT','공방 예약 사이트를 함께 만들 고수 찾습니다','작은 도자기 공방을 운영하고 있어요. 지금은 메시지로 예약을 받는데 날짜가 겹치는 일이 생겨서 예약 사이트를 알아보고 있습니다. 휴대폰에서 날짜와 수업을 고를 수 있으면 좋겠어요. 제작 기간과 관리 방법도 상담받고 싶습니다.','공방 예약 사이트를 함께 만들 고수 찾습니다','작은 도자기 공방을 운영하고 있어요. 지금은 메시지로 예약을 받는데 날짜가 겹치는 일이 생겨서 예약 사이트를 알아보고 있습니다. 휴대폰에서 날짜와 수업을 고를 수 있으면 좋겠어요. 제작 기간과 관리 방법도 상담받고 싶습니다.'),
(55,'jio.web@example.com','TIP','개발 견적을 받기 전에 정리해 보세요','어떤 기능이 필요한지 말로 설명하기 어려울 때는 실제 사용하는 순서를 적어 보세요. 예를 들어 날짜 선택 → 인원 선택 → 연락처 입력처럼요. 참고 사이트에서 마음에 드는 부분과 필요 없는 부분을 나눠 주시면 견적 범위를 맞추기 훨씬 수월합니다.','개발 견적을 받기 전에 정리해 보세요','어떤 기능이 필요한지 말로 설명하기 어려울 때는 실제 사용하는 순서를 적어 보세요. 예를 들어 날짜 선택 → 인원 선택 → 연락처 입력처럼요. 참고 사이트에서 마음에 드는 부분과 필요 없는 부분을 나눠 주시면 견적 범위를 맞추기 훨씬 수월합니다.'),
(56,'hyunwoo.dev@example.com','PROMOTION','기존 화면에 맞는 API 개발을 도와드립니다','화면은 준비됐는데 회원가입이나 예약 기능을 연결하지 못하고 계신가요? 현재 화면과 필요한 기능을 확인한 뒤 API와 데이터베이스를 구성해 드립니다. 작업 범위와 배포 후 확인할 항목은 상담에서 함께 정합니다.','기존 화면에 맞는 API 개발을 도와드립니다','화면은 준비됐는데 회원가입이나 예약 기능을 연결하지 못하고 계신가요? 현재 화면과 필요한 기능을 확인한 뒤 API와 데이터베이스를 구성해 드립니다. 작업 범위와 배포 후 확인할 항목은 상담에서 함께 정합니다.'),
(57,'lovesaltbread@example.com','QUESTION','이삿날 비 예보가 있는데 괜찮을까요?','다음 주 이삿날에 비 예보가 떠서 걱정이에요. 책장이랑 컴퓨터가 젖을까 봐 비닐을 더 사 둬야 하나 고민 중입니다. 비 오는 날 이사해 보신 분들은 어떻게 준비하셨나요?','이삿날 비 예보가 있는데 괜찮을까요?','다음 주 이삿날에 비 예보가 떠서 걱정이에요. 책장이랑 컴퓨터가 젖을까 봐 비닐을 더 사 둬야 하나 고민 중입니다. 비 오는 날 이사해 보신 분들은 어떻게 준비하셨나요?'),
(58,'doyoon.move@example.com','TIP','큰 가구는 현관 폭도 확인해 주세요','책장이나 소파를 옮길 때는 가구 크기뿐 아니라 현관과 복도의 폭도 중요합니다. 엘리베이터가 있어도 문을 통과하지 못하는 경우가 있어요. 가구 전체 사진과 이동할 곳의 문 폭을 미리 알려 주시면 분해나 다른 운반 방법이 필요한지 확인해 드릴 수 있습니다.','큰 가구는 현관 폭도 확인해 주세요','책장이나 소파를 옮길 때는 가구 크기뿐 아니라 현관과 복도의 폭도 중요합니다. 엘리베이터가 있어도 문을 통과하지 못하는 경우가 있어요. 가구 전체 사진과 이동할 곳의 문 폭을 미리 알려 주시면 분해나 다른 운반 방법이 필요한지 확인해 드릴 수 있습니다.'),
(59,'haneul123@example.com','RECRUIT','강남 입주 청소 가능한 분 계실까요?','주말에 이사를 앞두고 있어서 금요일 오후에 청소를 맡기고 싶습니다. 주방과 거실 위주로 생각하고 있고 집은 비어 있어요. 창틀 청소도 포함되는지와 예상 작업 시간을 알고 싶습니다.','강남 입주 청소 가능한 분 계실까요?','주말에 이사를 앞두고 있어서 금요일 오후에 청소를 맡기고 싶습니다. 주방과 거실 위주로 생각하고 있고 집은 비어 있어요. 창틀 청소도 포함되는지와 예상 작업 시간을 알고 싶습니다.'),
(60,'seoa.design@example.com','PROMOTION','휴대폰에서도 편한 가게 소개 페이지','가게 위치와 메뉴를 찾으려고 들어왔는데 화면이 작아서 불편했던 경험이 있나요? 휴대폰에서 읽기 편한 소개 페이지를 만듭니다. 전화·문의 버튼과 지도처럼 자주 쓰는 기능부터 함께 정해요.','휴대폰에서도 편한 가게 소개 페이지','가게 위치와 메뉴를 찾으려고 들어왔는데 화면이 작아서 불편했던 경험이 있나요? 휴대폰에서 읽기 편한 소개 페이지를 만듭니다. 전화·문의 버튼과 지도처럼 자주 쓰는 기능부터 함께 정해요.'),
(61,'jiuzzzang@example.com','QUESTION','개발 견적에서 유지보수는 따로 보나요?','사이트 제작 견적을 몇 군데 받았는데 유지보수 범위가 조금씩 다르네요. 문구 수정이나 오류 수정은 보통 어디까지 포함되는지 궁금합니다. 배포 비용도 처음 견적에서 같이 확인하는 게 좋을까요?','개발 견적에서 유지보수는 따로 보나요?','사이트 제작 견적을 몇 군데 받았는데 유지보수 범위가 조금씩 다르네요. 문구 수정이나 오류 수정은 보통 어디까지 포함되는지 궁금합니다. 배포 비용도 처음 견적에서 같이 확인하는 게 좋을까요?'),
(62,'lovesaltbread@example.com','RECRUIT','작품을 소개할 공방 홈페이지가 필요해요','만든 작품을 사진과 설명으로 모아 둘 홈페이지를 준비하고 있어요. 주문 기능까지는 필요 없고 문의할 수 있는 창구가 있으면 좋겠습니다. 사진과 글은 제가 나중에 바꿀 수 있는 방식이면 더 좋겠어요.','작품을 소개할 공방 홈페이지가 필요해요','만든 작품을 사진과 설명으로 모아 둘 홈페이지를 준비하고 있어요. 주문 기능까지는 필요 없고 문의할 수 있는 창구가 있으면 좋겠습니다. 사진과 글은 제가 나중에 바꿀 수 있는 방식이면 더 좋겠어요.');

INSERT INTO seed195_comments VALUES
(9,'expert21@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','여러 강사가 함께 쓰는 일정도 상담 가능한가요?'),
(9,'customer22@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','수업별 정원표와 현재 예약 과정을 정리해 두겠습니다.'),
(10,'expert51@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','서버 비용과 도메인 준비 항목도 함께 안내받고 싶습니다.'),
(10,'customer52@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','운영 중 직접 수정할 문구를 먼저 정리해 보겠습니다.'),
(11,'expert11@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','매장 사진이 적어도 시작할 수 있는지 궁금합니다.'),
(11,'customer12@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','소개 문구와 연락처를 먼저 준비한 뒤 상담해 보겠습니다.'),
(12,'expert41@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','기존 데스크톱 화면을 유지하면서 모바일만 보완할 수 있나요?'),
(12,'customer42@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','자주 쓰는 휴대폰 크기를 확인해 상담 때 알려 드릴게요.'),
(13,'expert31@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','이미 운영 중인 화면에도 필요한 부분만 연결할 수 있나요?'),
(13,'customer32@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','수기로 처리하는 업무 목록부터 적어 보고 문의하겠습니다.'),
(21,'expert31@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','소파 가로와 대각선 길이도 함께 재어 두시면 도움이 됩니다.'),
(21,'customer32@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','계단 모서리와 현관 사진을 찍어서 상담해 보겠습니다.'),
(22,'expert51@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','차량 높이 제한과 전선 위치도 미리 확인해 주세요.'),
(22,'customer52@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','이웃에게 통행 시간을 안내하고 업체와 동선을 정해 보겠습니다.'),
(23,'expert21@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','제품 모델과 운반 시간을 알려 주시면 준비 순서를 안내할 수 있어요.'),
(23,'customer22@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','냉동식품은 보냉 가방에 따로 챙겨 두겠습니다.'),
(24,'expert11@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','책은 작은 박스에 나누면 옮길 때 훨씬 수월합니다.'),
(24,'customer12@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','옷은 압축하지 않고 옷걸이 박스로 옮기는 방법도 있나요?'),
(25,'expert41@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','전자기기는 전용 포장과 방수 덮개를 함께 준비하는 편이 좋습니다.'),
(25,'customer42@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','서류는 지퍼백에 넣고 별도 가방으로 챙기려고 합니다.'),
(30,'expert11@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','메뉴 사진과 가격표를 먼저 모아 두면 작업 범위를 정하기 좋습니다.'),
(30,'customer12@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','직접 메뉴를 수정할 수 있는지도 상담할 때 물어보려고 합니다.'),
(31,'expert51@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','현재 쓰는 엑셀 양식을 바탕으로 필요한 항목을 추릴 수 있어요.'),
(31,'customer52@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','바코드 기능은 나중에 추가하고 수량 입력부터 시작하려고 합니다.'),
(32,'expert21@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','수업별 정원과 취소 가능 시간을 먼저 정해 주세요.'),
(32,'customer22@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','달력에서 수업 종류를 골라 신청하는 흐름을 생각하고 있어요.'),
(33,'expert41@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','사진 용량과 모바일에서 보일 순서를 정리해 두세요.'),
(33,'customer42@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','기존 촬영본을 행사별 폴더로 먼저 나누어 두겠습니다.'),
(34,'expert31@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','관리자별 접근 권한과 기록 보관 기간도 정하면 좋습니다.'),
(34,'customer32@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','전화번호 없이 회원 번호로 관리하는 방법을 검토하고 있습니다.'),
(43,'expert51@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','바닥 재질마다 관리 방법이 달라 제품 안내도 확인해 주세요.'),
(43,'customer52@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','가구를 바로 밀어 넣지 않고 충분히 말린 뒤 정리하겠습니다.'),
(44,'expert41@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','창틀 배수 구멍을 막지 않도록 마무리할 때 살펴보세요.'),
(44,'customer42@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','청소기를 먼저 쓰는 대신 젖은 천으로 시작해 보겠습니다.'),
(45,'expert21@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','배수구 덮개를 주기적으로 말리는 것도 도움이 됩니다.'),
(45,'customer22@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','수전 옆에 작은 마른 수건을 두고 사용해 보겠습니다.'),
(46,'expert11@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','선반을 빼기 전에 원래 위치를 사진으로 남겨 두면 편합니다.'),
(46,'customer12@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','붙박이장 경첩 주변도 이번에 같이 확인해 봐야겠어요.'),
(47,'expert31@example.com','궁금한 점은 댓글로 함께 이야기해 주세요.','분리한 나사는 그릇에 담아 두면 잃어버리지 않아요.'),
(47,'customer32@example.com','안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.','알루미늄 필터는 세제 선택부터 확인해야겠네요.');

INSERT INTO seed195_post_photos VALUES
(4,'5daa003e-1e20-4848-81d7-c3a418cecf91.jpg',0),
(5,'abd56987-aebe-47d2-8f7c-48df5f14bb94.jpg',0),
(8,'0f11d56a-67be-49b3-8573-7a97700a7f4c.jpg',0),
(3,'12904168-fe15-4dc8-9df6-ddc507020606.jpg',0),
(36,'d8c3a907-ba6a-469d-8093-4e69d9cf5284.jpg',0),
(39,'ebea7963-5a18-4ac7-87db-f75fc6cfb701.jpg',0),
(40,'73c6b070-7b0c-493b-8af0-894c14c0c78c.jpg',0),
(37,'74c3a957-9638-4644-962c-3eb84f6d090b.jpg',0),
(42,'2f5d05bc-bad6-42c7-bdb6-dbae87d162e8.jpg',0),
(38,'d40e132c-5fe4-4565-8427-859a847dca8a.jpg',0),
(35,'434ee595-4316-4306-8b89-be7d42425ab9.jpg',0),
(4,'50347254-5e8f-4b63-a33b-082d42401d16.jpg',1),
(41,'797b95bd-3896-4109-bbf3-4912b12e72b1.jpg',0),
(1,'064360f9-82e8-4e0c-b723-a2062cc1a375.jpg',0),
(1,'50f09811-0f6c-4624-a85a-6f2f95294a83.jpg',1),
(2,'aa76c2cf-be28-4f5c-99cc-197b0e69c1d4.jpg',0),
(7,'83d5c398-973d-497f-b61e-b9e2c0188532.jpg',0),
(46,'seed/accounts/4ea93c54a0470a6d410969ed4e282b1857b8d5ee39b1bc39497aed69c3b255ef.jpg',0),
(11,'seed/post-previews/4edd3c547767b74ce5b8500a47f2a5cc45a1a0c7f78ea85452c91137b9c33f04.jpg',0),
(7,'a5cd84ed-18ac-48e1-9a90-71e60abcaf05.jpg',1),
(45,'seed/post-previews/d66799a8c5f442fc5050a24e44ffd58014e11221e5ca7449ea8e6fcddf086b9e.jpg',0),
(47,'seed/post-previews/9de869d4366bf5b3c157c0a589f35583ba4ec6d3585ade73adcd933660e4b079.jpg',0),
(6,'10cd17fb-a6bc-456e-919d-3f8270397af3.jpg',0),
(44,'seed/post-previews/1ff3186710886e31e53e44b8c6cc649f9c4db1dc858134bf38a21d46656b53b2.jpg',0),
(6,'ab8834a5-3e84-4704-8069-ea8b9233458e.jpg',1),
(43,'seed/post-previews/c989c59da9f6b43d3388ae983b4179e61351280272ed803e0bba226702c7a4d3.jpg',0),
(49,'seed/accounts/b012c926b84cf2ee2a937c177623ab226b4488a88bfdc3c6c56beea9758a996a.jpg',0),
(50,'seed/accounts/9aa6442eda00a350e89f8c1dede7dfa374ccc7be0121f79e38866d076eeb5550.jpg',0),
(58,'seed/accounts/635432912a9746c58549034e8731f640cad6edf99d75487d1904da587f8bb18b.jpg',0),
(52,'seed/accounts/549fa5cdae210603006333b0e15da64284deea0779ef56315d62bda8439eebd0.jpg',0),
(53,'seed/accounts/b3feea8bc591fc46962067a6d9af6da5f65a90d6a4aa2da601c968ed25e7d3be.jpg',0),
(55,'seed/accounts/fdd784d615f30ce8fd509a65507304a5faafbb180b686f2d188c4ec0343b406f.jpg',0),
(56,'seed/accounts/2fc29d0f0ea4964e0500616fd9683dedbf5dfbb1f2188a70d973cddae6bf92b8.jpg',0),
(60,'seed/accounts/29dbe8f14c71b14762bab6d01ec486000ac2ea1c4efdb13c2c935afc45100b6f.jpg',0);

INSERT INTO seed195_old_post_photos VALUES
(46,'seed/accounts/4ea93c54a0470a6d410969ed4e282b1857b8d5ee39b1bc39497aed69c3b255ef.jpg',0),
(11,'seed/accounts/e80f5e65ee4dac108a994b09df8ef512c4a5fe4b84621f7310fa8c82cb67f77f.jpg',0),
(45,'seed/accounts/9b38c3f9e3543f9f22eb68db6ce195217395a1d2a346afc606b244e0cd2b7cbe.jpg',0),
(9,'seed/accounts/fc86f1b5a60b0d8faa97de25dfc583048d6de10815722587cb2961fe69ed81e6.jpg',0),
(47,'seed/accounts/9d10c42fe4e1e334e44d77f0b374ae719cb7f68da28f429b726c180303e3789d.jpg',0),
(13,'seed/accounts/8b86c275ce6b7ad07e155462ffa33b672553fb4dad2910535f0349c61fb3abdd.jpg',0),
(44,'seed/accounts/42bb7f0159736776be3c652278a682280fe63faf15a65c119bd6f669ed94e355.jpg',0),
(12,'seed/accounts/d2398d0ca1f40c7d45fe00ccea3cc3b86e8a6ea76f1982292f94fe0d55a2c1d8.jpg',0),
(43,'seed/accounts/4f61d536366c03e2ff32937be59960584cd04a09c7cb9674e25c55507061940d.jpg',0),
(10,'seed/accounts/c91458d9ec331ccac201adcde487fff49023e92275c39ae7d9d23bae11f2fd19.jpg',0);

START TRANSACTION;
-- 다른 DB에 잘못 실행하면 실제 데이터 변경 전에 중단합니다.
INSERT INTO seed195_guard SELECT CASE WHEN
 (EXISTS (SELECT 1 FROM member WHERE email='admin@example.com') AND
  (SELECT COUNT(*) FROM member m JOIN expert_profile ep ON ep.member_id=m.id WHERE m.email REGEXP '^expert([1-9]|10)@demo[.]efy[.]example$')=10)
 OR (EXISTS (SELECT 1 FROM member WHERE email='admin1@example.com') AND EXISTS (SELECT 1 FROM member WHERE email='expert1@example.com'))
 THEN 1 ELSE 0 END;
-- 발표용 DB에서 누락 고수가 있으면 add-experts.sql부터 실행해야 합니다. 변경 전에 중단합니다.
INSERT INTO seed195_guard SELECT CASE WHEN NOT EXISTS (SELECT 1 FROM member WHERE email='admin@example.com') OR
 (SELECT COUNT(*) FROM member m JOIN expert_profile ep ON ep.member_id=m.id WHERE m.email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$' AND m.deleted_at IS NULL)=20
 THEN 1 ELSE 0 END;
-- 필라테스는 기존 피트니스 서비스 174를 그대로 사용합니다. 직접 수정한 프로필은 덮어쓰지 않습니다.
UPDATE member m JOIN expert_profile ep ON ep.member_id=m.id
JOIN second_category c ON c.id=ep.primary_category_id AND c.id=174 AND BINARY c.name=BINARY '피트니스'
LEFT JOIN member collision ON collision.nickname='오늘의 필라테스' AND collision.id<>m.id
SET m.nickname='오늘의 필라테스',ep.short_intro='기구 필라테스와 1:1 자세 교정 수업',
    ep.description='기구 필라테스와 1:1 자세 교정 수업. 작업 범위와 일정은 상담 후 안내해 드립니다.',
    ep.career_description='필라테스 개인·소그룹 수업 경력 6년입니다.'
WHERE m.email='pilates.today@example.com' AND m.deleted_at IS NULL AND collision.id IS NULL
  AND BINARY m.nickname=BINARY '오늘의 요가'
  AND BINARY ep.short_intro=BINARY '소규모 그룹과 1:1 요가 수업'
  AND BINARY ep.description=BINARY '소규모 그룹과 1:1 요가 수업. 작업 범위와 일정은 상담 후 안내해 드립니다.'
  AND BINARY ep.career_description=BINARY '관련 서비스 경력 6년입니다.'
  AND ep.career_years=6
  AND EXISTS (SELECT 1 FROM expert_profile_services s WHERE s.expert_profile_id=ep.id AND s.services_id=174);
SELECT 'pilates_rows_updated' AS item, ROW_COUNT() AS affected;


-- 닉네임은 원래 시드 이름이 남아 있고 새 이름과 충돌하지 않을 때만 바꿉니다.
UPDATE member m JOIN seed195_names d ON m.email=d.email
LEFT JOIN member collision ON collision.nickname=d.new_name AND collision.id<>m.id
SET m.nickname=d.new_name
WHERE m.deleted_at IS NULL AND BINARY m.nickname=BINARY d.old_name AND collision.id IS NULL;
SELECT 'nicknames_updated' AS item, ROW_COUNT() AS affected;


UPDATE member SET password='$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu' WHERE email IN ('hangyeol.move@example.com','sparkle.home@example.com','yoon.english@example.com','sori.music@example.com','codemate@example.com','bareun.fix@example.com','onjip@example.com','damda.snap@example.com','tidyday@example.com','kimcoach@example.com','doorknob@example.com','haeundae.move@example.com','career.note@example.com','forest.art@example.com','saegil@example.com','mindcomma@example.com','hanbit.car@example.com','cleargrout@example.com','haneul.wed@example.com','pilates.today@example.com') AND deleted_at IS NULL AND BINARY password=BINARY '이사/청소';
SELECT 'invalid_hashes_fixed' AS item, ROW_COUNT() AS affected;


-- 기존 시드 문구와 일치하는 게시글만 대상으로 잡습니다. 직접 고친 제목·본문은 건드리지 않습니다.
INSERT INTO seed195_post_targets
SELECT d.seed_id,p.id FROM seed195_posts d JOIN member m ON m.email=d.email
JOIN post p ON p.member_id=m.id AND p.board_type=d.board
WHERE m.deleted_at IS NULL AND p.deleted_at IS NULL
AND ((BINARY p.title=BINARY d.old_title AND BINARY p.content=BINARY d.old_content)
  OR (BINARY p.title=BINARY d.title AND BINARY p.content=BINARY d.content));
UPDATE post p JOIN seed195_post_targets t ON p.id=t.post_id JOIN seed195_posts d ON d.seed_id=t.seed_id
SET p.title=d.title,p.content=d.content
WHERE BINARY p.title=BINARY d.old_title AND BINARY p.content=BINARY d.old_content
AND (BINARY d.title<>BINARY d.old_title OR BINARY d.content<>BINARY d.old_content);
SELECT 'posts_updated' AS item, ROW_COUNT() AS affected;
UPDATE post_comment c JOIN seed195_post_targets t ON c.post_id=t.post_id
JOIN seed195_comments d ON d.seed_id=t.seed_id JOIN member m ON c.member_id=m.id AND m.email=d.email
SET c.content=d.content WHERE c.deleted_at IS NULL AND m.deleted_at IS NULL AND BINARY c.content=BINARY d.old_content;
SELECT 'comments_updated' AS item, ROW_COUNT() AS affected;

-- 시드 외 사진이 한 장이라도 있으면 게시글의 사진 전체를 보존하고 교체를 보류합니다.
-- 삭제 전에 안전하게 교체할 게시글을 확정합니다. 사용자가 추가한 사진과 순서는 건드리지 않습니다.
INSERT INTO seed195_photo_replacements
SELECT t.post_id,t.seed_id FROM seed195_post_targets t
WHERE EXISTS (SELECT 1 FROM post_images pi JOIN file f ON f.id=pi.file_id
  JOIN seed195_old_post_photos old ON old.seed_id=t.seed_id AND old.path=f.storage_path AND old.sort_order=pi.sort_order
  WHERE pi.post_id=t.post_id)
AND NOT EXISTS (SELECT 1 FROM post_images extra JOIN file ef ON ef.id=extra.file_id
  WHERE extra.post_id=t.post_id AND NOT EXISTS
  (SELECT 1 FROM seed195_old_post_photos old WHERE old.seed_id=t.seed_id AND old.path=ef.storage_path AND old.sort_order=extra.sort_order));
DELETE pi FROM post_images pi JOIN seed195_photo_replacements t ON pi.post_id=t.post_id
JOIN file f ON pi.file_id=f.id JOIN seed195_old_post_photos old ON old.seed_id=t.seed_id AND old.path=f.storage_path AND old.sort_order=pi.sort_order
WHERE NOT EXISTS (SELECT 1 FROM seed195_post_photos d WHERE d.seed_id=t.seed_id AND d.path=f.storage_path AND d.sort_order=pi.sort_order);
SELECT 'old_seed_thumbnail_links_removed' AS item, ROW_COUNT() AS affected;

-- 비어 있거나 잘린 시드 사진이 연결된 회원만 원본 사진으로 바꿉니다.
INSERT INTO seed195_member_pending
SELECT m.id,m.email,d.path,m.image_id FROM seed195_member_photos d JOIN member m ON m.email=d.email
JOIN expert_profile ep ON ep.member_id=m.id LEFT JOIN file current_file ON m.image_id=current_file.id
WHERE m.deleted_at IS NULL AND (m.image_id IS NULL OR (current_file.uploader_id=m.id AND (current_file.storage_path=d.old_path OR EXISTS (SELECT 1 FROM seed195_previous_member_photos old WHERE old.email=m.email AND old.path=current_file.storage_path))));
-- 작업 사진을 이미 직접 등록한 고수는 그대로 둡니다.
INSERT INTO seed195_work_pending
SELECT DISTINCT ep.id,m.email FROM seed195_work d JOIN member m ON m.email=d.email JOIN expert_profile ep ON ep.member_id=m.id
WHERE m.deleted_at IS NULL AND NOT EXISTS (SELECT 1 FROM work_image w WHERE w.expert_profile_id=ep.id);
INSERT INTO seed195_post_pending
SELECT t.post_id,t.seed_id FROM seed195_post_targets t WHERE NOT EXISTS (SELECT 1 FROM post_images pi WHERE pi.post_id=t.post_id);
INSERT IGNORE INTO seed195_needed SELECT email,path FROM seed195_member_pending;
INSERT IGNORE INTO seed195_needed SELECT t.email,d.path FROM seed195_work_pending t JOIN seed195_work d ON t.email=d.email;
INSERT IGNORE INTO seed195_needed SELECT d.email,photo.path FROM seed195_post_pending t JOIN seed195_post_photos photo ON t.seed_id=photo.seed_id JOIN seed195_posts d ON t.seed_id=d.seed_id;

-- 같은 저장 경로가 다른 소유자에게 등록되어 있으면 덮어쓰지 않고 중단합니다.
INSERT INTO seed195_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_needed d JOIN member m ON m.email=d.email JOIN file f ON f.storage_path=d.path
WHERE f.uploader_id<>m.id OR f.visibility<>'PUBLIC' OR f.deleted_at IS NOT NULL;
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at)
SELECT m.id,d.path,d.name,d.mime,d.bytes,'PUBLIC',CURRENT_TIMESTAMP
FROM seed195_files d JOIN seed195_needed n ON n.email=d.email AND n.path=d.path
JOIN member m ON m.email=d.email AND m.deleted_at IS NULL
WHERE NOT EXISTS (SELECT 1 FROM file f WHERE f.storage_path=d.path);
SELECT 'file_rows_added' AS item, ROW_COUNT() AS affected;
UPDATE member m JOIN seed195_member_pending d ON m.id=d.member_id
JOIN file f ON f.storage_path=d.path AND f.uploader_id=m.id AND f.visibility='PUBLIC' AND f.deleted_at IS NULL
SET m.image_id=f.id WHERE m.deleted_at IS NULL AND m.image_id <=> d.old_image_id;
SELECT 'member_photos_connected' AS item, ROW_COUNT() AS affected;
INSERT INTO work_image (expert_profile_id,file_id,description,display_order)
SELECT t.profile_id,f.id,d.description,d.display_order FROM seed195_work_pending t
JOIN seed195_work d ON d.email=t.email JOIN member m ON m.email=t.email AND m.deleted_at IS NULL
JOIN file f ON f.storage_path=d.path AND f.uploader_id=m.id AND f.visibility='PUBLIC' AND f.deleted_at IS NULL;
SELECT 'work_photos_added' AS item, ROW_COUNT() AS affected;
INSERT INTO post_images (post_id,file_id,sort_order)
SELECT t.post_id,f.id,d.sort_order FROM seed195_post_pending t JOIN seed195_post_photos d ON t.seed_id=d.seed_id
JOIN post p ON p.id=t.post_id AND p.deleted_at IS NULL JOIN file f ON f.storage_path=d.path AND f.uploader_id=p.member_id AND f.visibility='PUBLIC' AND f.deleted_at IS NULL;
SELECT 'post_photos_added' AS item, ROW_COUNT() AS affected;
-- 이번 처리 대상의 필수 연결은 COMMIT 전에 검사합니다. 수동 변경으로 제외한 행은 별도 확인합니다.
INSERT INTO seed195_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_member_pending d JOIN member m ON m.id=d.member_id LEFT JOIN file f ON f.id=m.image_id
WHERE f.id IS NULL OR f.storage_path<>d.path OR f.uploader_id<>m.id OR f.visibility<>'PUBLIC' OR f.deleted_at IS NOT NULL;
INSERT INTO seed195_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_post_pending t JOIN seed195_post_photos d ON d.seed_id=t.seed_id
WHERE NOT EXISTS (SELECT 1 FROM post_images pi JOIN file f ON f.id=pi.file_id
WHERE pi.post_id=t.post_id AND f.storage_path=d.path AND pi.sort_order=d.sort_order);
INSERT INTO seed195_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_work_pending t JOIN seed195_work d ON t.email=d.email
WHERE NOT EXISTS (SELECT 1 FROM work_image w JOIN file f ON f.id=w.file_id
WHERE w.expert_profile_id=t.profile_id AND w.display_order=d.display_order AND f.storage_path=d.path);
COMMIT;

-- 존재하지 않는 계정은 만들지 않습니다. 수동 수정·삭제·충돌로 건너뛴 대상은 아래 결과를 보고 확인합니다.
SELECT d.email,'missing_or_deleted_account' AS skipped FROM seed195_member_photos d LEFT JOIN member m ON m.email=d.email AND m.deleted_at IS NULL WHERE m.id IS NULL;
SELECT d.email,'nickname_changed_or_conflict' AS skipped FROM seed195_names d JOIN member m ON m.email=d.email WHERE m.deleted_at IS NULL AND BINARY m.nickname<>BINARY d.new_name;
-- 알려진 시드 사진과 다른 사진이 섞인 게시글은 전체 사진 연결을 그대로 보존했습니다.
SELECT t.post_id,'mixed_photos_preserved' AS skipped FROM seed195_post_targets t
WHERE EXISTS (SELECT 1 FROM post_images pi JOIN file f ON f.id=pi.file_id JOIN seed195_old_post_photos old ON old.seed_id=t.seed_id AND old.path=f.storage_path AND old.sort_order=pi.sort_order WHERE pi.post_id=t.post_id)
AND NOT EXISTS (SELECT 1 FROM seed195_photo_replacements safe WHERE safe.post_id=t.post_id);
DROP TEMPORARY TABLE seed195_needed,seed195_post_pending,seed195_work_pending,seed195_member_pending,seed195_post_targets,seed195_old_post_photos,seed195_post_photos,seed195_comments,seed195_posts,seed195_work,seed195_member_photos,seed195_files,seed195_names,seed195_previous_member_photos,seed195_photo_replacements,seed195_guard;
