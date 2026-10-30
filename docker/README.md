# Document storage for local development

The original MinIO Community image in Quay currently returns `401 UNAUTHORIZED` to anonymous pulls. This local Compose service therefore runs RustFS, an S3-compatible object store, while keeping the same S3 endpoint and persistent volume expected by the backend.

## Start the storage service

1. Copy `.env.example` to `.env` if you have not already done so.
2. Edit `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY` in `.env` to use unique values. Keep those values private. For this local setup, the backend uses the same credentials as the RustFS Console.
3. From this directory, run `docker compose up -d`.
4. Check `docker compose ps`. The S3 API is at `http://localhost:9000`; the Console is at `http://localhost:9001`.
5. Sign in to the Console using `RUSTFS_ACCESS_KEY` and `RUSTFS_SECRET_KEY`, then create a bucket named `student-documents`.

If you already copied the old `.env.example`, do not overwrite it. Keep your existing `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY` values; Compose now uses them for RustFS and Spring. The old `MINIO_IMAGE`, `MINIO_ROOT_USER`, and `MINIO_ROOT_PASSWORD` entries are ignored.

The backend runs on the host and should use `MINIO_ENDPOINT=http://localhost:9000`, plus the same `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY` stored in `docker/.env`. If the backend runs in Compose, use `MINIO_ENDPOINT=http://minio:9000`.

The named `minio-data` volume persists objects when the container is restarted or recreated. `docker compose down -v` deletes the volume and its contents; use `docker compose down` when stopping while preserving documents.

## Document limits and deletion

Spring Boot enforces a 25 MiB file limit and a 26 MiB multipart request limit. A deleted document disappears from active results immediately; the backend purges its stored object after 30 days. Back up MySQL metadata and the object storage volume together.

## Before production

- This RustFS Compose setup is for local development. Review the storage product, image version, compatibility, security, and support requirements before deployment.
- Keep the Console and S3 endpoint private; configure TLS and network controls before exposing storage.
- The project’s document endpoints do not yet enforce user authentication/authorization. Do not expose them publicly.
- Store secrets in a secret manager and never commit `.env`.
- Back up MySQL metadata and stored objects on a schedule, and test restoring them together.
