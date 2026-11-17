import { createSlice } from '@reduxjs/toolkit'
import { authApi } from '../api/auth'
import { refreshSession, tokenStore } from '../api/client'

const authSlice = createSlice({
  name: 'auth',
  // initializing = true cho tới khi thử khôi phục phiên bằng refresh token xong
  initialState: { user: null, initializing: true },
  reducers: {
    sessionRestored(state, action) {
      state.user = action.payload
      state.initializing = false
    },
    loggedIn(state, action) {
      state.user = action.payload
    },
    loggedOut(state) {
      state.user = null
    },
  },
})

export const { sessionRestored, loggedIn, loggedOut } = authSlice.actions
export default authSlice.reducer

export const selectUser = (state) => state.auth.user
export const selectInitializing = (state) => state.auth.initializing

// Dùng thunk thường thay vì createAsyncThunk để lỗi ApiError giữ nguyên code và fields cho form

// Mở trang: dùng cookie refresh_token để khôi phục phiên, không có thì coi là khách
export const restoreSession = () => async (dispatch) => {
  try {
    const token = await refreshSession()
    dispatch(sessionRestored(token.taiKhoan))
  } catch {
    dispatch(sessionRestored(null))
  }
}

export const login = (body) => async (dispatch) => {
  const token = await authApi.login(body)
  tokenStore.set(token.accessToken)
  dispatch(loggedIn(token.taiKhoan))
  return token.taiKhoan
}

export const logout = () => async (dispatch) => {
  try {
    await authApi.logout()
  } finally {
    tokenStore.set(null)
    dispatch(loggedOut())
  }
}
