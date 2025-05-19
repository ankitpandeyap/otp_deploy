// src/api/axiosInstance.js
import axios from 'axios';
import { toast } from 'react-toastify';


const axiosInstance = axios.create({
  baseURL: 'https://otp-deploy.onrender.com/api',
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

    // If 401, not already retried, and the failing request is NOT the login or refresh token URL
    if (error.response && error.response.status === 401 && !originalRequest._retry &&
        originalRequest.url !== '/auth/login' &&
        originalRequest.url !== '/auth/refresh') { // Crucial: Don't retry if refresh itself failed with 401
      originalRequest._retry = true;

      try {
        // ⬅️ Try refreshing token
        const refreshApiResponse = await axiosInstance.post('/auth/refresh',
          {},
          { withCredentials: true }
        );

        const authorizationHeader = refreshApiResponse.headers['authorization'];

        if (authorizationHeader) {
          const tokenValue = authorizationHeader.startsWith('Bearer ') ? authorizationHeader.split(' ')[1] : authorizationHeader;
          localStorage.setItem('accessToken', tokenValue);

          // Update default Authorization header for subsequent requests
          axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${tokenValue}`;
          // Update Authorization header for the original request being retried
          originalRequest.headers['Authorization'] = `Bearer ${tokenValue}`;

          return axiosInstance(originalRequest); // 🔁 Retry the failed request
        } else {
          // Refresh API responded 2xx but didn't provide a new token.
          toast.error('Token refresh successful but no new token received. Please log in again.');
          localStorage.removeItem('accessToken');
          // Redirect to login. Consider using useNavigate if available in this context.
          window.location.href = '/login';
          return Promise.reject(new Error('Token refresh successful but no new token received in headers.'));
        }
      } catch (refreshError) {
        // This catch block handles errors from the /auth/refresh call itself
        // (e.g., network error, or server responds with 4xx/5xx for the refresh call, including another 401)
        let errorMessage = 'Your session has expired. Please log in again.';
        if (refreshError.response && refreshError.response.data && refreshError.response.data.message) {
            errorMessage = refreshError.response.data.message;
        } else if (refreshError.message) {
            // errorMessage = refreshError.message; // Can be too technical
        }
        toast.error(errorMessage);
        localStorage.removeItem('accessToken'); // Clean up
        window.location.href = '/login';       // Redirect to login
        return Promise.reject(refreshError);
      }
    }

    // For errors other than 401, or if it's a 401 for /auth/login or /auth/refresh,
    // or if the request has already been retried, just reject the promise.
    return Promise.reject(error);
  }
);


export default axiosInstance;
