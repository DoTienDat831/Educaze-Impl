import axiosClient from "./axiosClient";

const classApi = {
    getAll() {
        return axiosClient.get("/classes");
    },

    getById(id) {
        return axiosClient.get(`/classes/${id}`);
    },

    create(studentClass) {
        return axiosClient.post("/classes", studentClass);
    },

    update(id, studentClass) {
        return axiosClient.put(`/classes/${id}`, studentClass);
    },

    delete(id) {
        return axiosClient.delete(`/classes/${id}`);
    }
};

export default classApi;