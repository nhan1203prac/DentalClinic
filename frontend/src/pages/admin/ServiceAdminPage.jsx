import { useEffect, useState } from 'react'
import { Alert, Button, Form, Input, InputNumber, Select, Space, Table, Tag, Typography, message } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useDispatch, useSelector } from 'react-redux'
import { adminApi } from '../../api/adminApi'
import { EntityFormModal } from '../../components/EntityFormModal'
import { useAuth } from '../../auth/useAuth'
import { fetchServicesAdmin } from '../../store/serviceAdminSlice'
import { formatVnd } from '../../utils/format'

const STATUS = [
  { value: 'kich_hoat', label: 'Đang áp dụng' },
  { value: 'ngung', label: 'Ngưng' },
]

export function ServiceAdminPage() {
  const dispatch = useDispatch()
  const { user } = useAuth()
  const { items, status, error } = useSelector((state) => state.serviceAdmin)
  // Nhân viên chỉ được thêm; sửa và ngưng dịch vụ là quyền chủ phòng khám
  const canEdit = user.vaiTro === 'chu_phong_kham'
  // editing: null = đóng, {} = tạo mới, {...} = sửa
  const [editing, setEditing] = useState(null)

  useEffect(() => {
    dispatch(fetchServicesAdmin())
  }, [dispatch])

  async function handleSubmit(values) {
    if (editing.maDichVu) {
      await adminApi.updateService(editing.maDichVu, values)
      message.success('Đã cập nhật dịch vụ')
    } else {
      await adminApi.createService(values)
      message.success('Đã thêm dịch vụ')
    }
    setEditing(null)
    dispatch(fetchServicesAdmin())
  }

  const columns = [
    {
      title: 'Dịch vụ',
      dataIndex: 'tenDichVu',
      render: (name, row) => (
        <>
          <Typography.Text strong>{name}</Typography.Text>
          {row.moTa && <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>{row.moTa}</Typography.Paragraph>}
        </>
      ),
    },
    { title: 'Thời lượng', dataIndex: 'thoiLuong', responsive: ['sm'], render: (v) => (v ? `${v} phút` : '—') },
    { title: 'Giá', dataIndex: 'gia', align: 'right', render: formatVnd },
    {
      title: 'Trạng thái',
      dataIndex: 'trangThai',
      render: (v) => (v === 'kich_hoat' ? <Tag color="green">Đang áp dụng</Tag> : <Tag>Ngưng</Tag>),
    },
    ...(canEdit
      ? [{ title: '', key: 'actions', render: (_, row) => <Button type="link" onClick={() => setEditing(row)}>Sửa</Button> }]
      : []),
  ]

  return (
    <>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={2} style={{ margin: 0 }}>Dịch vụ</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setEditing({})}>Thêm dịch vụ</Button>
      </Space>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table rowKey="maDichVu" columns={columns} dataSource={items} loading={status === 'idle' || status === 'loading'} pagination={false} />

      <EntityFormModal
        title={editing?.maDichVu ? 'Sửa dịch vụ' : 'Thêm dịch vụ'}
        open={editing !== null}
        initialValues={editing?.maDichVu ? editing : undefined}
        onCancel={() => setEditing(null)}
        onSubmit={handleSubmit}
      >
        <Form.Item name="tenDichVu" label="Tên dịch vụ" rules={[{ required: true, whitespace: true, message: 'Nhập tên dịch vụ' }, { max: 150 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="moTa" label="Mô tả">
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item name="gia" label="Giá (VNĐ)" rules={[{ required: true, message: 'Nhập giá' }]}>
          <InputNumber min={0} precision={0} step={10000} style={{ width: '100%' }}
            formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, '.')} parser={(v) => v.replace(/\./g, '')} />
        </Form.Item>
        <Form.Item name="thoiLuong" label="Thời lượng (phút)">
          <InputNumber min={1} precision={0} style={{ width: '100%' }} />
        </Form.Item>
        {editing?.maDichVu && (
          <Form.Item name="trangThai" label="Trạng thái" rules={[{ required: true }]}>
            <Select options={STATUS} />
          </Form.Item>
        )}
      </EntityFormModal>
    </>
  )
}
