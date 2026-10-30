import { useState } from "react";
import { Bell, Mail, X, Pencil, Trash2, Search } from "lucide-react";

function StudentFields({ form, departments, onChange }) {
    return (
        <>
            <input
                type="text"
                name="name"
                placeholder="Name"
                value={form.name}
                onChange={onChange}
                required
            />
            <input
                type="date"
                name="dateOfBirth"
                aria-label="Date of birth"
                value={form.dateOfBirth || ""}
                onChange={onChange}
            />
            <select
                name="gender"
                aria-label="Gender"
                value={form.gender || ""}
                onChange={onChange}
            >
                <option value="">-- Select Gender --</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
            </select>
            <input
                type="date"
                name="enrollmentTime"
                aria-label="Enrollment date"
                value={form.enrollmentTime || ""}
                onChange={onChange}
            />
            <input
                type="email"
                name="email"
                placeholder="Email"
                value={form.email || ""}
                onChange={onChange}
            />
            <select
                name="departmentId"
                value={form.departmentId}
                onChange={onChange}
                required
            >
                <option value="">-- Select Department --</option>
                {departments.map((department) => (
                    <option key={department.id} value={department.id}>
                        {department.departmentName}
                    </option>
                ))}
            </select>
        </>
    );
}

export default function Student({
    students,
    departments,
    departmentError,
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
}) {
    const editingStudent = students.find((student) => student.id === editingId);
    const [exportError, setExportError] = useState("");

    const handleExport = async () => {
        setExportError("");
        try {
            const { default: ExcelJS } = await import("exceljs");
            const workbook = new ExcelJS.Workbook();
            const worksheet = workbook.addWorksheet("Students");

            worksheet.columns = [
                { header: "Student ID", key: "id", width: 12 },
                { header: "Student code", key: "studentCode", width: 16 },
                { header: "Name", key: "name", width: 26 },
                { header: "Date of birth", key: "dateOfBirth", width: 16 },
                { header: "Gender", key: "gender", width: 12 },
                { header: "Enrollment date", key: "enrollmentTime", width: 18 },
                { header: "Email", key: "email", width: 30 },
                { header: "Department", key: "department", width: 24 }
            ];
            
            worksheet.addRows(students.map((student) => ({
                id: student.id,
                studentCode: student.studentCode || "",
                name: student.name || "",
                dateOfBirth: student.dateOfBirth || "",
                gender: student.gender || "",
                enrollmentTime: student.enrollmentTime || "",
                email: student.email || "",
                department: student.department?.departmentName || student.studentClass?.department?.departmentName || ""
            })));

            worksheet.getRow(1).font = { bold: true, color: { argb: "FF1B2740" } };
            worksheet.getRow(1).fill = {
                type: "pattern",
                pattern: "solid",
                fgColor: { argb: "FFE5F7EF" }
            };
            worksheet.autoFilter = { from: "A1", to: "H1" };
            worksheet.views = [{ state: "frozen", ySplit: 1 }];

            const buffer = await workbook.xlsx.writeBuffer();
            const blob = new Blob([buffer], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            });
            const downloadUrl = URL.createObjectURL(blob);
            const link = document.createElement("a");
            const now = new Date();
            const date = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
            link.href = downloadUrl;
            link.download = `students-${date}.xlsx`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            URL.revokeObjectURL(downloadUrl);
        } catch {
            setExportError("Không thể tạo file Excel. Vui lòng thử lại.");
        }
    };

    return (
        <>

            <div className="student-container">
                <h1>Student Management</h1>
            </div>

            <section className="student-form-section" aria-labelledby="add-student-heading">
                <h2 id="add-student-heading">Add student</h2>
                {departmentError && <p className="form-error" role="alert">{departmentError}</p>}
                <form onSubmit={handleAddSubmit} className="student-form add-student-form">
                    <StudentFields form={addForm} departments={departments} onChange={handleAddChange} />
                    <button type="submit" className="add-btn">Add student</button>
                </form>
                {addError && <p className="form-error" role="alert">{addError}</p>}
            </section>

            {editingId != null && (
                <section className="student-form-section edit-student-section" aria-labelledby="edit-student-heading">
                    <div className="edit-student-heading-row">
                        <div>
                            <h2 id="edit-student-heading">Update student</h2>
                            <p>{editingStudent?.studentCode || `Student #${editingId}`}</p>
                        </div>
                        <button
                            type="button"
                            className="cancel-edit-icon"
                            aria-label="Close update form"
                            onClick={handleCancelEdit}
                        >
                            <X size={18} />
                        </button>
                    </div>
                    <form onSubmit={handleUpdateSubmit} className="student-form edit-student-form">
                        <StudentFields form={editForm} departments={departments} onChange={handleEditChange} />
                        <div className="edit-form-actions">
                            <button type="submit" className="update-btn">Save changes</button>
                            <button type="button" className="cancel-edit-btn" onClick={handleCancelEdit}>Cancel</button>
                        </div>
                    </form>
                    {editError && <p className="form-error" role="alert">{editError}</p>}
                </section>
            )}

            {error && <p className="form-error" role="alert">{error}</p>}

            <section className="student-list-card">
                <div className="student-list-heading">
                    <div>
                        <h2>Student list</h2>
                        <p>{students.length} student{students.length === 1 ? "" : "s"}</p>
                    </div>
                    <div className="student-list-tools">
                        <label className="student-search">
                            <Search size={16} />
                            <input
                                type="search"
                                placeholder="Search students"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                aria-label="Search by ID or student code"
                            />
                        </label>
                        <button
                            className="student-export-button"
                            type="button"
                            onClick={handleExport}
                            disabled={students.length === 0}
                            title={students.length === 0 ? "No students to export" : "Export visible students to Excel"}
                        >
                            <span aria-hidden="true">↓</span>
                            Export XLSX
                        </button>
                    </div>
                </div>
                {exportError && <p className="student-export-error" role="alert">{exportError}</p>}
                <div className="student-table-wrap">
                    <table className="student-table">
                        <thead>
                            <tr>
                                <th>Student ID</th>
                                <th>Name</th>
                                <th>Birth</th>
                                <th>Gender</th>
                                <th>Enrollment</th>
                                <th>Email</th>
                                <th>Department</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {students.length === 0 ? (
                                <tr><td colSpan="8" className="student-empty">No students found.</td></tr>
                            ) : students.map((student) => (
                                <tr key={student.id}>
                                    <td><span className="student-code">{student.studentCode}</span></td>
                                    <td className="student-name-cell">{student.name}</td>
                                    <td>{student.dateOfBirth || "—"}</td>
                                    <td>{student.gender || "—"}</td>
                                    <td>{student.enrollmentTime || "—"}</td>
                                    <td>{student.email || "—"}</td>
                                    <td>{student.department?.departmentName || student.studentClass?.department?.departmentName || "—"}</td>
                                    <td>
                                        <div className="student-row-actions">
                                            <button type="button" onClick={() => handleEdit(student)} aria-label={`Edit ${student.name}`} title="Edit"><Pencil size={15} /></button>
                                            <button type="button" onClick={() => handleDelete(student.id)} aria-label={`Delete ${student.name}`} title="Delete"><Trash2 size={15} /></button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </>
    );
}
