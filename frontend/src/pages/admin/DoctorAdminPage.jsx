import { useEffect, useState } from 'react'
import { Alert, Avatar, Button, Form, Input, Select, Space, Table, Tag, Typography, message } from 'antd'
import { PlusOutlined, UserOutlined } from '@ant-design/icons'
import { useDispatch, useSelector } from 'react-redux'
import { adminApi } from '../../api/adminApi'
import { ApiError } from '../../api/client'
import { AccountFields } from '../../components/AccountFields'
import { AvatarUpload } from '../../components/AvatarUpload'
import { EntityFormModal } from '../../components/EntityFormModal'
import { LockAccountButton } from '../../components/LockAccountButton'
import { fetchDoctorsAdmin } from '../../store/doctorAdminSlice'

const WORK_STATUS = [
  { value: 'dang_lam', label: 'Đang làm' },
  { value: 'nghi_viec', label: 'Nghỉ việc' },
]

export function DoctorAdminPage() {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.doctorAdmin)
  // editing: null = đóng, {} = tạo mới, {...} = sửa
  const [editing, setEditing] = useState(null)

  useEffect(() => {
    dispatch(fetchDoctorsAdmin())
  }, [dispatch])

  const reload = () => dispatch(fetchDoctorsAdmin())

  async function handleSubmit(values) {
    if (editing.maBacSi) {
      await adminApi.updateDoctor(editing.maBacSi, values)
      message.success('Đã cập nhật bác sĩ')
    } else {
      await adminApi.createDoctor(values)
      message.success('Đã tạo bác sĩ')
    }
    setEditing(null)
    reload()
  }

  function handleFieldError(form, e) {
    if (e instanceof ApiError && e.code === 'USERNAME_ALREADY_EXISTS') {
      form.setFields([{ name: 'tenDangNhap', errors: [e.message] }])
      return true
    }
    if (e instanceof ApiError && e.code === 'INVALID_AVATAR_URL') {
      form.setFields([{ name: 'anhDaiDien', errors: [e.message] }])
      return true
    }
    return false
  }

  const columns = [
    { title: '', dataIndex: 'anhDaiDien', width: 64, render: (url) => <Avatar src={url} icon={<UserOutlined />} /> },
    { title: 'Họ tên', dataIndex: 'hoTen' },
    { title: 'Chuyên môn', dataIndex: 'chuyenMon', responsive: ['md'], render: (v) => v ?? '—' },
    { title: 'Số điện thoại', dataIndex: 'soDienThoai', responsive: ['lg'] },
    { title: 'Tên đăng nhập', dataIndex: 'tenDangNhap', responsive: ['lg'] },
    {
      title: 'Làm việc',
      dataIndex: 'trangThaiLamViec',
      render: (v) => (v === 'dang_lam' ? <Tag color="green">Đang làm</Tag> : <Tag>Nghỉ việc</Tag>),
    },
    {
      title: 'Tài khoản',
      dataIndex: 'trangThaiTaiKhoan',
      render: (v) => (v === 'khoa' ? <Tag color="red">Đã khóa</Tag> : <Tag color="blue">Hoạt động</Tag>),
    },
    {
      title: '',
      key: 'actions',
      render: (_, row) => (
        <Space size={0}>
          <Button type="link" onClick={() => setEditing(row)}>Sửa</Button>
          <LockAccountButton accountId={row.maTaiKhoan} locked={row.trangThaiTaiKhoan === 'khoa'} onDone={reload} />
        </Space>
      ),
    },
  ]

  const initialValues = editing?.maBacSi ? editing : { trangThaiLamViec: 'dang_lam' }

  return (
    <>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={2} style={{ margin: 0 }}>Bác sĩ</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setEditing({})}>Thêm bác sĩ</Button>
      </Space>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table rowKey="maBacSi" columns={columns} dataSource={items} loading={status === 'idle' || status === 'loading'} pagination={false} scroll={{ x: 'max-content' }} />

      <EntityFormModal
        title={editing?.maBacSi ? 'Sửa bác sĩ' : 'Thêm bác sĩ'}
        open={editing !== null}
        initialValues={initialValues}
        onCancel={() => setEditing(null)}
        onSubmit={handleSubmit}
        onFieldError={handleFieldError}
      >
        <Form.Item name="anhDaiDien" label="Ảnh đại diện">
          <AvatarUpload />
        </Form.Item>
        <AccountFields creating={!editing?.maBacSi} />
        <Form.Item name="chuyenMon" label="Chuyên môn" rules={[{ max: 100 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="bangCap" label="Bằng cấp" rules={[{ max: 255 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="gioiThieu" label="Giới thiệu">
          <Input.TextArea rows={4} />
        </Form.Item>
        {editing?.maBacSi && (
          <Form.Item name="trangThaiLamViec" label="Trạng thái làm việc" rules={[{ required: true }]}>
            <Select options={WORK_STATUS} />
          </Form.Item>
        )}
      </EntityFormModal>
    </>
  )
}
