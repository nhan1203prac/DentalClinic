import { request } from './client'

export const publicApi = {
  doctors: () => request({ method: 'GET', url: '/public/bac-si' }),
  doctor: (id) => request({ method: 'GET', url: `/public/bac-si/${id}` }),
  services: () => request({ method: 'GET', url: '/public/dich-vu' }),
}
