// Khớp enum LoaiTienSu của backend
export const HISTORY_TYPES = [
  { value: 'benh_nen', label: 'Bệnh nền' },
  { value: 'di_ung', label: 'Dị ứng' },
  { value: 'thuoc_dang_dung', label: 'Thuốc đang dùng' },
  { value: 'can_thiep_rang', label: 'Can thiệp răng miệng' },
]

export const historyLabel = (value) => HISTORY_TYPES.find((t) => t.value === value)?.label ?? value

// BR-25: backend giới hạn số ngày đặt trước (app.clinic.max-booking-days); đây chỉ để giới hạn ô chọn ngày,
// backend vẫn là nơi quyết định
export const MAX_BOOKING_DAYS = 14
