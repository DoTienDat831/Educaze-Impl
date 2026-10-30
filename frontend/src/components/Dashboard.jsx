import { Building2, GraduationCap, Users } from "lucide-react";
import useDashboard from "../hooks/useDashboard";
import "./Dashboard.css";

function Dashboard() {
    const { departmentStats, totalDepartments, totalStudents, loading, error } = useDashboard();

    return (
        <div className="dashboard-page">
            <header className="dashboard-header">
                <p className="dashboard-eyebrow">OVERVIEW</p>
                <h1>Dashboard</h1>
                <p className="dashboard-subtitle">Student enrollment by department</p>
            </header>

            <section className="dashboard-summary" aria-label="Student overview">
                <div className="dashboard-summary-card">
                    <span className="dashboard-summary-icon"><Users size={20} /></span>
                    <div><span>Total students</span><strong>{loading ? "—" : totalStudents}</strong></div>
                </div>
                <div className="dashboard-summary-card">
                    <span className="dashboard-summary-icon dashboard-summary-icon--blue"><Building2 size={20} /></span>
                    <div><span>Departments</span><strong>{loading ? "—" : totalDepartments}</strong></div>
                </div>
            </section>

            <section className="dashboard-class-section" aria-labelledby="dashboard-departments-heading">
                <div className="dashboard-section-heading">
                    <div>
                        <h2 id="dashboard-departments-heading">Students by department</h2>
                        <p>Student totals for each department</p>
                    </div>
                    <span className="dashboard-class-total"><GraduationCap size={16} /> {totalDepartments} departments</span>
                </div>
                {error && <p className="dashboard-error" role="status">{error}</p>}
                <div className="dashboard-class-grid">
                    {loading ? (
                        <p className="dashboard-empty">Loading dashboard…</p>
                    ) : departmentStats.length === 0 ? (
                        <p className="dashboard-empty">No departments available.</p>
                    ) : departmentStats.map((department) => (
                        <article className="dashboard-class-card" key={department.id}>
                            <div className="dashboard-class-card-top">
                                <span className="dashboard-class-icon"><Building2 size={18} /></span>
                                <span className="dashboard-student-count">{department.studentCount} {department.studentCount === 1 ? "student" : "students"}</span>
                            </div>
                            <h3>{department.name}</h3>
                            {department.code && <p className="dashboard-department-label">Code: {department.code}</p>}
                        </article>
                    ))}
                </div>
            </section>
        </div>
    );
}

export default Dashboard;
