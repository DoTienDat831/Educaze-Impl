import axiosClient from "./axiosClient";

const departmentApi = {
    getAll() {
        return axiosClient.get("/departments");
    },

    getById(id) {
        return axiosClient.get(`/departments/${id}`);
    },

    create(department) {
        return axiosClient.post("/departments", department);
    },

    update(id, department) {
        return axiosClient.put(`/departments/${id}`, department);
    },

    delete(id) {
        return axiosClient.delete(`/departments/${id}`);
    }
};

export default departmentApi;