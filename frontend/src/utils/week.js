import dayjs from 'dayjs'

const DATE_FORMAT = 'YYYY-MM-DD'
export const DAY_LABELS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ nhật']

// Thứ Hai của tuần chứa ngày đã cho (backend yêu cầu tuần bắt đầu thứ Hai)
export function mondayOf(date = dayjs()) {
  const day = dayjs(date)
  return day.subtract((day.day() + 6) % 7, 'day').format(DATE_FORMAT)
}

export function shiftWeek(weekStart, weeks) {
  return dayjs(weekStart).add(weeks * 7, 'day').format(DATE_FORMAT)
}

export function weekEnd(weekStart) {
  return dayjs(weekStart).add(6, 'day').format(DATE_FORMAT)
}

// 7 ngày yyyy-MM-dd của tuần, bắt đầu từ thứ Hai
export function weekDates(weekStart) {
  return Array.from({ length: 7 }, (_, i) => dayjs(weekStart).add(i, 'day').format(DATE_FORMAT))
}

// Backend trả giờ dạng 08:00:00; hiển thị 08:00
export function shortTime(time) {
  return time.slice(0, 5)
}
