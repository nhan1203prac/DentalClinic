import { Form, Input } from 'antd'

// Ô nhập chung của nhân viên/bác sĩ. Tạo mới có thêm tên đăng nhập + mật khẩu; sửa thì không (BR-04)
export function AccountFields({ creating }) {
  return (
    <>
      {creating && (
        <>
          <Form.Item name="tenDangNhap" label="Tên đăng nhập" rules={[
            { required: true, message: 'Nhập tên đăng nhập' },
            { min: 3, max: 50, message: 'Từ 3 đến 50 ký tự' },
            { pattern: /^[A-Za-z0-9._-]+$/, message: 'Chỉ gồm chữ không dấu, số, dấu . _ -' },
          ]}>
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item name="matKhau" label="Mật khẩu" rules={[{ required: true, message: 'Nhập mật khẩu' }, { min: 6, max: 72, message: 'Từ 6 đến 72 ký tự' }]}>
            <Input.Password autoComplete="new-password" />
          </Form.Item>
        </>
      )}
      <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, whitespace: true, message: 'Nhập họ tên' }, { max: 100 }]}>
        <Input />
      </Form.Item>
      <Form.Item name="soDienThoai" label="Số điện thoại" rules={[{ required: true, message: 'Nhập số điện thoại' }, { pattern: /^[0-9]{9,15}$/, message: 'Gồm 9 đến 15 chữ số' }]}>
        <Input inputMode="tel" />
      </Form.Item>
      <Form.Item name="email" label="Email" rules={[{ type: 'email', message: 'Email không hợp lệ' }, { max: 100 }]}>
        <Input />
      </Form.Item>
    </>
  )
}
