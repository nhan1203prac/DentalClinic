import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { publicApi } from '../api/publicApi'

export const fetchDoctors = createAsyncThunk('doctors/fetchList', () => publicApi.doctors())
export const fetchDoctor = createAsyncThunk('doctors/fetchDetail', (id) => publicApi.doctor(id))

// status: 'idle' | 'loading' | 'succeeded' | 'failed'
const initialState = {
  list: { items: [], status: 'idle', error: null },
  detail: { item: null, status: 'idle', error: null, errorCode: null },
}

const doctorsSlice = createSlice({
  name: 'doctors',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchDoctors.pending, (state) => {
        state.list.status = 'loading'
        state.list.error = null
      })
      .addCase(fetchDoctors.fulfilled, (state, action) => {
        state.list.status = 'succeeded'
        state.list.items = action.payload
      })
      .addCase(fetchDoctors.rejected, (state, action) => {
        state.list.status = 'failed'
        state.list.error = action.error.message
      })
      .addCase(fetchDoctor.pending, (state) => {
        state.detail = { item: null, status: 'loading', error: null, errorCode: null }
      })
      .addCase(fetchDoctor.fulfilled, (state, action) => {
        state.detail.status = 'succeeded'
        state.detail.item = action.payload
      })
      .addCase(fetchDoctor.rejected, (state, action) => {
        state.detail.status = 'failed'
        state.detail.error = action.error.message
        state.detail.errorCode = action.error.code ?? null
      })
  },
})

export default doctorsSlice.reducer
