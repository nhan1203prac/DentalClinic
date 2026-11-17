import { createContext } from 'react'

// Giá trị: { user, initializing, login(body), logout() }
// initializing = true khi đang khôi phục phiên từ refresh token lúc mở trang
export const AuthContext = createContext(null)
