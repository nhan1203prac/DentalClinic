import { bookingApi } from '../api/bookingApi'
import { createListSlice } from './listSlice'

// Người khám của tài khoản: tải lại sau mỗi lần đặt vì có thể vừa tạo hồ sơ mới
const patients = createListSlice('myPatients', () => bookingApi.patients())
export const fetchMyPatients = patients.fetchList
export const myPatientsReducer = patients.reducer

// Khung trống của một ngày; arg là yyyy-MM-dd
const slots = createListSlice('freeSlots', (date) => bookingApi.freeSlots(date))
export const fetchFreeSlots = slots.fetchList
export const freeSlotsReducer = slots.reducer

// Lịch khám của tài khoản (mọi người khám), mới nhất trước
const appointments = createListSlice('myAppointments', () => bookingApi.myAppointments())
export const fetchMyAppointments = appointments.fetchList
export const myAppointmentsReducer = appointments.reducer
