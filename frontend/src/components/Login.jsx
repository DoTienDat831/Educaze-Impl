import { useState } from "react";
import authApi from "../api/authApi";
import "./Login.css";

export default function Login({ onLogin }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);
        try {
            const { data } = await authApi.login({ username, password });
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
                <span className="login-brand-mark">E</span>
                <p className="login-eyebrow">EDUCAZE</p>
                <h1>Đăng nhập</h1>
                <p className="login-subtitle">Đăng nhập để tiếp tục vào hệ thống quản lý.</p>
                <label>Tên đăng nhập
                    <input autoComplete="username" value={username} onChange={(event) => setUsername(event.target.value)} required />
                </label>
                <label>Mật khẩu
                    <input type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required />
                </label>
                {error && <p className="login-error" role="alert">{error}</p>}
                <button type="submit" disabled={submitting}>{submitting ? "Đang đăng nhập…" : "Đăng nhập"}</button>
            </form>
        </main>
    );
}
