import { request } from './client'

// API quản trị danh mục (giai đoạn 3): nhân viên, bác sĩ, tài khoản, dịch vụ
export const adminApi = {
  staff: () => request({ method: 'GET', url: '/admin/nhan-vien' }),
  createStaff: (data) => request({ method: 'POST', url: '/admin/nhan-vien', data }),
  updateStaff: (id, data) => request({ method: 'PUT', url: `/admin/nhan-vien/${id}`, data }),

  doctors: () => request({ method: 'GET', url: '/admin/bac-si' }),
  createDoctor: (data) => request({ method: 'POST', url: '/admin/bac-si', data }),
  updateDoctor: (id, data) => request({ method: 'PUT', url: `/admin/bac-si/${id}`, data }),

  lockAccount: (accountId, lock) =>
    request({ method: 'PATCH', url: `/admin/tai-khoan/${accountId}/khoa`, data: { khoa: lock } }),

  services: () => request({ method: 'GET', url: '/dich-vu' }),
  createService: (data) => request({ method: 'POST', url: '/dich-vu', data }),
  updateService: (id, data) => request({ method: 'PUT', url: `/dich-vu/${id}`, data }),
}
