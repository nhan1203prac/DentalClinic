import { adminApi } from '../api/adminApi'
import { createListSlice } from './listSlice'

// Danh sách dịch vụ đầy đủ (kể cả đã ngưng) cho nhân viên và chủ phòng khám
const { fetchList, reducer } = createListSlice('serviceAdmin', adminApi.services)
export const fetchServicesAdmin = fetchList
export default reducer
