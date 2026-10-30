import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Download, FileText, HardDrive, Search, Trash2, UploadCloud } from "lucide-react";
import documentApi from "../api/documentApi";
import "./Documents.css";

const MAX_FILE_SIZE = 25 * 1024 * 1024;
const allowedExtensions = new Set(["pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "csv", "txt"]);

function formatSize(bytes = 0) {
    const size = Number(bytes);
    if (!Number.isFinite(size) || size <= 0) return "0 B";
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    return `${(size / (1024 * 1024)).toFixed(1)} MB`;
}

function getDocumentName(item) {
    const value = item?.name ?? item?.fileName ?? item?.filename ?? item?.originalFilename ?? item?.documentName;
    if (typeof value === "string" && value.trim()) return value.trim();
    const path = item?.filePath ?? item?.objectKey;
    return typeof path === "string" && path ? path.split(/[\\/]/).pop() : "Tài liệu không tên";
}

function getErrorMessage(error) {
    const data = error?.response?.data;
    if (data && typeof data === "object") {
        return data.detail || data.message || data.error || "Không thể xử lý tài liệu.";
    }
    return error?.message || "Không thể kết nối đến máy chủ.";
}

function Documents() {
    const [documents, setDocuments] = useState([]);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [uploading, setUploading] = useState(false);
    const [busyId, setBusyId] = useState(null);
    const [error, setError] = useState("");
    const [notice, setNotice] = useState("");
    const [isDragging, setIsDragging] = useState(false);
    const fileInputRef = useRef(null);

    const loadDocuments = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const { data } = await documentApi.getAll();
            if (!Array.isArray(data)) throw new Error("Dữ liệu tài liệu từ máy chủ không hợp lệ.");
            setDocuments(data);
        } catch (loadError) {
            setError(getErrorMessage(loadError));
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadDocuments();
    }, [loadDocuments]);

    const visibleDocuments = useMemo(() => {
        const query = search.trim().toLocaleLowerCase();
        return documents.filter((item) => getDocumentName(item).toLocaleLowerCase().includes(query));
    }, [documents, search]);

    const totalBytes = useMemo(
        () => documents.reduce((total, item) => total + (Number(item.size) || 0), 0),
        [documents]
    );

    async function uploadFile(file) {
        if (!file) return;

        const extension = file.name.split(".").pop()?.toLowerCase();
        if (!extension || !allowedExtensions.has(extension)) {
            setError("Chỉ hỗ trợ PDF, Word, PowerPoint, Excel, CSV và TXT.");
            setNotice("");
            return;
        }
        if (file.size > MAX_FILE_SIZE) {
            setError("Kích thước tối đa cho mỗi tệp là 25 MiB.");
            setNotice("");
            return;
        }

        setUploading(true);
        setError("");
        setNotice("");
        try {
            await documentApi.upload(file);
            setNotice(`Đã tải lên ${file.name}.`);
            await loadDocuments();
        } catch (uploadError) {
            setError(getErrorMessage(uploadError));
        } finally {
            setUploading(false);
        }
    }

    function handleFileChange(event) {
        const file = event.target.files?.[0];
        event.target.value = "";
        uploadFile(file);
    }

    function handleDrop(event) {
        event.preventDefault();
        setIsDragging(false);
        if (uploading) return;
        const file = event.dataTransfer.files?.[0];
        if (event.dataTransfer.files?.length > 1) {
            setError("Mỗi lần chỉ tải lên một tệp.");
            setNotice("");
            return;
        }
        uploadFile(file);
    }

    async function handleDownload(item) {
        if (!item.id) {
            setError("Tài liệu này thiếu thông tin tải xuống.");
            return;
        }

        setBusyId(item.id);
        setError("");
        setNotice("");
        let objectUrl;
        try {
            const { data } = await documentApi.download(item.id);
            objectUrl = URL.createObjectURL(data);
            const link = window.document.createElement("a");
            link.href = objectUrl;
            link.download = getDocumentName(item);
            link.style.display = "none";
            window.document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (downloadError) {
            setError(getErrorMessage(downloadError));
        } finally {
            if (objectUrl) window.setTimeout(() => URL.revokeObjectURL(objectUrl), 1000);
            setBusyId(null);
        }
    }

    async function handleDelete(item) {
        const name = getDocumentName(item);
        if (!window.confirm(`Xóa tài liệu “${name}”?`)) return;

        setBusyId(item.id);
        setError("");
        setNotice("");
        try {
            await documentApi.delete(item.id);
            setDocuments((current) => current.filter((document) => document.id !== item.id));
            setNotice(`Đã xóa ${name}.`);
        } catch (deleteError) {
            setError(getErrorMessage(deleteError));
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="documents-page">
            <header className="documents-header">
                <div>
                    <p className="documents-eyebrow">TÀI LIỆU</p>
                    <h1>Documents</h1>
                    <p className="documents-subtitle">Lưu trữ và quản lý tài liệu của trường.</p>
                </div>
                <button className="documents-upload-button" type="button" onClick={() => fileInputRef.current?.click()} disabled={uploading}>
                    <UploadCloud size={17} />
                    {uploading ? "Đang tải lên…" : "Tải tài liệu lên"}
                </button>
                <input
                    ref={fileInputRef}
                    className="documents-file-input"
                    type="file"
                    accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.csv,.txt"
                    onChange={handleFileChange}
                    aria-label="Chọn tài liệu để tải lên"
                />
            </header>

            <section className="documents-overview" aria-label="Tóm tắt kho tài liệu">
                <div className="documents-overview-icon"><HardDrive size={21} /></div>
                <div><span>Tài liệu đã lưu</span><strong>{documents.length}</strong></div>
                <div className="documents-overview-divider" />
                <div><span>Tổng dung lượng</span><strong>{formatSize(totalBytes)}</strong></div>
            </section>

            <section
                className={`documents-dropzone${isDragging ? " is-dragging" : ""}`}
                aria-label="Khu vực tải tài liệu lên"
                onDragEnter={(event) => { event.preventDefault(); setIsDragging(true); }}
                onDragOver={(event) => event.preventDefault()}
                onDragLeave={(event) => {
                    if (!event.currentTarget.contains(event.relatedTarget)) setIsDragging(false);
                }}
                onDrop={handleDrop}
            >
                <span className="documents-dropzone-icon"><UploadCloud size={22} /></span>
                <div>
                    <strong>{isDragging ? "Thả tệp vào đây" : "Kéo thả tài liệu vào vùng này"}</strong>
                    <p>PDF, Word, PowerPoint, Excel, CSV, TXT · Tối đa 25 MiB/tệp</p>
                </div>
                <button type="button" onClick={() => fileInputRef.current?.click()} disabled={uploading}>
                    {uploading ? "Đang tải lên…" : "Chọn tệp"}
                </button>
            </section>

            {error && <p className="documents-message is-error" role="alert">{error}</p>}
            {notice && <p className="documents-message is-success" role="status">{notice}</p>}

            <section className="documents-list-card">
                <div className="documents-list-heading">
                    <div>
                        <h2>Tài liệu của bạn</h2>
                        <p>{visibleDocuments.length} tài liệu</p>
                    </div>
                    <label className="documents-search">
                        <Search size={16} />
                        <input type="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Tìm tài liệu" aria-label="Tìm tài liệu" />
                    </label>
                </div>

                <div className="documents-table-wrap">
                    <table className="documents-table">
                        <thead><tr><th>Tên</th><th>Loại</th><th>Dung lượng</th><th>Thao tác</th></tr></thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="4" className="documents-empty">Đang tải danh sách tài liệu…</td></tr>
                            ) : visibleDocuments.length === 0 ? (
                                <tr><td colSpan="4" className="documents-empty">
                                    <FileText size={24} />
                                    <span>{search ? "Không tìm thấy tài liệu phù hợp." : "Chưa có tài liệu. Hãy tải tệp đầu tiên lên."}</span>
                                </td></tr>
                            ) : visibleDocuments.map((item) => (
                                <tr key={item.id}>
                                    <td><div className="documents-name"><span className="documents-file-icon"><FileText size={17} /></span><strong title={getDocumentName(item)}>{getDocumentName(item)}</strong></div></td>
                                    <td><span className="documents-type">{getDocumentName(item).split(".").pop()?.toUpperCase() || "FILE"}</span></td>
                                    <td>{formatSize(item.size)}</td>
                                    <td><div className="documents-actions">
                                        <button type="button" onClick={() => handleDownload(item)} disabled={busyId === item.id} title="Tải xuống" aria-label={`Tải xuống ${getDocumentName(item)}`}><Download size={16} /></button>
                                        <button className="is-delete" type="button" onClick={() => handleDelete(item)} disabled={busyId === item.id} title="Xóa" aria-label={`Xóa ${getDocumentName(item)}`}><Trash2 size={16} /></button>
                                    </div></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="documents-footnote">Định dạng hỗ trợ: PDF, DOC/DOCX, PPT/PPTX, XLS/XLSX, CSV, TXT. Tối đa 25 MiB mỗi tệp.</div>
            </section>
        </div>
    );
}

export default Documents;
