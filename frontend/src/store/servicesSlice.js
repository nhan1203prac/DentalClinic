import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { publicApi } from '../api/publicApi'

export const fetchServices = createAsyncThunk('services/fetchList', () => publicApi.services())

// status: 'idle' | 'loading' | 'succeeded' | 'failed'
const servicesSlice = createSlice({
  name: 'services',
  initialState: { items: [], status: 'idle', error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchServices.pending, (state) => {
        state.status = 'loading'
        state.error = null
      })
      .addCase(fetchServices.fulfilled, (state, action) => {
        state.status = 'succeeded'
        state.items = action.payload
      })
      .addCase(fetchServices.rejected, (state, action) => {
        state.status = 'failed'
        state.error = action.error.message
      })
  },
})

export default servicesSlice.reducer
