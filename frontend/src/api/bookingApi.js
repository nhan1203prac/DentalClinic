import { request } from './client'

// API đặt lịch của bệnh nhân (giai đoạn 5). Ngày dạng yyyy-MM-dd, giờ dạng HH:mm:ss
export const bookingApi = {
  // Người khám của tài khoản, kèm tiền sử
  patients: () => request({ method: 'GET', url: '/me/benh-nhan' }),
  // [{ maBacSi, hoTen, maLich, khung: [{ gio, trong }] }]
  freeSlots: (date) => request({ method: 'GET', url: '/dat-lich/khung-trong', params: { ngay: date } }),
  myAppointments: () => request({ method: 'GET', url: '/me/lich-hen' }),
  cancel: (id) => request({ method: 'PATCH', url: `/lich-hen/${id}/huy` }),
  book: (data) => request({ method: 'POST', url: '/lich-hen', data }),
}
