import { request } from './client'

// API lịch làm việc (giai đoạn 4). Ngày dạng yyyy-MM-dd, giờ dạng HH:mm:ss
export const scheduleApi = {
  // Chủ phòng khám thấy mọi bác sĩ; bác sĩ chỉ thấy lịch của mình (backend tự lọc)
  list: (fromDate, toDate, doctorId) =>
    request({ method: 'GET', url: '/lich-lam-viec', params: { tuNgay: fromDate, denNgay: toDate, maBacSi: doctorId } }),
  // luu: các ô thêm hoặc sửa, xoa: các ô xóa; trả về lịch cả tuần
  saveWeek: (weekStart, save = [], remove = []) =>
    request({ method: 'PUT', url: '/lich-lam-viec/tuan', data: { tuNgay: weekStart, luu: save, xoa: remove } }),
  copyPreviousWeek: (weekStart) =>
    request({ method: 'POST', url: '/lich-lam-viec/sao-chep-tuan', data: { tuNgay: weekStart } }),
  // Đổi buổi đã có lịch hẹn: trả { lich, lichHenBiAnhHuong }
  update: (scheduleId, data) => request({ method: 'PATCH', url: `/lich-lam-viec/${scheduleId}`, data }),
}
