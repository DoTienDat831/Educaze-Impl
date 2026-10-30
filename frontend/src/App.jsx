import Student from "./components/Student";
import useStudents from "./hooks/useStudents";
import Sidebar from "./components/Sidebar";
import Department from "./components/Department";
import ClassPage from "./components/Class";
import Dashboard from "./components/Dashboard";
import Documents from "./components/Documents";
import { useEffect, useState } from "react";
import "./App.css";

function App() {
    const studentData = useStudents();
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
    const [activePage, setActivePage] = useState("dashboard");

    useEffect(() => {
        if (activePage === "students") studentData.loadDepartments();
    }, [activePage, studentData.loadDepartments]);

    return (
        <div className={`app-shell${sidebarCollapsed ? " is-collapsed" : ""}`}>
            <Sidebar
                collapsed={sidebarCollapsed}
                onToggle={() => setSidebarCollapsed((collapsed) => !collapsed)}
                activePage={activePage}
                onNavigate={setActivePage}
            />
            <main className="app-main">
                {activePage === "dashboard" ? (
                    <Dashboard />
                ) : activePage === "departments" ? (
                    <Department />
                ) : activePage === "classes" ? (
                    <ClassPage />
                ) : activePage === "documents" ? (
                    <Documents />
                ) : (
                    <Student className="student-management" {...studentData} />
                )}
            </main>
        </div>
    );
}

export default App;
