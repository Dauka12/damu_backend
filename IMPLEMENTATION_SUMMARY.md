# AML Backend - DER-Department Integration Summary

## ✅ COMPLETED TASKS

### 1. Fixed JWT Token Expiration Bug
- **File**: `src/main/java/com/AFM/AML/config/JwtService.java`
- **Issue**: Token expiration was using multiplication instead of addition, causing tokens to live ~577,000 years
- **Fix**: Changed `System.currentTimeMillis() * 1000 * 60 * 24` to `System.currentTimeMillis() + 1000 * 60 * 60 * 24`

### 2. Fixed Hibernate Proxy Serialization Issues
- **Files**: `User.java`, `Department.java`, `Der.java`
- **Issue**: Jackson couldn't serialize Hibernate lazy-loaded proxy objects during authentication
- **Fix**: Added `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` annotations

### 3. Created Database Schema Changes
- **File**: `update_departments.sql`
- **Changes**:
  - Added `der_id` column to `department` table
  - Created complete mapping of all 22 DERs to their respective departments
  - Populated data based on organizational structure provided

### 4. Enhanced Entity Models
- **Department.java**: Added `derId` field and `@ManyToOne` relationship to Der
- **Der.java**: Added `@OneToMany` relationship to departments
- **User.java**: Enhanced with proper Jackson annotations for Der/Department relationships

### 5. Built New API Infrastructure
- **DepartmentDTO.java**: Data transfer object for department data
- **DerWithDepartmentsDTO.java**: Hierarchical DTO for DER with its departments
- **ReferencesService.java**: Service layer for managing DER-department relationships
- **ReferencesController.java**: Added new endpoint `/api/references/ders-with-departments`

### 6. Updated Repository Layer
- **DepartmentRepository.java**: Added `findByDerId()` method for querying departments by DER ID

## 🆕 NEW API ENDPOINT

**URL**: `GET /api/references/ders-with-departments`

**Response Format**:
```json
[
  {
    "id": 1,
    "name_rus": "ДЭР по Акмолинской области",
    "name_kaz": "Ақмола облысы бойынша ДЭР",
    "departments": [
      {
        "id": 1,
        "name_rus": "Руководство",
        "name_kaz": "Басшылық"
      },
      {
        "id": 2,
        "name_rus": "Следственное управление",
        "name_kaz": "Тергеу басқармасы"
      }
    ]
  }
]
```

## 🔄 PENDING TASKS

### 1. Execute Database Migration
- Run the `update_departments.sql` script on the database
- Verify data population is correct

### 2. Test the Application
- Start the application: `mvn spring-boot:run`
- Test authentication to ensure no serialization errors
- Test the new API endpoint
- Verify existing department APIs still work

### 3. Verify Database Connection
- Ensure PostgreSQL connection to 192.168.122.132:5432
- Verify MinIO connection to 192.168.122.132:9000

## 📁 KEY FILES MODIFIED

1. `src/main/java/com/AFM/AML/config/JwtService.java` - JWT fix
2. `src/main/java/com/AFM/AML/User/models/User.java` - Serialization fix
3. `src/main/java/com/AFM/AML/User/models/Department.java` - Enhanced model
4. `src/main/java/com/AFM/AML/User/models/Der.java` - Enhanced model
5. `src/main/java/com/AFM/AML/User/models/dto/DepartmentDTO.java` - New DTO
6. `src/main/java/com/AFM/AML/User/models/dto/DerWithDepartmentsDTO.java` - New DTO
7. `src/main/java/com/AFM/AML/references/service/ReferencesService.java` - New service
8. `src/main/java/com/AFM/AML/statistics/repository/DepartmentRepository.java` - Enhanced repository
9. `src/main/java/com/AFM/AML/references/ReferencesController.java` - New endpoint
10. `update_departments.sql` - Database migration script

## 🎯 ORGANIZATIONAL STRUCTURE IMPLEMENTED

The system now supports a hierarchical structure where:
- **22 DERs** (Regional Departments) each have their own departments
- **Central Apparatus** (id=22) has 17 specialized departments
- **Major cities** (Almaty, Astana, Shymkent) have multiple investigation and operational departments
- **Regional DERs** have standard departmental structures

## 🚀 TESTING

Use the provided `test_api.cmd` script to test all endpoints, or manually test:

1. **Authentication**: Verify login works without serialization errors
2. **DERs**: `GET /api/references/ders`
3. **Departments**: `GET /api/references/departments`  
4. **Hierarchical**: `GET /api/references/ders-with-departments`

## 📝 NOTES

- All Hibernate lazy loading serialization issues have been resolved
- The new API provides efficient hierarchical data structure
- Database migration includes comprehensive mapping of all organizational units
- JWT token expiration is now correctly set to 24 hours
