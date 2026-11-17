import { useMemo } from 'react'
import { Button, Dropdown, Layout, Menu, Space, Tag, Typography } from 'antd'
import { HomeOutlined, LogoutOutlined, MedicineBoxOutlined, UserOutlined } from '@ant-design/icons'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import { menuForRole, ROLE_LABELS } from './appMenu'

export function AppLayout() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  // ProtectedRoute đã đảm bảo user có giá trị
  const role = user.vaiTro
  const items = useMemo(
    () => menuForRole(role).map((item) => ({ key: item.path, icon: item.icon, label: <Link to={item.path}>{item.label}</Link> })),
    [role],
  )

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Layout.Sider breakpoint="lg" collapsedWidth={0} width={240} theme="light">
        <div style={{ height: 64, display: 'flex', alignItems: 'center', gap: 8, padding: '0 24px' }}>
          <MedicineBoxOutlined style={{ fontSize: 24, color: '#1677ff' }} />
          <Typography.Text strong style={{ fontSize: 18 }}>Nha khoa</Typography.Text>
        </div>
        <Menu mode="inline" items={items} selectedKeys={[location.pathname]} />
      </Layout.Sider>
      <Layout>
        <Layout.Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', padding: '0 16px', background: '#fff' }}>
          <Space>
            <Tag color="blue">{ROLE_LABELS[role]}</Tag>
            <Dropdown
              menu={{
                items: [
                  { key: 'home', icon: <HomeOutlined />, label: 'Trang chủ', onClick: () => navigate('/') },
                  {
                    key: 'logout',
                    icon: <LogoutOutlined />,
                    label: 'Đăng xuất',
                    onClick: async () => {
                      await logout()
                      navigate('/dang-nhap')
                    },
                  },
                ],
              }}
            >
              <Button icon={<UserOutlined />}>{user.tenDangNhap}</Button>
            </Dropdown>
          </Space>
        </Layout.Header>
        <Layout.Content style={{ padding: 24 }}>
          <Outlet />
        </Layout.Content>
      </Layout>
    </Layout>
  )
}
