import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { loggedOut } from './authSlice'

// Slice dùng chung cho các màn hình quản trị dạng "bảng danh sách": tải một lần, tạo/sửa xong thì tải lại
// status: 'idle' | 'loading' | 'succeeded' | 'failed'
export function createListSlice(name, loader) {
  const fetchList = createAsyncThunk(`${name}/fetchList`, loader)
  const initialState = { items: [], status: 'idle', error: null }
  const slice = createSlice({
    name,
    initialState,
    reducers: {},
    extraReducers: (builder) => {
      builder
        // Dữ liệu quản trị là riêng tư: đăng xuất thì xóa
        .addCase(loggedOut, () => initialState)
        .addCase(fetchList.pending, (state) => {
          state.status = 'loading'
          state.error = null
        })
        .addCase(fetchList.fulfilled, (state, action) => {
          state.status = 'succeeded'
          state.items = action.payload
        })
        .addCase(fetchList.rejected, (state, action) => {
          state.status = 'failed'
          state.error = action.error.message
        })
    },
  })
  return { fetchList, reducer: slice.reducer }
}
