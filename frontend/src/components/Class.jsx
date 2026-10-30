import { BookOpen, CirclePlus, Pencil, Search, Trash2, X } from "lucide-react";
import useClass from "../hooks/useClass";
import "./Class.css";

function ClassPage() {
    const {
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
    } = useClass();

    return (
        <div className="class-page">
            <header className="class-header">
                <div>
                    <h1>Classes</h1>
                </div>
                <button className="class-primary-button" type="button" onClick={openCreateForm}>
                    <CirclePlus size={17} />
                    <span>Add class</span>
                </button>
            </header>

            <section className="class-summary-card">
                <div className="class-summary-icon"><BookOpen size={21} /></div>
                <div>
                    <span>Total classes</span>
                    <strong>{classes.length}</strong>
                </div>
            </section>

            {isFormOpen && (
                <section className="class-form-card" aria-labelledby="class-form-title">
                    <div className="class-form-heading">
                        <div>
                            <h2 id="class-form-title">{editingId == null ? "Add class" : "Update class"}</h2>
                            <p>Enter the class details below.</p>
                        </div>
                        <button className="class-icon-button" type="button" onClick={closeForm} aria-label="Close form">
                            <X size={18} />
                        </button>
                    </div>

                    <form className="class-form" onSubmit={handleSubmit}>
                        <label>
                            <input name="name" value={form.name} onChange={handleChange} required maxLength={150} placeholder="Class name"/>
                        </label>
                        <label>
                            <select name="departmentId" value={form.departmentId} onChange={handleChange}>
                                <option value="">Department</option>
                                {departments.map((department) => (
                                    <option key={department.id} value={department.id}>
                                        {department.departmentName} ({department.departmentCode})
                                    </option>
                                ))}
                            </select>
                        </label>
                        <div className="class-form-actions">
                            <button className="class-primary-button" type="submit" disabled={saving}>
                                {saving ? "Saving…" : editingId == null ? "Create class" : "Save changes"}
                            </button>
                    
                        </div>
                    </form>

                </section>
            )}

            {error && <p className="class-error" role="alert">{error}</p>}

            <section className="class-list-card">
                <div className="class-list-heading">
                    <div>
                        <h2>Class list</h2>
                        <p>{visibleClasses.length} class{visibleClasses.length === 1 ? "" : "s"}</p>
                    </div>
                    <label className="class-search">
                        <Search size={16} />
                        <input
                            type="search"
                            placeholder="Search classes"
                            value={search}
                            onChange={(event) => setSearch(event.target.value)}
                            aria-label="Search classes or departments"
                        />
                    </label>
                </div>
                <div className="class-table-wrap">
                    <table className="class-table">
                        <thead>
                            <tr><th>ID</th><th>Class</th><th>Department</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="4" className="class-empty">Loading classes…</td></tr>
                            ) : visibleClasses.length === 0 ? (
                                <tr><td colSpan="4" className="class-empty">No classes found.</td></tr>
                            ) : visibleClasses.map((classEntry) => (
                                <tr key={classEntry.id}>
                                    <td><span className="class-id">{classEntry.id}</span></td>
                                    <td className="class-name-cell">{classEntry.name}</td>
                                    <td>{classEntry.department?.departmentName || "No department"}</td>
                                    <td>
                                        <div className="class-row-actions">
                                            <button type="button" onClick={() => openEditForm(classEntry)} aria-label={`Edit ${classEntry.name}`} title="Edit">
                                                <Pencil size={15} />
                                            </button>
                                            <button type="button" onClick={() => handleDelete(classEntry)} aria-label={`Delete ${classEntry.name}`} title="Delete">
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

export default ClassPage;
