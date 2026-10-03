import axios from 'axios';

const baseURL = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');

export const api = axios.create({ baseURL: `${baseURL}/api` });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('campusconnect_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status;
    const url = error.config?.url || '';
    if (status === 401 && !url.includes('/auth/login')) {
      localStorage.clear();
      if (window.location.pathname !== '/login') {
        window.location.replace('/login');
      }
    }
    return Promise.reject(error);
  }
);

export async function login(email, password) {
  const { data } = await api.post('/auth/login', { email, password });
  localStorage.setItem('campusconnect_token', data.token);
  localStorage.setItem('campusconnect_role', data.role);
  localStorage.setItem('campusconnect_name', data.name);
  return data;
}
