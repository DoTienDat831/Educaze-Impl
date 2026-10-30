import { useEffect, useMemo, useState } from "react";
import departmentApi from "../api/departmentApi";
import studentApi from "../api/studentApi";

function useDashboard() {
    const [departments, setDepartments] = useState([]);
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let active = true;

        const loadDashboard = async () => {
            setLoading(true);
            setError("");

            const [departmentResult, studentResult] = await Promise.allSettled([
                departmentApi.getAll(),
                studentApi.getAll()
            ]);

            if (!active) return;

            if (departmentResult.status === "fulfilled") setDepartments(departmentResult.value.data || []);
            if (studentResult.status === "fulfilled") setStudents(studentResult.value.data || []);

            const failed = [departmentResult, studentResult].some(
                (result) => result.status === "rejected"
            );
            if (failed) setError("Some dashboard data could not be loaded. Counts may be incomplete.");
            setLoading(false);
        };

        loadDashboard();
        return () => { active = false; };
    }, []);

    const departmentStats = useMemo(() => {
        const counts = new Map();
        students.forEach((student) => {
            const departmentId = student.department?.id ?? student.studentClass?.department?.id;
            if (departmentId != null) {
                const key = String(departmentId);
                counts.set(key, (counts.get(key) || 0) + 1);
            }
        });

        const stats = departments.map((department) => ({
            id: department.id,
            name: department.departmentName || department.name || "Unnamed department",
            code: department.departmentCode || "",
            studentCount: counts.get(String(department.id)) || 0
        }));

        const unassignedCount = students.filter(
            (student) => student.department?.id == null && student.studentClass?.department?.id == null
        ).length;
        if (unassignedCount > 0) {
            stats.push({
                id: "unassigned",
                name: "Chưa phân khoa",
                code: "",
                studentCount: unassignedCount
            });
        }
        return stats;
    }, [departments, students]);

    return {
        departmentStats,
        totalDepartments: departments.length,
        totalStudents: students.length,
        loading,
        error
    };
}

export default useDashboard;
