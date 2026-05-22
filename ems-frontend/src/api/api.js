import axios from 'axios';

const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

// Automatically add JWT token to every request
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// If token expired → redirect to login
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403 || error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth
export const login = (data) => API.post('/api/auth/login', data);
export const register = (data) => API.post('/api/auth/register', data);
export const changePassword = (data) => API.put('/api/auth/change-password', data);

// Employees
export const getEmployees = () => API.get('/api/employees');
export const getEmployee = (id) => API.get(`/api/employees/${id}`);
export const createEmployee = (data) => API.post('/api/employees', data);
export const updateEmployee = (id, data) => API.put(`/api/employees/${id}`, data);
export const deleteEmployee = (id) => API.delete(`/api/employees/${id}`);

// Work Logs
export const getWorkLogs = () => API.get('/api/worklogs');
export const getWorkLogsByEmployee = (id) => API.get(`/api/worklogs/employee/${id}`);
export const createWorkLog = (data) => API.post('/api/worklogs', data);
export const updateWorkLog = (id, data) => API.put(`/api/worklogs/${id}`, data);
export const deleteWorkLog = (id) => API.delete(`/api/worklogs/${id}`);
export const filterWorkLogs = (params) => API.get('/api/worklogs/filter', { params });

// Reports
export const getEmployeeReport = (id) => API.get(`/api/reports/employee/${id}`);
export const getAllReports = () => API.get('/api/reports/employees');

// Export
export const exportAllCSV = () =>
  API.get('/api/export/worklogs', { responseType: 'blob' });
export const exportEmployeeCSV = (id) =>
  API.get(`/api/export/worklogs/employee/${id}`, { responseType: 'blob' });

// Notifications
export const getUnreadNotifications = () => API.get('/api/notifications');
export const markNotificationAsRead = (id) => API.put(`/api/notifications/${id}/read`);
