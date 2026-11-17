import { Button, Dropdown, Layout, Menu, Space, Typography } from 'antd'
import { LogoutOutlined, MedicineBoxOutlined, UserOutlined } from '@ant-design/icons'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import { ROLE_LABELS } from './appMenu'

const NAV_ITEMS = [
  { key: '/', label: <Link to="/">Trang chủ</Link> },
  { key: '/bac-si', label: <Link to="/bac-si">Bác sĩ</Link> },
  { key: '/dich-vu', label: <Link to="/dich-vu">Dịch vụ và bảng giá</Link> },
]

export function PublicLayout() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  // /bac-si/3 vẫn sáng mục "Bác sĩ"
  const selectedKey = NAV_ITEMS.map((item) => item.key)
    .filter((key) => (key === '/' ? location.pathname === '/' : location.pathname.startsWith(key)))
    .at(-1)

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Layout.Header style={{ display: 'flex', alignItems: 'center', gap: 16, padding: '0 16px', background: '#fff' }}>
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 8, whiteSpace: 'nowrap' }}>
          <MedicineBoxOutlined style={{ fontSize: 24, color: '#1677ff' }} />
          <Typography.Text strong style={{ fontSize: 18 }}>Nha khoa</Typography.Text>
        </Link>
        <Menu
          mode="horizontal"
          items={NAV_ITEMS}
          selectedKeys={selectedKey ? [selectedKey] : []}
          style={{ flex: 1, minWidth: 0, borderBottom: 'none' }}
        />
        {user ? (
          <Dropdown
            menu={{
              items: [
                { key: 'app', icon: <UserOutlined />, label: 'Vào hệ thống', onClick: () => navigate('/app') },
                {
                  key: 'logout',
                  icon: <LogoutOutlined />,
                  label: 'Đăng xuất',
                  onClick: async () => {
                    await logout()
                    navigate('/')
                  },
                },
              ],
            }}
          >
            <Button icon={<UserOutlined />}>{user.tenDangNhap} ({ROLE_LABELS[user.vaiTro]})</Button>
          </Dropdown>
        ) : (
          <Space>
            <Link to="/dang-nhap"><Button>Đăng nhập</Button></Link>
            <Link to="/dang-ky"><Button type="primary">Đăng ký</Button></Link>
          </Space>
        )}
      </Layout.Header>
      <Layout.Content style={{ padding: '24px 16px', maxWidth: 1200, width: '100%', margin: '0 auto' }}>
        <Outlet />
      </Layout.Content>
      <Layout.Footer style={{ textAlign: 'center' }}>Phòng khám nha khoa · Đồ án tốt nghiệp</Layout.Footer>
    </Layout>
  )
}
