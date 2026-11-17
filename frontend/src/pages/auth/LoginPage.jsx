import { useState } from 'react'
import { Alert, Button, Card, Form, Input, Typography } from 'antd'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/useAuth'
import { applyFieldErrors, errorMessage } from '../../utils/formErrors'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const redirectTo = location.state?.from ?? '/app'

  async function onFinish(values) {
    setSubmitting(true)
    setError(null)
    try {
      await login(values)
      navigate(redirectTo, { replace: true })
    } catch (e) {
      if (!applyFieldErrors(form, e)) {
        setError(errorMessage(e))
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card style={{ maxWidth: 420, margin: '32px auto' }}>
      <Typography.Title level={3} style={{ textAlign: 'center' }}>Đăng nhập</Typography.Title>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Form form={form} layout="vertical" onFinish={onFinish} requiredMark={false}>
        <Form.Item name="tenDangNhap" label="Tên đăng nhập" rules={[{ required: true, message: 'Nhập tên đăng nhập' }]}>
          <Input autoFocus autoComplete="username" />
        </Form.Item>
        <Form.Item name="matKhau" label="Mật khẩu" rules={[{ required: true, message: 'Nhập mật khẩu' }]}>
          <Input.Password autoComplete="current-password" />
        </Form.Item>
        <Button type="primary" htmlType="submit" block loading={submitting}>Đăng nhập</Button>
      </Form>
      <Typography.Paragraph style={{ marginTop: 16, marginBottom: 0, textAlign: 'center' }}>
        Chưa có tài khoản? <Link to="/dang-ky">Đăng ký</Link>
      </Typography.Paragraph>
    </Card>
  )
}
