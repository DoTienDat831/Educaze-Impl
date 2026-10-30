import axios from "axios";
const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    }
});

axiosClient.interceptors.request.use((config) => {
    const token = window.sessionStorage.getItem("educaze.accessToken");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

axiosClient.interceptors.response.use((response) => response, (error) => {
    if (error.response?.status === 401) {
        window.sessionStorage.removeItem("educaze.accessToken");
        window.sessionStorage.removeItem("educaze.username");
        window.sessionStorage.removeItem("educaze.role");
        window.dispatchEvent(new Event("educaze:unauthorized"));
    }
    return Promise.reject(error);
});

export default axiosClient;
