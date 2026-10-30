import Student from "./components/Student";
import useStudents from "./hooks/useStudents";
import Sidebar from "./components/Sidebar";
import Department from "./components/Department";
import ClassPage from "./components/Class";
import Dashboard from "./components/Dashboard";
import Documents from "./components/Documents";
import Login from "./components/Login";
import { useEffect, useState } from "react";
import "./App.css";

function loadSession() {
    const token = window.sessionStorage.getItem("educaze.accessToken");
    const role = window.sessionStorage.getItem("educaze.role");
    const username = window.sessionStorage.getItem("educaze.username");
    if (!token || !role || !username) return null;
    try {
        const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
        if (payload.exp * 1000 <= Date.now()) return null;
    } catch {
        return null;
    }
    return { token, role, username };
}

function Workspace({ session, onLogout }) {
    const studentData = useStudents();
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
    const [activePage, setActivePage] = useState("dashboard");
    const isAdmin = session.role === "ADMIN";
    const canUploadDocuments = isAdmin || session.role === "TEACHER";

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
                role={session.role}
                username={session.username}
                onLogout={onLogout}
            />
            <main className="app-main">
                {activePage === "dashboard" ? (
                    <Dashboard />
                ) : activePage === "departments" ? (
                    <Department canManage={isAdmin} />
                ) : activePage === "classes" ? (
                    <ClassPage canManage={isAdmin} />
                ) : activePage === "documents" ? (
                    <Documents canUpload={canUploadDocuments} canDelete={isAdmin} />
                ) : (
                    <Student className="student-management" {...studentData} canManage={isAdmin} canExport={isAdmin} />
                )}
            </main>
        </div>
    );
}

function App() {
    const [session, setSession] = useState(loadSession);

    useEffect(() => {
        const clearSession = () => setSession(null);
        window.addEventListener("educaze:unauthorized", clearSession);
        return () => window.removeEventListener("educaze:unauthorized", clearSession);
    }, []);

    const handleLogin = (data) => {
        window.sessionStorage.setItem("educaze.accessToken", data.accessToken);
        window.sessionStorage.setItem("educaze.username", data.username);
        window.sessionStorage.setItem("educaze.role", data.role);
        setSession({ token: data.accessToken, username: data.username, role: data.role });
    };

    const handleLogout = () => {
        window.sessionStorage.removeItem("educaze.accessToken");
        window.sessionStorage.removeItem("educaze.username");
        window.sessionStorage.removeItem("educaze.role");
        setSession(null);
    };

    return session
        ? <Workspace session={session} onLogout={handleLogout} />
        : <Login onLogin={handleLogin} />;
}

export default App;
