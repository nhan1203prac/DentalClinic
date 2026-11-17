import axios from 'axios'

// Access token chỉ giữ trong bộ nhớ; refresh token nằm trong cookie HttpOnly do backend đặt
let accessToken = null
let onSessionLost = null

export const tokenStore = {
  get: () => accessToken,
  set: (token) => {
    accessToken = token
  },
}

// AuthProvider đăng ký hàm này để biết khi phiên hết hạn hẳn (refresh thất bại)
export function setSessionLostHandler(handler) {
  onSessionLost = handler
}

export class ApiError extends Error {
  constructor(code, message, status, fields) {
    super(message)
    this.code = code
    this.status = status
    this.fields = fields
  }
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  withCredentials: true,
})

http.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

let refreshing = null

// Nhiều request cùng gặp 401 chỉ gọi refresh một lần
export function refreshSession() {
  refreshing ??= http
    .post('/auth/refresh')
    .then((res) => {
      const token = res.data.result
      accessToken = token.accessToken
      return token
    })
    .finally(() => {
      refreshing = null
    })
  return refreshing
}

function toApiError(error) {
  const body = error.response?.data
  if (!body?.code) {
    return new ApiError('NETWORK_ERROR', 'Không kết nối được máy chủ', error.response?.status)
  }
  const fields = body.code === 'VALIDATION_ERROR' ? body.result : undefined
  return new ApiError(body.code, body.message, error.response?.status, fields)
}

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error instanceof ApiError) {
      throw error
    }
    const original = error.config
    const isAuthCall = original?.url?.startsWith('/auth/')
    if (error.response?.status === 401 && original && !original._retry && !isAuthCall) {
      original._retry = true
      try {
        await refreshSession()
      } catch {
        accessToken = null
        onSessionLost?.()
        throw toApiError(error)
      }
      return http(original)
    }
    throw toApiError(error)
  },
)

// Trả thẳng phần result của ApiResponse { code, message, result }
export async function request(config) {
  const res = await http.request(config)
  return res.data.result
}
