
space
------
id (PK)
tenant_id
name
description
created_at
created_by

field_definition
----------------
id (PK)
space_id (FK)
type
label
data_type
ui_json
validation_json
status
created_at
created_by

form
----
id (PK)
space_id
name
description
status
created_at
created_by

form_field
-----------
id (PK)
form_id
field_id
field_order
required_override
override_json


```mermaid
SPACE — Example Row
This is your logical workspace container.

{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "tenant_id": "tenant_1",
  "name": "HR Space",
  "description": "Space for recruitment and onboarding forms",
  "created_at": 1705400000000,
  "updated_at": 1705400000000,
  "created_by": "admin",
  "updated_by": "admin"
}

FIELD_DEFINITION — Example Row

Reusable field template.
{
  "id": "8c3f0b61-4f41-4c71-8a7c-6f53fd123abc",
  "space_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",

  "type": "TEXT",
  "label": "Email Address",
  "data_type": "STRING",

  "ui_json": {
    "placeholder": "Enter your email",
    "component": "input",
    "subType": "email",
    "width": 12
  },

  "validation_json": {
    "required": true,
    "regex": "^[A-Za-z0-9+_.-]+@(.+)$",
    "minLength": 5,
    "maxLength": 100
  },

  "status": "ACTIVE",

  "created_at": 1705400500000,
  "updated_at": 1705400500000,
  "created_by": "admin",
  "updated_by": "admin"
}

FORM — Example Row

Logical dynamic table definition.
{
  "id": "b18a9e7c-7b93-4b4c-bb87-d8e0adfd7891",
  "space_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",

  "name": "Job Application",
  "description": "Backend developer job application form",

  "status": "PUBLISHED",

  "created_at": 1705401000000,
  "updated_at": 1705401000000,
  "created_by": "admin",
  "updated_by": "admin"
}

FORM_FIELD — Example Row

This maps field → form and applies overrides.
{
  "id": "0f6b4f3c-8eab-4e44-9c3d-9c1ab33f5555",

  "form_id": "b18a9e7c-7b93-4b4c-bb87-d8e0adfd7891",
  "field_id": "8c3f0b61-4f41-4c71-8a7c-6f53fd123abc",

  "field_order": 1,

  "required_override": true,

  "override_json": {
    "label": "Official Email Address",
    "ui": {
      "placeholder": "Enter company email"
    }
  }
}


🔥 What Happens At Runtime (Important)

Your backend will produce this merged field config:

Final Field Sent To Angular
{
"id": "8c3f0b61-4f41-4c71-8a7c-6f53fd123abc",
"type": "TEXT",
"label": "Official Email Address",
"dataType": "STRING",

"ui": {
"placeholder": "Enter company email",
"component": "input",
"subType": "email",
"width": 12
},

"validation": {
"required": true,
"regex": "^[A-Za-z0-9+_.-]+@(.+)$",
"minLength": 5,
"maxLength": 100
}
}

Override applied cleanly.