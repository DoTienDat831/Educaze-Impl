# REST API contract

All routes use the `/api` prefix and require a bearer token, except `POST /auth/login`.

## Status conventions

- `200 OK`: successful read or update.
- `201 Created`: successful create or document upload.
- `204 No Content`: successful delete.
- `400 Bad Request`: invalid body, field validation, or invalid relationship ID.
- `401 Unauthorized`: missing, invalid, or expired token.
- `404 Not Found`: path ID does not identify an existing resource.
- `409 Conflict`: unique value already exists, or a referenced record prevents deletion.

Errors use the `ApiError` response with `timestamp`, `status`, `error`, `message`, `path`, and `validationErrors`.

## Students

`POST /students` and `PUT /students/{id}` accept the same full representation:

```json
{
  "name": "Nguyen An",
  "dateOfBirth": "2005-03-14",
  "gender": "Female",
  "enrollmentTime": "2023-09-01",
  "email": "an@example.edu",
  "departmentId": 2,
  "classId": 5
}
```

`name` and `departmentId` are required. `dateOfBirth` must be in the past. `classId` is optional; setting it to `null` in a PUT explicitly unassigns the student. A selected class must exist and belong to the selected department. The server generates `studentCode`; clients cannot set it.

Responses keep `department` and `studentClass` as nested read-only summaries for compatibility with the UI. `GET /students` and `GET /students/{id}` return `StudentResponse` objects.

## Departments

`POST /departments` and `PUT /departments/{id}` accept:

```json
{
  "departmentCode": "CS",
  "departmentName": "Computer Science",
  "tuitionFee": 12000000
}
```

All fields are required. Department code and name are unique. A department referenced by a class or student cannot be deleted (`409`).

## Classes

`POST /classes` and `PUT /classes/{id}` accept:

```json
{
  "name": "CS2026A",
  "departmentId": 2
}
```

Both fields are required. A class with assigned students cannot be deleted (`409`).

## Documents

- `GET /documents`: list document summaries.
- `POST /documents` (`multipart/form-data`, field `file`): upload a document.
- `GET /documents/{id}/download`: download a document.
- `DELETE /documents/{id}`: soft-delete an existing document.

The existing 25 MiB size limit and allowed file types apply.
