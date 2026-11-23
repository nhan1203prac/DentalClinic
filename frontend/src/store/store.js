import { configureStore } from '@reduxjs/toolkit'
import { setSessionLostHandler } from '../api/client'
import authReducer, { loggedOut } from './authSlice'
import { freeSlotsReducer, myAppointmentsReducer, myPatientsReducer } from './bookingSlice'
import doctorAdminReducer from './doctorAdminSlice'
import doctorsReducer from './doctorsSlice'
import scheduleReducer from './scheduleSlice'
import serviceAdminReducer from './serviceAdminSlice'
import servicesReducer from './servicesSlice'
import staffReducer from './staffSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    doctors: doctorsReducer,
    services: servicesReducer,
    staff: staffReducer,
    doctorAdmin: doctorAdminReducer,
    serviceAdmin: serviceAdminReducer,
    schedule: scheduleReducer,
    myPatients: myPatientsReducer,
    freeSlots: freeSlotsReducer,
    myAppointments: myAppointmentsReducer,
  },
})

// Refresh thất bại giữa chừng: xóa user, ProtectedRoute sẽ đưa về trang đăng nhập
setSessionLostHandler(() => store.dispatch(loggedOut()))
