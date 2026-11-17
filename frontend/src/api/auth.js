import { request } from './client'

export const authApi = {
  login: (body) => request({ method: 'POST', url: '/auth/login', data: body }),
  register: (body) => request({ method: 'POST', url: '/auth/register', data: body }),
  logout: () => request({ method: 'POST', url: '/auth/logout' }),
  me: () => request({ method: 'GET', url: '/auth/me' }),
}
