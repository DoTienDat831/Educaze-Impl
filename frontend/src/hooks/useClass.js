import { useCallback, useEffect, useMemo, useState } from "react";
import classApi from "../api/classApi";
import departmentApi from "../api/departmentApi";

const emptyForm = { name: "", departmentId: "" };

function useClass() {
    const [classes, setClasses] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadData = useCallback(async () => {
        setLoading(true);
        setError("");
        const [classResult, departmentResult] = await Promise.allSettled([
            classApi.getAll(),
            departmentApi.getAll()
        ]);

        if (classResult.status === "fulfilled") {
            setClasses(classResult.value.data);
        } else {
            setError(classResult.reason?.response?.data?.message || "Không thể tải danh sách lớp.");
        }

        if (departmentResult.status === "fulfilled") {
            setDepartments(departmentResult.value.data);
        } else if (classResult.status === "fulfilled") {
            setError(departmentResult.reason?.response?.data?.message || "Không thể tải danh sách khoa.");
        }

        setLoading(false);
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const visibleClasses = useMemo(() => {
        const query = search.trim().toLowerCase();
        if (!query) return classes;
        return classes.filter((studentClass) =>
            studentClass.name?.toLowerCase().includes(query) ||
            studentClass.department?.departmentName?.toLowerCase().includes(query) ||
            studentClass.department?.departmentCode?.toLowerCase().includes(query)
        );
    }, [classes, search]);

    const openCreateForm = () => {
        setForm(emptyForm);
        setEditingId(null);
        setIsFormOpen(true);
        setError("");
    };

    const openEditForm = (studentClass) => {
        setForm({
            name: studentClass.name || "",
            departmentId: studentClass.department?.id ? String(studentClass.department.id) : ""
        });
        setEditingId(studentClass.id);
        setIsFormOpen(true);
        setError("");
    };

    const closeForm = () => {
        setIsFormOpen(false);
        setEditingId(null);
        setForm(emptyForm);
    };

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setSaving(true);
        setError("");
        const payload = {
            name: form.name.trim(),
            department: form.departmentId ? { id: Number(form.departmentId) } : null
        };

        try {
            if (editingId == null) {
                await classApi.create(payload);
            } else {
                await classApi.update(editingId, payload);
            }
            closeForm();
            await loadData();
        } catch (err) {
            setError(err.response?.data?.message || "Không thể lưu lớp.");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (studentClass) => {
        if (!window.confirm(`Xóa lớp ${studentClass.name}?`)) return;
        setError("");
        try {
            await classApi.delete(studentClass.id);
            await loadData();
        } catch (err) {
            setError(err.response?.data?.message || "Không thể xóa lớp này.");
        }
    };

    return {
        classes,
        visibleClasses,
        departments,
        form,
        editingId,
        isFormOpen,
        search,
        loading,
        saving,
        error,
        setSearch,
        openCreateForm,
        openEditForm,
        closeForm,
        handleChange,
        handleSubmit,
        handleDelete
    };
}

export default useClass;
