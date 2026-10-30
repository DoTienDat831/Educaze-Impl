import { useCallback, useEffect, useState } from "react";
import studentApi from "../api/studentApi";
import departmentApi from "../api/departmentApi";

const empty = {
    name: "",
    dateOfBirth: "",
    gender: "",
    enrollmentTime: "",
    email:"",
    departmentId: ""
};

function useStudents() {
    const [students, setStudents] = useState([]);
    const [addForm, setAddForm] = useState(empty);
    const [editForm, setEditForm] = useState(empty);
    const [editingId, setEditingId] = useState(null);
    const [search, setSearch] = useState("");
    const [error, setError] = useState("");
    const [addError, setAddError] = useState("");
    const [editError, setEditError] = useState("");
    const [departments, setDepartments] = useState([]);
    const [departmentError, setDepartmentError] = useState("");

    // Lấy danh sách student từ backend
    const load = async () => {
        setError("");
        try {
            const { data } = await studentApi.getAll();
            setStudents(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                "Không thể tải danh sách sinh viên. Hãy kiểm tra backend và kết nối cơ sở dữ liệu."
            );
        }
    };

    const loadDepartments = useCallback(async () => {
        setDepartmentError("");
        try {
            const { data } = await departmentApi.getAll();
            setDepartments(Array.isArray(data) ? data : []);
        } catch (err) {
            setDepartmentError(
                err.response?.data?.message || "Không thể tải danh sách department."
            );
        }
    }, []);

    // Chỉ load dữ liệu khi component được tạo
    useEffect(() => {
        load();
        loadDepartments();
    }, []);

    const updateForm = (form, setForm, e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const makeStudentPayload = (form) => ({
        name: form.name,
        dateOfBirth: form.dateOfBirth || null,
        gender: form.gender || null,
        enrollmentTime: form.enrollmentTime || null,
        email: form.email,
        department: { id: Number(form.departmentId) }
    });

    // Luồng tạo mới độc lập với luồng cập nhật.
    const handleAddChange = (e) => updateForm(addForm, setAddForm, e);
    const handleEditChange = (e) => updateForm(editForm, setEditForm, e);

    const handleAddSubmit = async (e) => {
        e.preventDefault();
        setAddError("");
        try {
            await studentApi.create(makeStudentPayload(addForm));
            setAddForm(empty);
            await load();
        } catch (err) {
            setAddError(
                err.response?.data?.message ||
                "Không thể thêm sinh viên"
            );
        }
    };

    // Mở form cập nhật riêng; không thay đổi form thêm mới.
    const handleEdit = (student) => {
        setEditForm({
            name: student.name || "",
            dateOfBirth: student.dateOfBirth || "",
            gender: student.gender || "",
            enrollmentTime: student.enrollmentTime || "",
            email: student.email || "",
            departmentId: student.department?.id
                ? String(student.department.id)
                : student.studentClass?.department?.id
                    ? String(student.studentClass.department.id)
                    : ""
        });

        setEditingId(student.id);
        setEditError("");
    };

    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        if (editingId == null) return;
        setEditError("");
        try {
            await studentApi.update(editingId, makeStudentPayload(editForm));
            setEditingId(null);
            setEditForm(empty);
            await load();
        } catch (err) {
            setEditError(
                err.response?.data?.message ||
                "Không thể cập nhật sinh viên"
            );
        }
    };

    const handleCancelEdit = () => {
        setEditingId(null);
        setEditForm(empty);
        setEditError("");
    };

    // Xóa student
    const handleDelete = async (id) => {
        if (!window.confirm("Delete this student?")) {
            return;
        }

        try {
            await studentApi.delete(id);
            await load();
        } catch (err) {
            setError(
                err.response?.data?.message ||
                "Không thể xóa sinh viên"
            );
        }
    };

    // Tìm theo ID số hoặc mã sinh viên.
    const query = search.trim().toLowerCase();
    const filteredStudents = students.filter((student) =>
        String(student.id).toLowerCase().includes(query) ||
        student.studentCode?.toLowerCase().includes(query)
    );

    return {
        students: filteredStudents,
        departments,
        departmentError,
        loadDepartments,
        addForm,
        editForm,
        editingId,
        search,
        error,
        addError,
        editError,
        setSearch,
        handleAddChange,
        handleEditChange,
        handleAddSubmit,
        handleUpdateSubmit,
        handleEdit,
        handleCancelEdit,
        handleDelete
    };
}

export default useStudents;
