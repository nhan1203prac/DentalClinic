import { useEffect, useState } from 'react'
import { Alert, Button, DatePicker, Form, Select, Space, Table, Tag, Typography, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { adminApi } from '../../api/adminApi'
import { ApiError } from '../../api/client'
import { AccountFields } from '../../components/AccountFields'
import { EntityFormModal } from '../../components/EntityFormModal'
import { LockAccountButton } from '../../components/LockAccountButton'
import { fetchStaff } from '../../store/staffSlice'

const WORK_STATUS = [
  { value: 'dang_lam', label: 'Đang làm' },
  { value: 'nghi_viec', label: 'Nghỉ việc' },
]

// Form lưu ngày dạng dayjs, backend nhận yyyy-MM-dd
function toPayload({ ngayVaoLam, ...rest }) {
  return { ...rest, ngayVaoLam: ngayVaoLam ? ngayVaoLam.format('YYYY-MM-DD') : null }
}

export function StaffPage() {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.staff)
  // editing: null = đóng, {} = tạo mới, {...} = sửa
  const [editing, setEditing] = useState(null)

  useEffect(() => {
    dispatch(fetchStaff())
  }, [dispatch])

  const reload = () => dispatch(fetchStaff())

  async function handleSubmit(values) {
    if (editing.maNhanVien) {
      await adminApi.updateStaff(editing.maNhanVien, toPayload(values))
      message.success('Đã cập nhật nhân viên')
    } else {
      await adminApi.createStaff(toPayload(values))
      message.success('Đã tạo nhân viên')
    }
    setEditing(null)
    reload()
  }

  function handleFieldError(form, e) {
    if (e instanceof ApiError && e.code === 'USERNAME_ALREADY_EXISTS') {
      form.setFields([{ name: 'tenDangNhap', errors: [e.message] }])
      return true
    }
    return false
  }

  const columns = [
    { title: 'Họ tên', dataIndex: 'hoTen' },
    { title: 'Tên đăng nhập', dataIndex: 'tenDangNhap', responsive: ['md'] },
    { title: 'Số điện thoại', dataIndex: 'soDienThoai' },
    { title: 'Email', dataIndex: 'email', responsive: ['lg'], render: (v) => v ?? '—' },
    { title: 'Ngày vào làm', dataIndex: 'ngayVaoLam', responsive: ['lg'], render: (v) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
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

  const initialValues = editing?.maNhanVien
    ? { ...editing, ngayVaoLam: editing.ngayVaoLam ? dayjs(editing.ngayVaoLam) : null }
    : { trangThaiLamViec: 'dang_lam' }

  return (
    <>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={2} style={{ margin: 0 }}>Nhân viên</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setEditing({})}>Thêm nhân viên</Button>
      </Space>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table rowKey="maNhanVien" columns={columns} dataSource={items} loading={status === 'idle' || status === 'loading'} pagination={false} scroll={{ x: 'max-content' }} />

      <EntityFormModal
        title={editing?.maNhanVien ? 'Sửa nhân viên' : 'Thêm nhân viên'}
        open={editing !== null}
        initialValues={initialValues}
        onCancel={() => setEditing(null)}
        onSubmit={handleSubmit}
        onFieldError={handleFieldError}
      >
        <AccountFields creating={!editing?.maNhanVien} />
        <Form.Item name="ngayVaoLam" label="Ngày vào làm">
          <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
        </Form.Item>
        {editing?.maNhanVien && (
          <Form.Item name="trangThaiLamViec" label="Trạng thái làm việc" rules={[{ required: true }]}>
            <Select options={WORK_STATUS} />
          </Form.Item>
        )}
      </EntityFormModal>
    </>
  )
}
