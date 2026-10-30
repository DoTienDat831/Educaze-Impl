import { useCallback, useEffect, useMemo, useState } from "react";
import departmentApi from "../api/departmentApi";

const emptyForm = { departmentCode: "", departmentName: "", tuitionFee: "" };

function useDepartments() {
    const [departments, setDepartments] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadDepartments = useCallback(async () => {
        setLoading(true);
        try {
            const { data } = await departmentApi.getAll();
            setDepartments(data);
            setError("");
        } catch (err) {
            setError(err.response?.data?.message || "Không thể tải danh sách khoa.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadDepartments();
    }, [loadDepartments]);

    const visibleDepartments = useMemo(() => {
        const query = search.trim().toLowerCase();
        if (!query) return departments;
        return departments.filter((department) =>
            department.departmentName?.toLowerCase().includes(query) ||
            department.departmentCode?.toLowerCase().includes(query)
        );
    }, [departments, search]);

    const openCreateForm = () => {
        setForm(emptyForm);
        setEditingId(null);
        setIsFormOpen(true);
        setError("");
    };

    const openEditForm = (department) => {
        setForm({
            departmentCode: department.departmentCode || "",
            departmentName: department.departmentName || "",
            tuitionFee: department.tuitionFee ?? ""
        });
        setEditingId(department.id);
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
            ...form,
            tuitionFee: Number(form.tuitionFee)
        };

        try {
            if (editingId == null) {
                await departmentApi.create(payload);
            } else {
                await departmentApi.update(editingId, payload);
            }
            closeForm();
            await loadDepartments();
        } catch (err) {
            setError(err.response?.data?.message || "Không thể lưu thông tin khoa.");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (department) => {
        if (!window.confirm(`Xóa khoa ${department.departmentName}?`)) return;
        setError("");
        try {
            await departmentApi.delete(department.id);
            await loadDepartments();
        } catch (err) {
            setError(err.response?.data?.message || "Không thể xóa khoa này.");
        }
    };

    const formatTuition = (amount) => {
        if (amount == null || amount === "") return "—";
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
            maximumFractionDigits: 0
        }).format(Number(amount));
    };

    return {
        departments,
        visibleDepartments,
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
        handleDelete,
        formatTuition
    };
}

export default useDepartments;
