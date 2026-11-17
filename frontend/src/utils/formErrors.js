import { ApiError } from '../api/client'

// Gán lỗi validate của backend (code VALIDATION_ERROR) vào từng ô của form; trả true nếu đã xử lý
export function applyFieldErrors(form, error) {
  if (error instanceof ApiError && error.fields) {
    form.setFields(Object.entries(error.fields).map(([name, message]) => ({ name, errors: [message] })))
    return true
  }
  return false
}

export function errorMessage(error) {
  return error instanceof Error ? error.message : 'Có lỗi xảy ra, vui lòng thử lại'
}
