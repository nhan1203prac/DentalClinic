import { configureStore } from '@reduxjs/toolkit'
import { setSessionLostHandler } from '../api/client'
import authReducer, { loggedOut } from './authSlice'
import doctorsReducer from './doctorsSlice'
import servicesReducer from './servicesSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    doctors: doctorsReducer,
    services: servicesReducer,
  },
})

// Refresh thất bại giữa chừng: xóa user, ProtectedRoute sẽ đưa về trang đăng nhập
setSessionLostHandler(() => store.dispatch(loggedOut()))
