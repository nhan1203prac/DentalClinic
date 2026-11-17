import { Button, Result, Spin } from 'antd'
import { Link, Navigate, useLocation } from 'react-router-dom'
import { useAuth } from './useAuth'

// roles bỏ trống: chỉ cần đã đăng nhập
export function ProtectedRoute({ roles, children }) {
  const { user, initializing } = useAuth()
  const location = useLocation()

  if (initializing) {
    return <Spin fullscreen />
  }
  if (!user) {
    return <Navigate to="/dang-nhap" replace state={{ from: location.pathname }} />
  }
  if (roles && !roles.includes(user.vaiTro)) {
    return (
      <Result
        status="403"
        title="403"
        subTitle="Bạn không có quyền truy cập trang này"
        extra={
          <Link to="/app">
            <Button type="primary">Về trang làm việc</Button>
          </Link>
        }
      />
    )
  }
  return <>{children}</>
}

// Trang đăng nhập/đăng ký: đã đăng nhập thì chuyển vào ứng dụng
export function GuestOnly({ children }) {
  const { user, initializing } = useAuth()
  if (initializing) {
    return <Spin fullscreen />
  }
  return user ? <Navigate to="/app" replace /> : <>{children}</>
}
