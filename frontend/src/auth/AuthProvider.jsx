import { useCallback, useEffect, useMemo, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { authApi } from '../api/auth'
import { refreshSession, setSessionLostHandler, tokenStore } from '../api/client'
import { AuthContext } from './AuthContext'

export function AuthProvider({ children }) {
  const queryClient = useQueryClient()
  const [user, setUser] = useState(null)
  const [initializing, setInitializing] = useState(true)

  // Mở trang: dùng cookie refresh_token để khôi phục phiên, không có thì coi là khách
  useEffect(() => {
    let active = true
    refreshSession()
      .then((token) => active && setUser(token.taiKhoan))
      .catch(() => undefined)
      .finally(() => active && setInitializing(false))
    return () => {
      active = false
    }
  }, [])

  // Refresh thất bại giữa chừng: xóa phiên, ProtectedRoute sẽ đưa về trang đăng nhập
  useEffect(() => {
    setSessionLostHandler(() => {
      setUser(null)
      queryClient.clear()
    })
    return () => setSessionLostHandler(null)
  }, [queryClient])

  const login = useCallback(async (body) => {
    const token = await authApi.login(body)
    tokenStore.set(token.accessToken)
    setUser(token.taiKhoan)
    return token.taiKhoan
  }, [])

  const logout = useCallback(async () => {
    try {
      await authApi.logout()
    } finally {
      tokenStore.set(null)
      setUser(null)
      queryClient.clear()
    }
  }, [queryClient])

  const value = useMemo(() => ({ user, initializing, login, logout }), [user, initializing, login, logout])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
