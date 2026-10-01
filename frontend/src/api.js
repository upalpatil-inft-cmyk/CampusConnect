import axios from 'axios';

export const api = axios.create({ baseURL: 'http://localhost:8080/api' });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('campusconnect_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export async function login(email, password) {
  const { data } = await api.post('/auth/login', { email, password });
  localStorage.setItem('campusconnect_token', data.token);
  localStorage.setItem('campusconnect_role', data.role);
  localStorage.setItem('campusconnect_name', data.name);
  return data;
}
