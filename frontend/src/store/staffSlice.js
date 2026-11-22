import { adminApi } from '../api/adminApi'
import { createListSlice } from './listSlice'

const { fetchList, reducer } = createListSlice('staff', adminApi.staff)
export const fetchStaff = fetchList
export default reducer
