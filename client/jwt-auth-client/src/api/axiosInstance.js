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

// Define paths where automatic redirect to /login should be suppressed
// if a token refresh attempt ultimately fails.
// Ensure these paths match your actual react-router-dom routes.
const NO_REDIRECT_ON_REFRESH_FAILURE_PATHS = ['/login', '/register'];
// If you have a separate route like '/verify-otp', add it to the list:
// const NO_REDIRECT_ON_REFRESH_FAILURE_PATHS = ['/login', '/register', '/verify-otp'];

axiosInstance.interceptors.response.use(
  (response) => response, // If successful, just return the response
  async (error) => {
    const originalRequest = error.config;

    // Check if the error is a 401 Unauthorized,
    // and it's not a request that has already been retried,
    // and it's not the login or token refresh API endpoint itself.
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry &&
      originalRequest.url !== '/auth/login' &&
      originalRequest.url !== '/auth/refresh'
    ) {
      originalRequest._retry = true; // Mark that we're attempting a retry for this request

      try {
        // Attempt to refresh the token
        const refreshApiResponse = await axiosInstance.post('/auth/refresh', {}, { withCredentials: true });
        const authorizationHeader = refreshApiResponse.headers['authorization'];

        if (authorizationHeader) {
          // New token successfully received
          const tokenValue = authorizationHeader.startsWith('Bearer ')
            ? authorizationHeader.split(' ')[1]
            : authorizationHeader;

          localStorage.setItem('accessToken', tokenValue);
          // Update the default Authorization header for subsequent requests
          axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${tokenValue}`;
          // Update the Authorization header for the original request being retried
          originalRequest.headers['Authorization'] = `Bearer ${tokenValue}`;

          return axiosInstance(originalRequest); // Retry the original request with the new token
        } else {
          // Refresh API responded with success (e.g., 200 OK) but no new token was in the header
          toast.error('Session update failed (no new token). Please log in again.');
          localStorage.removeItem('accessToken');
          delete axiosInstance.defaults.headers.common['Authorization']; // Clear stale default token

          // Conditional redirect: Only redirect if not on specified pages
          if (!NO_REDIRECT_ON_REFRESH_FAILURE_PATHS.includes(window.location.pathname)) {
            window.location.href = '/login';
          }
          return Promise.reject(new Error('Token refresh successful but no new token received.'));
        }
      } catch (refreshError) {
        // Token refresh attempt failed (e.g., refresh token is invalid, expired, or a network error occurred)
        let errorMessage = 'Your session has expired. Please log in again.';
        if (refreshError.response && refreshError.response.data && refreshError.response.data.message) {
          errorMessage = refreshError.response.data.message; // Use server's error message if available
        }
        toast.error(errorMessage);
        localStorage.removeItem('accessToken');
        delete axiosInstance.defaults.headers.common['Authorization']; // Clear stale default token

        // Conditional redirect: Only redirect if not on specified pages
        if (!NO_REDIRECT_ON_REFRESH_FAILURE_PATHS.includes(window.location.pathname)) {
          window.location.href = '/login';
        }
        return Promise.reject(refreshError); // Propagate the error so the calling code can also handle it
      }
    }

    // For errors not handled by the refresh logic (e.g., non-401 errors,
    // or 401s on login/refresh URLs, or if the request was already retried)
    return Promise.reject(error);
  }
);


export default axiosInstance;
