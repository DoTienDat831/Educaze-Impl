package com.example.student_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", unique = true)
    private String studentCode;

    @NotBlank
    private String name;

    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;

    private LocalDate enrollmentTime;

    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Class studentClass;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Student() {
    }

    public Student(
            String name,
            LocalDate dateOfBirth,
            String studentCode,
            LocalDate enrollmentTime,
            Class studentClass,
            String email
    ) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.studentCode = studentCode;
        this.enrollmentTime = enrollmentTime;
        this.studentClass = studentClass;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getEnrollmentTime() {
        return enrollmentTime;
    }

    public void setEnrollmentTime(LocalDate enrollmentTime) {
        this.enrollmentTime = enrollmentTime;
    }

    public Class getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(Class studentClass) {
        this.studentClass = studentClass;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
