import { useState } from "react";
import { UserPlus } from "lucide-react";
import authApi from "../api/authApi";
import "./AccountManagement.css";

const initialForm = { username: "", password: "", confirmPassword: "", role: "TEACHER" };

function getErrorMessage(error) {
    const status = error.response?.status;
    if (status === 409) return "Tên đăng nhập đã được sử dụng. Hãy chọn tên khác.";
    if (status === 400) return error.response?.data?.message || "Thông tin chưa hợp lệ. Kiểm tra tên đăng nhập và mật khẩu.";
    if (!error.response) return "Không thể kết nối backend. Kiểm tra backend rồi thử lại.";
    return "Không tạo được tài khoản. Vui lòng thử lại.";
}

export default function AccountManagement() {
    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function updateField(event) {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
        setError("");
        setNotice("");
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setNotice("");
        if (form.password !== form.confirmPassword) {
            setError("Mật khẩu xác nhận không khớp.");
            return;
        }
        setSubmitting(true);
        try {
            const { data } = await authApi.createUser({
                username: form.username.trim(),
                password: form.password,
                role: form.role,
            });
            setNotice(`Đã tạo tài khoản ${data.username} với vai trò ${data.role}.`);
            setForm(initialForm);
        } catch (requestError) {
            setError(getErrorMessage(requestError));
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <section className="account-page">
            <header className="account-header">
                <div>
                    <p className="account-eyebrow">QUẢN TRỊ HỆ THỐNG</p>
                    <h1>Tạo tài khoản</h1>
                    <p>Tạo thông tin đăng nhập cho giáo viên hoặc học sinh.</p>
                </div>
                <span className="account-header-icon" aria-hidden="true"><UserPlus size={22} /></span>
            </header>

            <form className="account-form" onSubmit={handleSubmit}>
                <label>Vai trò
                    <select name="role" value={form.role} onChange={updateField} disabled={submitting}>
                        <option value="TEACHER">TEACHER — Giáo viên</option>
                        <option value="STUDENT">STUDENT — Học sinh</option>
                    </select>
                </label>
                <label>Tên đăng nhập
                    <input name="username" autoComplete="username" minLength={3} maxLength={100} value={form.username} onChange={updateField} required disabled={submitting} />
                </label>
                <label>Mật khẩu
                    <input name="password" type="password" autoComplete="new-password" minLength={12} maxLength={72} value={form.password} onChange={updateField} required disabled={submitting} />
                    <small>Mật khẩu cần có từ 12 đến 72 ký tự.</small>
                </label>
                <label>Xác nhận mật khẩu
                    <input name="confirmPassword" type="password" autoComplete="new-password" minLength={12} maxLength={72} value={form.confirmPassword} onChange={updateField} required disabled={submitting} />
                </label>
                {error && <p className="account-message is-error" role="alert">{error}</p>}
                {notice && <p className="account-message is-success" role="status">{notice}</p>}
                <button type="submit" disabled={submitting}>{submitting ? "Đang tạo…" : "Tạo tài khoản"}</button>
            </form>
        </section>
    );
}
