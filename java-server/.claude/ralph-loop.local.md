---
active: true
iteration: 1
session_id: 
max_iterations: 30
completion_promise: "DONE"
started_at: "2026-04-19T03:55:53Z"
---

Verify and fix my existing Java student management system.

IMPORTANT:
- First fully understand the project before making any changes
- Do not ask for user confirmation during execution
- You are allowed to run commands and modify files automatically
- Continue execution independently until completion

Project type:
- Existing half-finished Maven project
- Use Maven to build and run (mvn clean compile, mvn exec:java)
- Do not rebuild the project
- Do not rewrite large parts of the code
- Do not change the project structure unless absolutely necessary

Primary goal:
Verify whether the current system works correctly, including business logic and frontend-backend consistency. Fix only broken or inconsistent parts.

Core checks:
1. Analyze project structure
   - identify frontend, backend, and data flow
   - identify controllers, services, models, and entry point

2. Verify core functionality (CRUD)
   - add student
   - delete student
   - update student
   - query/search student
   - list/display students

3. Verify business logic correctness
   - correct handling of IDs
   - null checks and edge cases
   - duplicate data handling
   - input validation

4. Verify frontend-backend consistency
   - API paths and HTTP methods match
   - request/response fields match
   - frontend forms match backend model fields
   - displayed data matches backend data

5. Run and test
   - compile with Maven
   - run the project
   - test main features
   - detect compile/runtime errors

Fixing rules:
- ONLY fix necessary parts
- DO NOT refactor large code blocks
- DO NOT rename files unnecessarily
- DO NOT change architecture
- preserve all working features
- prefer minimal and safe fixes

Execution loop:
1. inspect code
2. compile
3. run
4. test features
5. fix issues
6. repeat until stable

Success condition:
- project compiles successfully
- program runs without crashing
- CRUD features work logically
- frontend and backend are consistent

When finished, print exactly: DONE
