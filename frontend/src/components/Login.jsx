import { useState } from "react";
import authApi from "../api/authApi";
import "./Login.css";

const portals = [
    { role: "ADMIN", title: "ADMIN", description: "Quản trị hệ thống", mark: "A" },
    { role: "TEACHER", title: "TEACHER", description: "Dành cho giáo viên", mark: "T" },
    { role: "STUDENT", title: "STUDENT", description: "Dành cho học sinh", mark: "S" },
];

export default function Login({ onLogin }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [selectedRole, setSelectedRole] = useState("ADMIN");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function selectPortal(role) {
        setSelectedRole(role);
        setError("");
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);
        try {
            const { data } = await authApi.login({ username, password });
            if (data.role !== selectedRole) {
                setError("Tài khoản không thuộc cổng đăng nhập đã chọn. Vui lòng chọn đúng vai trò.");
                return;
            }
            onLogin(data);
        } catch {
            setError("Tên đăng nhập hoặc mật khẩu không đúng.");
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <main className="login-page">
            <form className="login-card" onSubmit={handleSubmit}>
                <span className="login-brand-mark" aria-hidden="true">E</span>
                <p className="login-eyebrow">EDUCAZE</p>
                <h1>Đăng nhập</h1>

                <div className="login-portals" role="group" aria-label="Chọn cổng đăng nhập">
                    {portals.map((portal) => (
                        <button
                            className={`login-portal${selectedRole === portal.role ? " is-selected" : ""}`}
                            type="button"
                            key={portal.role}
                            aria-pressed={selectedRole === portal.role}
                            onClick={() => selectPortal(portal.role)}
                            disabled={submitting}
                        >
                            <span className="login-portal-mark" aria-hidden="true">{portal.mark}</span>
                            <span className="login-portal-title">{portal.title}</span>
                            {/*<span className="login-portal-description">{portal.description}</span> */}
                        </button>
                    ))}
                </div>

                <label>Tên đăng nhập
                    <input autoComplete="username" value={username} onChange={(event) => setUsername(event.target.value)} required />
                </label>
                <label>Mật khẩu
                    <input type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required />
                </label>
                {error && <p className="login-error" role="alert">{error}</p>}
                <button className="login-submit" type="submit" disabled={submitting}>
                    {submitting ? "Đang đăng nhập…" : `Đăng nhập ${selectedRole}`}
                </button>
            </form>
        </main>
    );
}
