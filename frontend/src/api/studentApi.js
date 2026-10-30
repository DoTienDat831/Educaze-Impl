import axiosClient from "./axiosClient";

const studentApi = {
    getAll() {
        return axiosClient.get("/students");
    },

    getById(id) {
        return axiosClient.get(`/students/${id}`);
    },

    create(student) {
        return axiosClient.post("/students", student);
    },

    update(id, student) {
        return axiosClient.put(`/students/${id}`, student);
    },

    delete(id) {
        return axiosClient.delete(`/students/${id}`);
    }
};

export default studentApi;