import axiosClient from "./axiosClient";

const authApi = {
    login(credentials) {
        return axiosClient.post("/auth/login", credentials);
    },
    createUser(user) {
        return axiosClient.post("/auth/users", user);
    },
    listUsers() {
        return axiosClient.get("/auth/users");
    }
};

export default authApi;
