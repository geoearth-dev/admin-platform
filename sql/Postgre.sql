CREATE TABLE "test_user" (
                             "id" SERIALNOT NULL,
                             "name" VARCHAR(50) NOT NULL,
                             "created_at" TIMESTAMP NOT NULL DEFAULT now(),
                             "email" VARCHAR(100) NULL DEFAULT NULL::character varying,
                             PRIMARY KEY ("id")
);
COMMENT ON COLUMN "test_user"."id" IS '';
COMMENT ON COLUMN "test_user"."name" IS '';
COMMENT ON COLUMN "test_user"."created_at" IS '';
COMMENT ON COLUMN "test_user"."email" IS '';
