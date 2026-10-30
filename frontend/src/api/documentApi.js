import axiosClient from "./axiosClient";

const documentApi = {
    getAll() {
        return axiosClient.get("/documents");
    },
    upload(file) {
        const formData = new FormData();
        formData.append("file", file);
        // Remove the client's JSON default so the browser can add the multipart boundary.
        return axiosClient.post("/documents", formData, {
            headers: { "Content-Type": undefined }
        });
    },
    download(id) {
        return axiosClient.get(`/documents/${id}/download`, {
            responseType: "blob"
        });
    },
    delete(id) {
        return axiosClient.delete(`/documents/${id}`);
    }
};

export default documentApi;
