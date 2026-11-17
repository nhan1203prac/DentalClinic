import { useState } from 'react'
import { Alert, Button, Card, Form, Input, Typography, message } from 'antd'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../api/auth'
import { ApiError } from '../../api/client'
import { applyFieldErrors, errorMessage } from '../../utils/formErrors'

export function RegisterPage() {
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  // xacNhanMatKhau chỉ để kiểm tra ở form, không gửi lên backend
  async function onFinish({ xacNhanMatKhau: _confirm, ...values }) {
    setSubmitting(true)
    setError(null)
    try {
      await authApi.register(values)
      message.success('Đăng ký thành công, hãy đăng nhập')
      navigate('/dang-nhap', { replace: true })
    } catch (e) {
      if (e instanceof ApiError && e.code === 'USERNAME_ALREADY_EXISTS') {
        form.setFields([{ name: 'tenDangNhap', errors: [e.message] }])
      } else if (!applyFieldErrors(form, e)) {
        setError(errorMessage(e))
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card style={{ maxWidth: 480, margin: '32px auto' }}>
      <Typography.Title level={3} style={{ textAlign: 'center' }}>Đăng ký tài khoản</Typography.Title>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Form form={form} layout="vertical" onFinish={onFinish} requiredMark={false}>
        <Form.Item
          name="tenDangNhap"
          label="Tên đăng nhập"
          rules={[
            { required: true, message: 'Nhập tên đăng nhập' },
            { min: 3, max: 50, message: 'Từ 3 đến 50 ký tự' },
            { pattern: /^[A-Za-z0-9._-]+$/, message: 'Chỉ gồm chữ không dấu, số, dấu . _ -' },
          ]}
        >
          <Input autoComplete="username" />
        </Form.Item>
        <Form.Item
          name="matKhau"
          label="Mật khẩu"
          rules={[{ required: true, message: 'Nhập mật khẩu' }, { min: 6, max: 72, message: 'Từ 6 đến 72 ký tự' }]}
        >
          <Input.Password autoComplete="new-password" />
        </Form.Item>
        <Form.Item
          name="xacNhanMatKhau"
          label="Nhập lại mật khẩu"
          dependencies={['matKhau']}
          rules={[
            { required: true, message: 'Nhập lại mật khẩu' },
            ({ getFieldValue }) => ({
              validator: (_, value) =>
                !value || getFieldValue('matKhau') === value
                  ? Promise.resolve()
                  : Promise.reject(new Error('Mật khẩu nhập lại không khớp')),
            }),
          ]}
        >
          <Input.Password autoComplete="new-password" />
        </Form.Item>
        <Form.Item
          name="soDienThoai"
          label="Số điện thoại"
          rules={[{ required: true, message: 'Nhập số điện thoại' }, { pattern: /^[0-9]{9,15}$/, message: 'Gồm 9 đến 15 chữ số' }]}
        >
          <Input inputMode="tel" autoComplete="tel" />
        </Form.Item>
        <Form.Item name="email" label="Email (không bắt buộc)" rules={[{ type: 'email', message: 'Email không hợp lệ' }, { max: 100 }]}>
          <Input autoComplete="email" />
        </Form.Item>
        <Button type="primary" htmlType="submit" block loading={submitting}>Đăng ký</Button>
      </Form>
      <Typography.Paragraph style={{ marginTop: 16, marginBottom: 0, textAlign: 'center' }}>
        Đã có tài khoản? <Link to="/dang-nhap">Đăng nhập</Link>
      </Typography.Paragraph>
    </Card>
  )
}
