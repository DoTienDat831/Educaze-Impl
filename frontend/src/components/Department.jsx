import { Building2, CirclePlus, Pencil, Search, Trash2, X, Bell, Mail } from "lucide-react";
import useDepartments from "../hooks/useDepartments";
import "./Department.css";

function Department() {
    const {
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
    } = useDepartments();

    return (
        <div className="department-page">
            <header className="department-header">
                <div>
                    <h1>Departments</h1>
                </div>
                <button className="department-primary-button" type="button" onClick={openCreateForm}>
                    <CirclePlus size={17} />
                    <span>Add department</span>
                </button>
            </header>

            <section className="department-summary-card">
                <div className="department-summary-icon"><Building2 size={21} /></div>
                <div>
                    <span>Total departments</span>
                    <strong>{departments.length}</strong>
                </div>
            </section>

            {isFormOpen && (
                <section className="department-form-card" aria-labelledby="department-form-title">

                    <div className="department-form-heading">
                        <div>
                            <h2 id="department-form-title">{editingId == null ? "Add department" : "Update department"}</h2>
                            <p>Enter the department details below.</p>
                        </div>
                        <button className="department-icon-button" type="button" onClick={closeForm} aria-label="Close form">
                            <X size={18} />
                        </button>
                    </div>

                    <form className="department-form" onSubmit={handleSubmit}>

                        <label>
                            <input name="departmentCode" value={form.departmentCode} onChange={handleChange} required placeholder="Department code" />
                        </label>
                        <label>
                            <input name="departmentName" value={form.departmentName} onChange={handleChange} required placeholder="Name"/>
                        </label>
                        <label>
                            <input name="tuitionFee" type="number" min="0" step="1" value={form.tuitionFee} onChange={handleChange} required placeholder="Tuition fee"/>
                        </label>    

                        <div className="department-form-actions">
                            <button className="department-primary-button" type="submit" disabled={saving}>
                                {saving ? "Saving…" : editingId == null ? "Create department" : "Save changes"}
                            </button>
                        </div>

                    </form>
                </section>
            )}

            {error && <p className="department-error" role="alert">{error}</p>}

            <section className="department-list-card">
                <div className="department-list-heading">
                    <div>
                        <h2>Department list</h2>
                        <p>{visibleDepartments.length} department{visibleDepartments.length === 1 ? "" : "s"}</p>
                    </div>
                    <label className="department-search">
                        <Search size={16} />
                        <input
                            type="search"
                            placeholder="Search departments"
                            value={search}
                            onChange={(event) => setSearch(event.target.value)}
                            aria-label="Search departments"
                        />
                    </label>
                </div>
                <div className="department-table-wrap">
                    <table className="department-table">
                        <thead>
                            <tr><th>Code</th><th>Department</th><th>Tuition fee</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="4" className="department-empty">Loading departments…</td></tr>
                            ) : visibleDepartments.length === 0 ? (
                                <tr><td colSpan="4" className="department-empty">No departments found.</td></tr>
                            ) : visibleDepartments.map((department) => (
                                <tr key={department.id}>
                                    <td><span className="department-code">{department.departmentCode}</span></td>
                                    <td className="department-name-cell">{department.departmentName}</td>
                                    <td>{formatTuition(department.tuitionFee)}</td>
                                    <td>
                                        <div className="department-row-actions">
                                            <button type="button" onClick={() => openEditForm(department)} aria-label={`Edit ${department.departmentName}`} title="Edit">
                                                <Pencil size={15} />
                                            </button>
                                            <button type="button" onClick={() => handleDelete(department)} aria-label={`Delete ${department.departmentName}`} title="Delete">
                                                <Trash2 size={15} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default Department;
