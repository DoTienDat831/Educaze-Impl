package com.example.student_management.controller;

import com.example.student_management.entity.Class;
import com.example.student_management.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {
    private final ClassService classService;
    public ClassController(ClassService classService) { this.classService = classService; }
    @PostMapping public ResponseEntity<Class> createClass(@RequestBody Class studentClass) { return ResponseEntity.ok(classService.createClass(studentClass)); }
    @GetMapping public ResponseEntity<List<Class>> getAllClasses() { return ResponseEntity.ok(classService.getAllClasses()); }
    @GetMapping("/{id}") public ResponseEntity<Class> getClassById(@PathVariable Long id) { Class classEntity = classService.getClassById(id); return classEntity == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(classEntity); }
    @PutMapping("/{id}") public ResponseEntity<Class> updateClass(@PathVariable Long id, @RequestBody Class studentClass) { Class updatedClass = classService.updateClass(id, studentClass); return updatedClass == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedClass); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deleteClass(@PathVariable Long id) { if (classService.getClassById(id) == null) return ResponseEntity.notFound().build(); classService.deleteClass(id); return ResponseEntity.noContent().build(); }
}
