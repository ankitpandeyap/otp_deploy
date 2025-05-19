// src/api/axiosInstance.js
import axios from 'axios';
import { toast } from 'react-toastify';


const axiosInstance = axios.create({
  baseURL: 'http://localhost:8082/api',
  withCredentials: true,
});

// Add a request interceptor to attach the token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;

    }
    return config;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login'; // Force logout
    }
    return Promise.reject(error);
  }

);

axiosInstance.interceptors.response.use(
  (response) => response, // ✅ if successful, just return response
  async (error) => {
    const originalRequest = error.config;

    // If 401 and not already retried
    if (error.response && error.response.status === 401 && !originalRequest._retry && 
      originalRequest.url !== '/auth/login') {
      originalRequest._retry = true;

      try {
        // ⬅️ Try refreshing token
        const response = await axiosInstance.post('/auth/refresh',
          {},
          { withCredentials: true }
        );
        const token = response.headers.get('authorization') || response.headers['authorization'];
        const tokenValue = token.startsWith('Bearer ') ? token.split(' ')[1] : token;
        localStorage.setItem('accessToken', tokenValue);

        // Update header and retry original request
        originalRequest.headers['Authorization'] = `Bearer ${tokenValue}`;
        return axiosInstance(originalRequest); // 🔁 Retry the failed request
      } catch (refreshError) {
        toast.error('Refresh token expired or invalid.');
        localStorage.removeItem('accessToken'); // Clean up
        window.location.href = '/login';
        return Promise.reject(refreshError);   // Redirect to login
      }
    }

    return Promise.reject(error);
  }
);



export default axiosInstance;
