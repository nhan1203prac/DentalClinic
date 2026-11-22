import { scheduleApi } from '../api/scheduleApi'
import { weekEnd } from '../utils/week'
import { createListSlice } from './listSlice'

// Lịch của tuần đang xem; arg là yyyy-MM-dd của thứ Hai
const { fetchList, reducer } = createListSlice('schedule', (weekStart) => scheduleApi.list(weekStart, weekEnd(weekStart)))
export const fetchSchedule = fetchList
export default reducer
