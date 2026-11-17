import { Navigate, Route, Routes } from 'react-router-dom'
import { GuestOnly, ProtectedRoute } from './auth/ProtectedRoute'
import { useAuth } from './auth/useAuth'
import { APP_MENU, menuForRole } from './layouts/appMenu'
import { AppLayout } from './layouts/AppLayout'
import { PublicLayout } from './layouts/PublicLayout'
import { LoginPage } from './pages/auth/LoginPage'
import { RegisterPage } from './pages/auth/RegisterPage'
import { ComingSoonPage } from './pages/common/ComingSoonPage'
import { NotFoundPage } from './pages/common/NotFoundPage'
import { DoctorDetailPage } from './pages/public/DoctorDetailPage'
import { DoctorListPage } from './pages/public/DoctorListPage'
import { HomePage } from './pages/public/HomePage'
import { ServicePricePage } from './pages/public/ServicePricePage'

// Vào /app thì chuyển tới màn hình đầu tiên trong menu của vai trò
function AppHome() {
  const { user } = useAuth()
  const [first] = menuForRole(user.vaiTro)
  return <Navigate to={first.path} replace />
}

export default function App() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route index element={<HomePage />} />
        <Route path="bac-si" element={<DoctorListPage />} />
        <Route path="bac-si/:id" element={<DoctorDetailPage />} />
        <Route path="dich-vu" element={<ServicePricePage />} />
        <Route path="dang-nhap" element={<GuestOnly><LoginPage /></GuestOnly>} />
        <Route path="dang-ky" element={<GuestOnly><RegisterPage /></GuestOnly>} />
      </Route>

      <Route path="app" element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
        <Route index element={<AppHome />} />
        {APP_MENU.map((item) => (
          <Route
            key={item.path}
            path={item.path.replace('/app/', '')}
            element={<ProtectedRoute roles={item.roles}><ComingSoonPage title={item.label} /></ProtectedRoute>}
          />
        ))}
        <Route path="*" element={<NotFoundPage />} />
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
