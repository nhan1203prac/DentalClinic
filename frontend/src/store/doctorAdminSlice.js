import { adminApi } from '../api/adminApi'
import { createListSlice } from './listSlice'

// Danh sách bác sĩ cho chủ phòng khám (khác doctorsSlice là dữ liệu công khai)
const { fetchList, reducer } = createListSlice('doctorAdmin', adminApi.doctors)
export const fetchDoctorsAdmin = fetchList
export default reducer
